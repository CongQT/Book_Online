from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sqlalchemy import create_engine, MetaData, text
import numpy as np
from sklearn.metrics import mean_squared_error
import json
import logging
import requests
from fastapi.middleware.cors import CORSMiddleware
from sklearn.model_selection import train_test_split
import mysql.connector
import pandas as pd
from scipy.sparse import lil_matrix

# Database connection
DB_URL = "mysql+pymysql://root:123456@localhost:3306/book"
engine = create_engine(DB_URL)
metadata = MetaData()

# Matrix Factorization
class MF(object):
    def __init__(self, Y, n_factors=2, X=None, W=None, lamda=0.1, lr=2, n_epochs=50, 
                 top=10, filename=None):
        if filename:
            self.f = open(filename, 'a+')
        self.Y = Y
        self.lamda = lamda
        self.n_factors = n_factors
        self.lr = lr
        self.n_epochs = n_epochs
        self.top = top
        self.users_count = int(np.max(self.Y[:, 0])) + 1
        self.items_count = int(np.max(self.Y[:, 1])) + 1
        self.ratings_count = Y.shape[0]
        if X is None:
            self.X = np.random.randn(self.items_count, n_factors)
        if W is None:
            self.W = np.random.randn(n_factors, self.users_count)
        self.Ybar = self.Y.copy()

        self.bi = np.random.randn(self.items_count)
        self.bu = np.random.randn(self.users_count)
        self.n_ratings = self.Y.shape[0]

    def get_user_rated_item(self, i):
        ids = np.where(i == self.Ybar[:, 1])[0].astype(int)
        users = self.Ybar[ids, 0].astype(int)
        ratings = self.Ybar[ids, 2]
        return (users, ratings)

    def get_item_rated_by_user(self, u):
        ids = np.where(u == self.Ybar[:, 0])[0].astype(int)
        items = self.Ybar[ids, 1].astype(int)
        ratings = self.Ybar[ids, 2]
        return (items, ratings)

    def updateX(self):
        for m in range(self.items_count):
            users, ratings = self.get_user_rated_item(m)
            Wm = self.W[:, users]
            b = self.bu[users]
            sum_grad_xm = np.full(shape=(self.X[m].shape), fill_value=1e-8)
            sum_grad_bm = 1e-8
            for i in range(50):
                xm = self.X[m]
                error = xm.dot(Wm) + self.bi[m] + b - ratings
                grad_xm = error.dot(Wm.T) / self.n_ratings + self.lamda * xm
                grad_bm = np.sum(error) / self.n_ratings
                sum_grad_xm += grad_xm**2
                sum_grad_bm += grad_bm**2
                # gradient descent
                self.X[m] -= self.lr * grad_xm.reshape(-1) / np.sqrt(sum_grad_xm)
                self.bi[m] -= self.lr * grad_bm / np.sqrt(sum_grad_bm)

    def updateW(self):
        for n in range(self.users_count):
            items, ratings = self.get_item_rated_by_user(n)
            Xn = self.X[items, :]
            b = self.bi[items]
            sum_grad_wn = np.full(shape=(self.W[:, n].shape), fill_value=1e-8).T
            sum_grad_bn = 1e-8
            for i in range(50):
                wn = self.W[:, n]
                error = Xn.dot(wn) + self.bu[n] + b - ratings
                grad_wn = Xn.T.dot(error) / self.n_ratings + self.lamda * wn
                grad_bn = np.sum(error) / self.n_ratings
                sum_grad_wn += grad_wn**2
                sum_grad_bn += grad_bn**2
                # gradient descent
                self.W[:, n] -= self.lr * grad_wn.reshape(-1) / np.sqrt(sum_grad_wn)
                self.bu[n] -= self.lr * grad_bn / np.sqrt(sum_grad_bn)

    def fit(self, x, data_size, Data_test, test_size=0):
        for i in range(self.n_epochs):
            self.updateW()
            self.updateX()
            if (i + 1) % x == 0:
                self.RMSE(Data_test, data_size=data_size, test_size=0, p=i + 1)

    def pred(self, u, i):
        u = int(u)
        i = int(i)
        pred = self.X[i, :].dot(self.W[:, u]) + self.bi[i] + self.bu[u]
        return max(0, min(5, pred))

    def recommend(self):
        # Duyệt qua tất cả các người dùng
        recommendations = {}
        
        for u in range(self.users_count):
            # Lấy các vật phẩm mà người dùng u đã đánh giá
            ids = np.where((self.Y[:, 0] == u) & (self.Y[:, 2] != 0))[0].astype(int)
            items_rated_by_user = self.Y[ids, 1].tolist()
            print("items:", items_rated_by_user)
            # Dự đoán điểm cho tất cả các vật phẩm
            pred = self.X.dot(self.W[:, u])

            # Khởi tạo mảng các điểm số cho tất cả các vật phẩm
            a = np.zeros((self.items_count,))

            # Tính điểm cho từng vật phẩm chưa đánh giá
            for i in range(self.items_count):
                if i not in items_rated_by_user:
                    a[i] = pred[i] + self.bi[i] + self.bu[u]
            # Lưu kết quả dự đoán cho người dùng u
            recommendations[u] = a

        return recommendations

    def RMSE(self, Data_test, test_size=0, data_size='100K', p=10):
        n_tests = Data_test.shape[0]
        SE = 0
        for n in range(n_tests):
            pred = self.pred(Data_test[n, 0], Data_test[n, 1])
            SE += (pred - Data_test[n, 2])**2
        RMSE = np.sqrt(SE / n_tests)
        print(f'{str(data_size)}::1::{self.n_factors}::{self.n_epochs}::{self.lamda}::{self.lr}::{RMSE}')
        return RMSE

    def evaluate(self, data_size, Data_test, test_size=0):
        sum_p = 0
        sum_r = 0
        self.Pu = np.zeros((self.users_count,))
        for u in range(self.users_count):
            recommended_items = self.recommend(u)
            ids = np.where(Data_test[:, 0] == u)[0]
            rated_items = Data_test[ids, 1]
            for i in recommended_items:
                if i in rated_items:
                    self.Pu[u] += 1
            sum_p += self.Pu[u]
        p = sum_p / (self.users_count * self.top)
        r = sum_p / (Data_test.shape[0])
        self.f.write(f'{str(data_size)}::1::{self.top}::{self.n_factors}::{self.n_epochs}::{test_size}::{p}::{r}\r\n')
        return p, r

# Đọc dữ liệu ratings
ratings = pd.read_csv('Ratings.csv', delimiter=",", encoding="latin1")
ratings.columns = ['User-ID', 'ISBN', 'Book-Rating']

# Loại bỏ các dòng có giá trị thiếu và các dòng có Book-Rating không hợp lệ
ratings = ratings.dropna(subset=['User-ID', 'ISBN', 'Book-Rating'])
ratings = ratings[ratings['Book-Rating'].between(0, 5)]

min_ratings_user = 15  # Người dùng phải có ít nhất 5 đánh giá
min_ratings_item = 15  # Sách phải có ít nhất 5 đánh giá

user_counts = ratings['User-ID'].value_counts()
isbn_counts = ratings['ISBN'].value_counts()

ratings = ratings[ratings['User-ID'].isin(user_counts[user_counts >= min_ratings_user].index)]
ratings = ratings[ratings['ISBN'].isin(isbn_counts[isbn_counts >= min_ratings_item].index)]

# Mã hóa User-ID và ISBN thành các chỉ số số học
user_id_mapping = {user: idx for idx, user in enumerate(ratings['User-ID'].unique())}
isbn_mapping = {isbn: idx for idx, isbn in enumerate(ratings['ISBN'].unique())}

# Thêm các cột mới cho ID của người dùng và sách
ratings['User-ID'] = ratings['User-ID'].map(user_id_mapping)
ratings['ISBN'] = ratings['ISBN'].map(isbn_mapping)

# Kiểm tra lại dữ liệu sau khi mã hóa
print(ratings.head())

# Chia dữ liệu thành Training set (70%) và Test set (30%)
rate_train_find, rate_train_test= train_test_split(ratings, test_size=0.8, random_state=42)
train_data, test_data = train_test_split(rate_train_find, test_size=0.3, random_state=42)

# Kiểm tra kích thước dữ liệu
print(f"Training data size: {train_data.shape}")
print(f"Test data size: {test_data.shape}")

# Sử dụng ma trận thưa (sparse) để lưu trữ ma trận huấn luyện
train_matrix = lil_matrix((len(user_id_mapping), len(isbn_mapping)))

for row in train_data.itertuples():
    train_matrix[row[1], row[2]] = row[3]  # User-ID, ISBN, Book-Rating

# Sử dụng ma trận thưa (sparse) để lưu trữ ma trận kiểm tra
test_matrix = lil_matrix((len(user_id_mapping), len(isbn_mapping)))

for row in test_data.itertuples():
    test_matrix[row[1], row[2]] = row[3]  # User-ID, ISBN, Book-Rating

# Kiểm tra ma trận huấn luyện (với ma trận thưa)
print(f"Training matrix shape: {train_matrix.shape}")
print(f"Test matrix shape: {test_matrix.shape}")

# Chuyển đổi dữ liệu thành dạng mảng NumPy cho Y (ma trận đánh giá)
Y_train = train_data[['User-ID', 'ISBN', 'Book-Rating']].values

# Khởi tạo mô hình MF
mf_model = MF(Y_train, n_factors=20, n_epochs=25, lr=0.02, lamda=0.1)

# Huấn luyện mô hình
mf_model.fit(x=10, data_size='100K', Data_test=test_data[['User-ID', 'ISBN', 'Book-Rating']].values)

# Tính toán RMSE trên bộ dữ liệu kiểm tra
rmse = mf_model.RMSE(test_data[['User-ID', 'ISBN', 'Book-Rating']].values)
print(f"RMSE on test data: {rmse}")