from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sqlalchemy import create_engine, MetaData, text
import numpy as np
from sklearn.metrics import mean_squared_error
import uvicorn
import logging
import requests
from fastapi.middleware.cors import CORSMiddleware
import asyncio
from update_model import update_model_task
from apscheduler.schedulers.background import BackgroundScheduler

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

# API: Recommend
@app.post("/recommend/")
def recommend(data: RecommendRequest):
    user_id = data.user_id
    top_n = data.top_n

    with engine.connect() as conn:
       
            # Lấy dự đoán từ bảng predictions
            result = conn.execute(
                text("SELECT book_id, predicted_rating FROM predictions WHERE user_id = :user_id"),
                {"user_id": user_id}
            ).fetchall()

            if not result:
                raise HTTPException(status_code=404, detail="No recommendations found for this user.")
            # Lấy top N sách có predicted_rating cao nhất
            sorted_books = sorted(result, key=lambda x: x[1], reverse=True)[:top_n]
            recommended_books = [book[0] for book in sorted_books]  # Truy cập bằng index

            # Lấy thông tin chi tiết của các sách từ API
            recommended_books_with_info = []
        
            for book_id in recommended_books:
                book_info = get_book_info(book_id)  # Gọi API để lấy thông tin chi tiết của sách
                if not book_info or "data" not in book_info:
                    logging.warning(f"Failed to fetch data for book ID {book_id}")
                    continue
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
                    "author":{
                            "id": book_info["data"]["author"]["id"],
                            "name": book_info["data"]["author"]["name"],
                            "created_at": book_info["data"]["author"]["created_at"],
                            "updated_at": book_info["data"]["author"]["updated_at"]
                    },
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

def get_book_info(book_id):
    # Gửi yêu cầu GET tới API để lấy thông tin các sách
    API_URL = "http://localhost:8080/public/book/info/"
    url = f"{API_URL}{book_id}"
    
    # Gửi yêu cầu GET tới API để lấy thông tin sách
    response = requests.get(url)
    if response.status_code != 200:
        return None
    return response.json()

# Scheduler function
def start_scheduler():
    scheduler = BackgroundScheduler()
    scheduler.add_job(update_model_task, 'interval', minutes=10)  # Đặt lịch chạy mỗi 10 phút
    scheduler.start()

@app.on_event("startup")
def startup_event():
    start_scheduler()

# Khởi động FastAPI và Scheduler trong vòng lặp sự kiện
if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=5500)