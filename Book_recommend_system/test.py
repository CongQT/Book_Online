from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sqlalchemy import create_engine, MetaData, text
import numpy as np
from sklearn.metrics import mean_squared_error
import json
import logging
import requests
from fastapi.middleware.cors import CORSMiddleware

# Database connection
DB_URL = "mysql+pymysql://root:123456@localhost:3306/book"
engine = create_engine(DB_URL)
metadata = MetaData()

# FastAPI app
app = FastAPI()

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Cho phép tất cả các nguồn gốc (frontend URLs). Cần hạn chế trong môi trường production.
    allow_credentials=True,
    allow_methods=["*"],  # Cho phép tất cả các phương thức HTTP
    allow_headers=["*"],  # Cho phép tất cả các header
)

# Models for Pydantic
class RecommendRequest(BaseModel):
    user_id: int
    top_n: int = 10

class UpdateModelRequest(BaseModel):
    feedback: list[dict]  # [{user_id, book_id, rating}]

# Matrix Factorization
class MF:
    def __init__(self, Y, n_factors=2, X = None, W = None, lamda=0.1, lr=0.01, n_epochs=20):
        if Y is not None and Y.size > 0:  # Nếu có dữ liệu Y
            self.users = int(np.max(Y[:, 0])) + 1
            self.items = int(np.max(Y[:, 1])) + 1
        elif X is not None and W is not None:  # Nếu có dữ liệu X và W
            self.users = W.shape[1]
            self.items = X.shape[0]
        else:
            raise ValueError("Không thể khởi tạo MF nếu thiếu thông tin Y hoặc X và W.")

        self.Y = Y if Y is not None else np.empty((0, 3))  # Khởi tạo Y là mảng rỗng nếu Y là None
        self.n_factors = n_factors
        self.lamda = lamda
        self.lr = lr
        self.n_epochs = n_epochs

        self.X = X if X is not None else np.random.randn(self.items, n_factors)
        self.W = W if W is not None else np.random.randn(n_factors, self.users)
    def get_user_rated(self, user_id):
        ids = np.where(self.Y[:, 0] == user_id)[0]
        return self.Y[ids, 1].astype(int), self.Y[ids, 2]

    def get_item_rated(self, book_id):
        ids = np.where(self.Y[:, 1] == book_id)[0]
        return self.Y[ids, 0].astype(int), self.Y[ids, 2]

    def train(self):
        for epoch in range(self.n_epochs):
            for book_id in range(self.items):
                users, ratings = self.get_item_rated(book_id)
                if len(users) == 0:
                    continue
                error = ratings - self.X[book_id, :].dot(self.W[:, users])
                grad_x = -error.dot(self.W[:, users].T) / len(users) + self.lamda * self.X[book_id, :]
                self.X[book_id, :] -= self.lr * grad_x

            for user_id in range(self.users):
                items, ratings = self.get_user_rated(user_id)
                if len(items) == 0:
                    continue
                error = ratings - self.X[items, :].dot(self.W[:, user_id])
                grad_w = -self.X[items, :].T.dot(error) / len(items) + self.lamda * self.W[:, user_id]
                self.W[:, user_id] -= self.lr * grad_w

    def predict(self, user_id, book_id):
        return max(0, min(5, self.X[book_id, :].dot(self.W[:, user_id])))
    def recommend(self, user_id, top_n=10):
        rated_items, _ = self.get_user_rated(user_id)
        predictions = np.array([self.predict(user_id, i) for i in range(self.items)])
        predictions[rated_items] = -1  # Exclude already rated items
        # Log predictions and rated items to debug
        logging.warning(f"Predictions: {predictions}")
        logging.warning(f"Rated items: {rated_items}")
        recommended_items = predictions.argsort()[-top_n:][::-1]
        logging.debug(f"Recommended items: {recommended_items}")
        return recommended_items

    def rmse(self, test_data):
        predictions = [self.predict(int(u), int(i)) for u, i, _ in test_data]
        true_ratings = test_data[:, 2]
        return np.sqrt(mean_squared_error(true_ratings, predictions))


# API: Recommend
@app.post("/recommend/")
def recommend(data: RecommendRequest):
    user_id = data.user_id
    top_n = data.top_n

    with engine.connect() as conn:
       
        # Lấy mô hình từ cơ sở dữ liệu
        x_result = conn.execute(text("SELECT * FROM model_x ORDER BY book_id")).mappings().fetchall()
        w_result = conn.execute(text("SELECT * FROM model_w ORDER BY user_id")).mappings().fetchall()

        # Nếu không có dữ liệu mô hình, huấn luyện lại từ đầu
        if not x_result or not w_result:
            logging.warning("No pre-trained model found. Training from scratch.")
            
            # Lấy dữ liệu feedback từ cơ sở dữ liệu
            feedback = conn.execute(text("SELECT user_id, book_id, rating FROM feedback")).fetchall()
            if not feedback:
                raise HTTPException(status_code=404, detail="No feedback data available for training.")
            
            Y = np.array(feedback)
            model = MF(Y=Y, n_factors=10)
            model.train()
            try:
                # Bắt đầu giao dịch
                
                    # Lưu mô hình X vào cơ sở dữ liệu
                    for i, factors in enumerate(model.X):
                        conn.execute(
                            text("INSERT INTO model_x (book_id, factors) VALUES (:book_id, :factors)"),
                            {"book_id": i, "factors": json.dumps(factors.tolist())}
                        )

                    # Lưu mô hình W vào cơ sở dữ liệu
                    for u, factors in enumerate(model.W.T):
                        conn.execute(
                            text("INSERT INTO model_w (user_id, factors) VALUES (:user_id, :factors)"),
                            {"user_id": u, "factors": json.dumps(factors.tolist())}
                        )
                    conn.commit()
            except Exception as e:
                logging.error(f"Error during transaction: {e}")
                raise HTTPException(status_code=500, detail="Failed to update model.")

            # Sử dụng mô hình mới huấn luyện để gợi ý
            recommended_books = model.recommend(user_id=user_id, top_n=top_n).tolist()
         # Lấy thông tin chi tiết của các sách từ API
            recommended_books_with_info = []
        
            for book_id in recommended_books:
                book_info = get_book_info(book_id)  # Gọi API để lấy thông tin chi tiết của sách
                # Kiểm tra xem "author" có tồn tại trong dữ liệu không
                author_info = book_info["data"].get("author", None)
                
                # Nếu "author" không phải là None, tiếp tục truy cập thông tin
                if author_info is not None:
                    author = {
                        "id": author_info["id"],
                        "name": author_info["name"],
                        "created_at": author_info["created_at"],
                        "updated_at": author_info["updated_at"]
                    }
                else:
                    # Nếu không có thông tin tác giả, bạn có thể để trống hoặc xử lý khác
                    author = {}
                recommended_books_with_info.append({
                    "book_id": book_info["data"]["id"],  # Giả sử API trả về dữ liệu dưới dạng đối tượng sách
                    "title": book_info["data"]["title"],
                    "summary":book_info["data"]["summary"],
                    "avg_rating":book_info["data"]["avg_rating"],
                    "thumbnail_url":book_info["data"]["thumbnail_url"],
                    "view":book_info["data"]["view"],
                    "status":book_info["data"]["status"],
                    "created_at":book_info["data"]["created_at"],
                    "updated_at":book_info["data"]["updated_at"],
                    "author":author,
                    "category_book":[
                        {
                            "category_id":category["category_id"],
                            "category_name":category["category_name"]
                        }
                        for category in book_info["data"]["category_book"]
                    ]
                })

            response = {
                "user_id": user_id,
                "recommended_books": recommended_books_with_info,
                "message": "Recommendation successful."
            }
            return response

        # Nếu có dữ liệu mô hình, sử dụng mô hình đó
        X = np.array([json.loads(row['factors']) for row in x_result])
        W = np.array([json.loads(row['factors']) for row in w_result]).T
    
        model = MF(Y=None, n_factors=X.shape[1], X=X, W=W)  # Khởi tạo MF mà không cần dữ liệu feedback
        model.X = X
        model.W = W
        
        logging.warning(f"Model X: {model}")
        logging.warning(f"Model W: {model.W}")
        # Kiểm tra kích thước của mô hình
        feedback = conn.execute(text("SELECT user_id, book_id, rating FROM feedback")).fetchall()
        Y = np.array(feedback)
        if X.shape[0] <= np.max(Y[:, 1]) or W.shape[1] <= np.max(Y[:, 0]):
            feedback_to_update = [{"user_id": int(row[0]), "book_id": int(row[1]), "rating": float(row[2])} for row in feedback]
            response = requests.post(
                f"http://localhost:5500/update-model/",
                json={"feedback": feedback_to_update}
            )
            if response.status_code != 200:
                raise HTTPException(status_code=500, detail="Failed to update model.")
            logging.warning("Model updated successfully.")
        # Gợi ý sách
        recommended_books = model.recommend(user_id=user_id, top_n=top_n).tolist()
         # Lấy thông tin chi tiết của các sách từ API
        recommended_books_with_info = []
    
        for book_id in recommended_books:
            if book_id != 0:
                book_info = get_book_info(book_id)  # Gọi API để lấy thông tin chi tiết của sách
                # Kiểm tra xem "author" có tồn tại trong dữ liệu không
                if book_info is not None:
                    author_info = book_info["data"].get("author", None)
                    
                    # Nếu "author" không phải là None, tiếp tục truy cập thông tin
                    if author_info is not None:
                        author = {
                            "id": author_info["id"],
                            "name": author_info["name"],
                            "created_at": author_info["created_at"],
                            "updated_at": author_info["updated_at"]
                        }
                    else:
                        # Nếu không có thông tin tác giả, bạn có thể để trống hoặc xử lý khác
                        author = {}
                    recommended_books_with_info.append({
                        "book_id": book_info["data"]["id"],  # Giả sử API trả về dữ liệu dưới dạng đối tượng sách
                        "title": book_info["data"]["title"],
                        "summary":book_info["data"]["summary"],
                        "avg_rating":book_info["data"]["avg_rating"],
                        "thumbnail_url":book_info["data"]["thumbnail_url"],
                        "view":book_info["data"]["view"],
                        "status":book_info["data"]["status"],
                        "created_at":book_info["data"]["created_at"],
                        "updated_at":book_info["data"]["updated_at"],
                        "author":author,
                        "category_book":[
                            {
                                "category_id":category["category_id"],
                                "category_name":category["category_name"]
                            }
                            for category in book_info["data"]["category_book"]
                        ]
                    })

        response = {
            "user_id": user_id,
            "recommended_books": recommended_books_with_info,
            "message": "Recommendation successful."
        }
        return response
# API: Update Model
@app.post("/update-model/")
def update_model(data: UpdateModelRequest):
    feedback = np.array([[f["user_id"], f["book_id"], f["rating"]] for f in data.feedback])

    if feedback.size == 0:
        raise HTTPException(status_code=400, detail="No feedback provided.")

    model = MF(Y=feedback, n_factors=10)
    model.train()

    # Save updated model to DB
    with engine.connect() as conn:
        try:
            # Bắt đầu giao dịch
                # Xóa dữ liệu cũ
                conn.execute(text("DELETE FROM model_x"))
                conn.execute(text("DELETE FROM model_w"))

                # Lưu mô hình X vào cơ sở dữ liệu
                for i, factors in enumerate(model.X):
                    conn.execute(
                        text("INSERT INTO model_x (book_id, factors) VALUES (:book_id, :factors)"),
                        {"book_id": i, "factors": json.dumps(factors.tolist())}
                    )

                # Lưu mô hình W vào cơ sở dữ liệu
                for u, factors in enumerate(model.W.T):
                    conn.execute(
                        text("INSERT INTO model_w (user_id, factors) VALUES (:user_id, :factors)"),
                        {"user_id": u, "factors": json.dumps(factors.tolist())}
                    )
                conn.commit()
        except Exception as e:
            logging.error(f"Error during transaction: {e}")
            raise HTTPException(status_code=500, detail="Failed to update model.")

    return {"message": "Model updated successfully."}

# Gọi API lấy thông tin sách
def get_book_info(book_id):
    # Gửi yêu cầu GET tới API để lấy thông tin các sách
    API_URL = "http://localhost:8080/public/book/info/"
    url = f"{API_URL}{book_id}"
    
    # Gửi yêu cầu GET tới API để lấy thông tin sách
    response = requests.get(url)
    if response.status_code != 200:
        return None
    return response.json()