# 🛒 E-Commerce System 

Dự án được xây dựng theo mô hình **Layered Architecture (Controller → Service → Repository)** giúp mã nguồn dễ bảo trì, mở rộng và tuân thủ nguyên tắc tách biệt trách nhiệm. Hệ thống gồm **backend Spring Boot, frontend React, PostgreSQL, Redis,** tích hợp **VNPay** để xử lý thanh toán trực tuyến an toàn và nhanh chóng. Toàn bộ kiến trúc được thiết kế hướng tới hiệu năng cao, dễ mở rộng và phù hợp cho các hệ thống thương mại điện tử hiện đại.

![markdown](https://skillicons.dev/icons?i=java,spring,react,postgres,redis,docker,postman,github)

![markdown](https://askany.s3.ap-southeast-1.amazonaws.com/images/70aa584f-2fee-475d-8128-15b60b0d6a69.jpg)


---

# 👥 Thành viên thực hiện
* **Nguyễn Bá Vũ Khoa** – 3122411097
* **Đặng Minh Hào** – 3122411047
* **Vũ Văn Minh** – 3122411129
* **Lại Trần Trung Kiên** – 3122411102


---

## Mục lục
- [🛒 E-Commerce System](#-e-commerce-system)
  - [👥 Thành viên thực hiện](#-thành-viên-thực-hiện)
  - [Mục lục](#mục-lục)
  - [✨ Tính Năng Chính (Key Features)](#-tính-năng-chính-key-features)
  - [🏗️ Kiến trúc tổng thể](#️-kiến-trúc-tổng-thể)
  - [🛠 Công nghệ sử dụng](#-công-nghệ-sử-dụng)
  - [📂 Cấu trúc thư mục (tóm tắt)](#-cấu-trúc-thư-mục-tóm-tắt)
  - [📚 Yêu cầu](#-yêu-cầu)
  - [⚙️ Cài đặt \& Chạy dự án](#️-cài-đặt--chạy-dự-án)
  - [📡 API Documentation](#-api-documentation)
    - [Endpoints](#endpoints)
      - [Auth](#auth)
      - [Product](#product)
      - [Cart](#cart)
      - [Order](#order)
      - [Payment](#payment)
  - [Response Schema](#response-schema)
  - [Bảo mật](#bảo-mật)
  - [⚡ Redis \& Security Strategy](#-redis--security-strategy)
  - [Testing](#testing)
  - [🐳 Docker \& Triển khai (Deployment)](#-docker--triển-khai-deployment)
  - [📄 License](#-license)

---

## ✨ Tính Năng Chính (Key Features)

Hệ thống cung cấp đầy đủ các chức năng của một sàn thương mại điện tử cơ bản, tập trung vào hiệu suất và bảo mật.


**👤 Quản lý Người dùng (User Management)**
* Đăng ký và Đăng nhập (Authentication).
* Phân quyền người dùng dựa trên vai trò (Role-based Authorization - ADMIN/USER).
* Quản lý thông tin cá nhân (Profile).

**📦 Quản lý Sản phẩm (Product Catalog)**
* Hỗ trợ đầy đủ thao tác **CRUD** (Tạo, Xem chi tiết, Cập nhật, Xóa).
* Tìm kiếm và lọc sản phẩm (tùy chọn mở rộng).

**🛒 Giỏ hàng (Shopping Cart)**
* Thêm sản phẩm vào giỏ hàng.
* Cập nhật số lượng hoặc xóa sản phẩm khỏi giỏ.
* Đồng bộ trạng thái giỏ hàng thời gian thực.

**🧾 Quản lý Đơn hàng (Order Processing)**
* Quy trình đặt hàng (Checkout) hoàn chỉnh.
* Xem lịch sử đơn hàng đã đặt.
* Cập nhật trạng thái đơn hàng (cho Admin/Manager).

**💳 Tích hợp Thanh toán (Payment Integration)**
* Tích hợp cổng thanh toán VNPay.
* Xử lý thanh toán an toàn và nhanh chóng.

---

## 🏗️ Kiến trúc tổng thể

<div align="center">
  <img src="https://shareprogramming.net/wp-content/uploads/2021/01/Screen-Shot-2021-01-01-at-8.13.20-PM.png" width="700"/>
</div>

<div align="center">
  <img src="D:\Project\ktpm-team\docs\image.png" width="700"/>
</div>


---

## 🛠 Công nghệ sử dụng

* **Frontend:** React, Vite, Axios
* **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA
* **Database:** PostgreSQL
* **Cache:** Redis
* **Security:** JWT, Bcrypt
* **Payment:** Cổng thanh toán VNPay
* **Testing:** JUnit 5, Mockito, Testcontainers
* **API Testing:** Postman
* **DevOps & Monitor:** Docker, Prometheus, Grafana
* **Build Tools:** npm, Maven

---

## 📂 Cấu trúc thư mục (tóm tắt)

Backend (Spring Boot)
```
backend/
 └── src/main/java/com/ecommerce/
     ├── config/
     ├── controller/
     ├── service/
     │     └── impl/
     ├── repository/
     ├── dto/
     ├── mapper/
     ├── entity/
     ├── exceptions/
     ├── orther/
     ├── enums/
     ├── util/
     ├── specification/
     └── EcommerceApplication.java
```

Frontend (React)
```
frontend/
 ├── src/
 │   ├── api/
 │   ├── components/
 │   ├── pages/
 │   ├── hooks/
 │   ├── store/   
 │   └── App.jsx
 └── public/
```

---

## 📚 Yêu cầu
- Java 17+
- Maven
- Node.js 16+
- Docker & Docker Compose (khuyến nghị)
- PostgreSQL
- Redis

---

## ⚙️ Cài đặt & Chạy dự án

1. Clone repository
```bash
git clone https://github.com/haolor/ktpm-team.git
cd ktpm-team
```

2. Backend
```bash
cd backend

# copy file môi trường mẫu và chỉnh sửa 
cp .env.example

# build & run
./mvnw clean package
./mvnw spring-boot:run

# hoặc chạy jar
java -jar target/ecommerce-*.jar
```
Server sẽ khởi động tại: http://localhost:8080/api/v1/

3. Frontend
```bash
cd frontend

cp .env.example

npm install
npm run dev

# hoặc
npm run build
serve -s build
```

---

## 📡 API Documentation
Tài liệu mô tả các RESTful API của hệ thống Backend.

* **Base URL**: `http://localhost:8080/api/v1`
* **Content-Type**: `application/json`
* **Authentication**: Bearer Token (JWT)
* **Version**: v1
* **Format**: JSON
* **Pagination**: Sử dụng `page` và `size` làm query parameters

### Endpoints
#### Auth
- POST /auth/register — đăng ký người dùng mới
  - body: {username, password, email, roles}
  - Response: 201 {id, username, email, roles}
- POST /auth/login — đăng nhập
  - body: {username, password}
  - Response: 200 {token, type, id, username, email, roles}
- GET  /auth/me — lấy thông tin người dùng hiện tại 
  - Protected: Có
- PUT  /auth/me — cập nhật thông tin người dùng hiện tại 
  - Protected: Có
  - body: {email, password, ...}
  - Response: 200 {id, username, email, roles}
- GET  /auth/users — danh sách người dùng (ADMIN)
  - Protected: Có
  - hỗ trợ pagination và filter
- GET  /auth/users/{id} — chi tiết người dùng (ADMIN)
  - Protected: Có
  - Response: 200 {id, username, email, roles}
- DELETE /auth/users/{id} — xóa người dùng (ADMIN)
  - Protected: Có
- PUT  /auth/users/{id} — cập nhật người dùng (ADMIN)
  - Protected: Có
  - body: {email, roles, ...}
  - Response: 200 {id, username, email, roles}
- POST /auth/refresh-token — làm mới token
  - body: {refreshToken}
- POST /auth/logout — đăng xuất
  - body: {refreshToken}
  - Protected: Có
- POST /auth/forgot-password — quên mật khẩu
  - body: {email}
  - Response: 200 {message}
- POST /auth/reset-password — đặt lại mật khẩu
  - body: {token, currentPassword, newPassword}
  - Response: 200 {message}
  - Protected: Có

----

#### Product
- GET  /products — danh sách sản phẩm 
  - hỗ trợ pagination, filter, sort
  - query params: page, size, category, priceMin, priceMax, sortBy, sortDir
  - Response: 200 {content: [...], page, size, totalElements, totalPages}
- POST /products/search — tìm kiếm sản phẩm
  - body: {keyword, page, size, sortBy, sortDir}
  - Response: 200 {content: [...], page, size, totalElements, totalPages}
  - hỗ trợ pagination và sort
- GET  /products/{id} — chi tiết sản phẩm
  -  Response: 200 {id, name, description, price, stock, category, images, createdAt, updatedAt}
- POST /products — tạo sản phẩm (ADMIN)
  - body: {name, description, price, stock, category, images}
  - Response: 201 {id, name, description, price, stock, category, images, createdAt, updatedAt}
  - Protected: Có
- PUT  /products/{id} — cập nhật sản phẩm (ADMIN)
  - body: {name, description, price, stock, category, images}
  - Response: 200 {id, name, description, price, stock, category, images, createdAt, updatedAt}
  - Protected: Có
- DELETE /products/{id} — xóa sản phẩm (ADMIN)
  - Protected: Có

---

#### Cart

- POST /cart/add — thêm vào giỏ hàng
  -  body: {productId, quantity}
  -  Response: 201 {cartId, items: [...], totalPrice}
  -  Protected: Có
- GET  /cart/{id} — lấy chi tiết giỏ hàng
  - Protected: Có
  - Response: 200 {cartId, items: [...], totalPrice}
- DELETE /cart/remove/{productId} — xóa khỏi giỏ hàng
  - Protected: Có
- PUT  /cart/update — cập nhật số lượng
  - body: {productId, quantity}
  - Protected: Có
- GET  /cart/user/{id} — lấy giỏ hàng của user
  - Protected: Có
- DELETE /cart/clear — xóa toàn bộ giỏ hàng
  - Protected: Có

--- 
#### Order
- POST /orders — tạo đơn hàng
    - body: {cartId, shippingAddress, paymentMethod}
    - Response: 201 {orderId, status, totalAmount, createdAt}
    - Protected: Có
- GET  /orders/{id} — chi tiết đơn hàng
  - Protected: Có
- GET  /orders — danh sách đơn hàng (ADMIN)
  - Protected: Có
  - hỗ trợ pagination và filter
- GET  /orders?page=&size=&status= — lọc đơn hàng theo trạng thái (ADMIN)
  - Protected: Có
- GET  /orders/user/{id} — đơn hàng của user
  - Protected: Có
- PUT  /orders/{id}/status — cập nhật trạng thái (ADMIN)
  - body: {status}
  - Protected: Có
- DELETE /orders/{id} — hủy đơn hàng
  - Protected: Có
- POST /orders/{id}/pay — thanh toán đơn hàng
  - body: {paymentDetails}
  - Protected: Có
  - Tích hợp VNPay
- GET  /orders/restaurant/{idRestaurant} — lấy đơn hàng theo nhà hàng (ADMIN)
  - Protected: Có
  - hỗ trợ pagination và filter

---
#### Payment
- POST /payments/vnpay — khởi tạo thanh toán VNPay
  - body: {orderId, amount, returnUrl}
  - Response: 200 {paymentUrl}
  - Protected: Có
- GET  /payments/vnpay/return — xử lý callback VNPay
  - query params: vnp_ResponseCode, vnp_TxnRef, vnp_Amount, ...
  - Response: 200 {message, orderStatus}
  

## Response Schema

Tất cả API responses tuân theo cấu trúc chung sau:

```json
{
    "success": true,
    "message": "Thao tác thành công",
    "data": {
        // Object, List, hoặc Page data
    },
    "errors": null,
    "timestamp": "2024-01-15T10:30:45Z"
}
```

**Chi tiết các field:**

| Field | Kiểu | Mô tả |
|-------|------|-------|
| `success` | boolean | Trạng thái request (true = thành công, false = thất bại) |
| `message` | string | Thông báo người dùng, dễ hiểu, có thể hiển thị trên UI |
| `data` | object/array | Dữ liệu chính (Object, List, hoặc Page) |
| `errors` | object/null | Chi tiết lỗi validation nếu có (hiển thị form errors) |
| `timestamp` | string | Thời gian server phản hồi (ISO 8601) |

**Ví dụ Response thành công:**
```json
{
    "success": true,
    "message": "Lấy danh sách sản phẩm thành công",
    "data": [{"id": 1, "name": "Sản phẩm A"}],
    "errors": null,
    "timestamp": "2024-01-15T10:30:45Z"
}
```

**Ví dụ Response lỗi:**
```json
{
    "success": false,
    "message": "Dữ liệu không hợp lệ",
    "data": null,
    "errors": {"email": "Email không đúng định dạng"},
    "timestamp": "2024-01-15T10:30:45Z"
}
```


---

## Bảo mật

- Spring Security + JWT
- Role-based access control: ROLE_USER, ROLE_ADMIN
- Lưu JWT có thể bằng HTTP-only cookie hoặc lưu ở client và trả qua header Authorization
- Các API nhạy cảm chỉ cho phép role tương ứng

---

## ⚡ Redis & Security Strategy

Trong dự án này, Redis **không** được sử dụng để cache dữ liệu tĩnh (như sản phẩm). Thay vào đó, Redis đóng vai trò cốt lõi trong việc đảm bảo **Bảo mật** và **Tính toàn vẹn dữ liệu** (Data Integrity).

| Tính năng | Key Pattern (Ví dụ) | TTL (Thời gian tồn tại) | Mục đích sử dụng |
| :--- | :--- | :--- | :--- |
| **🔐 OTP Storage** | `auth:otp:{email}` | 2 - 5 phút | Lưu mã OTP xác thực đăng ký/quên mật khẩu. Tự động hết hạn để bảo mật. |
| **🛡️ Login Guard** | `auth:login_attempts:{ip}` | 30 - 60 phút | Đếm số lần đăng nhập sai. Tạm khóa IP/User nếu vượt quá giới hạn (Chống Brute Force). |
| **🚫 Rate Limiting** | `rate_limit:{api}:{ip}` | 1 phút | Giới hạn số lượng request đến API trong một khoảng thời gian (Chống Spam/DDoS). |
| **🔒 Distributed Lock**| `lock:inventory:{prod_id}`| 10 - 30 giây | Cơ chế khóa phân tán để ngăn chặn **Race Condition** khi nhiều user cùng mua 1 sản phẩm cuối cùng. |

---

## Testing

- Unit tests: JUnit 5 + Mockito
- Integration tests: Testcontainers (Postgres, Redis)
- Chạy tests backend:
```bash
cd backend
./mvnw test
```

---

## 🐳 Docker & Triển khai (Deployment)

Dự án được đóng gói hoàn chỉnh (Containerization) giúp việc cài đặt và triển khai trở nên đồng nhất trên mọi môi trường.


File `docker-compose.yml` đã tích hợp sẵn toàn bộ các dịch vụ cần thiết để chạy ứng dụng:

* **backend-service**: Spring Boot Application (Port `8080`)
* **frontend-service**: ReactJS Application (Port `3000` hoặc `80`)
* **postgres-db**: Cơ sở dữ liệu chính (Port `5432`)
* **redis-cache**: Lưu trữ OTP, Rate Limit & Distributed Lock (Port `6379`)

**Bước 1: Cài đặt Docker**
Trước tiên, bạn cần cài đặt Docker Engine và Docker Compose trên máy tính:
* [Download Docker Desktop](https://www.docker.com/products/docker-desktop/) (Hỗ trợ Windows, Mac, Linux).
* Sau khi cài đặt, hãy đảm bảo Docker đã bật bằng cách mở ứng dụng Docker Desktop lên.

**Bước 2: Khởi chạy ứng dụng**
Mở terminal, di chuyển tới thư mục gốc của dự án (nơi chứa file `docker-compose.yml`) và chạy lệnh:

```bash
# 1. Di chuyển vào thư mục dự án (thay thế bằng tên thư mục của bạn)
cd ten-du-an

# 2. Build images và khởi động các container ở chế độ background
docker-compose up -d --build

```
---

## 📄 License
- Dự án được phát triển cho mục đích học tập.
    
