
# DoctorCare - Hệ thống Đặt lịch khám bệnh Online (Backend)

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-brightgreen.svg)
![JPA/Hibernate](https://img.shields.io/badge/JPA-Hibernate-orange.svg)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue.svg)
![Redis](https://img.shields.io/badge/Cache-Redis-red.svg)
![JWT](https://img.shields.io/badge/Security-JWT-purple.svg)
![Swagger](https://img.shields.io/badge/API_Docs-Swagger-lightblue.svg)

**DoctorCare** là một hệ thống backend mạnh mẽ, được xây dựng bằng Spring Boot, cung cấp toàn bộ các API cần thiết cho một ứng dụng đặt lịch khám bệnh trực tuyến. Dự án này bao gồm các chức năng quản lý người dùng, phân quyền, quản lý lịch khám, tìm kiếm bác sĩ, và nhiều hơn nữa, được thiết kế theo các best practice hiện đại nhất.

## Mục lục

1.  [Tính năng nổi bật](#tính-năng-nổi-bật)
2.  [Kiến trúc & Công nghệ sử dụng](#kiến-trúc--công-nghệ-sử-dụng)
3.  [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
4.  [Hướng dẫn cài đặt & Chạy dự án](#hướng-dẫn-cài-đặt--chạy-dự-án)
6.  [Tài liệu API (Swagger)](#tài-liệu-api-swagger)
7.  [Dữ liệu mẫu & Tài khoản](#dữ-liệu-mẫu--tài-khoản)

---

## 1. Tính năng nổi bật

Hệ thống được xây dựng với đầy đủ các nghiệp vụ cho 3 vai trò chính:

#### **Đối với Người dùng (Bệnh nhân):**
*   🔐 **Xác thực an toàn:** Đăng ký, Đăng nhập, Đăng xuất, Quên mật khẩu.
*   🔄 **Hệ thống Token chuyên nghiệp:** Sử dụng Access Token (ngắn hạn) và Refresh Token (dài hạn).
*   👤 **Quản lý tài khoản:** Xem và cập nhật thông tin cá nhân.
*   📅 **Đặt lịch khám:** Đặt lịch với bác sĩ theo các ca làm việc có sẵn.
*   📜 **Lịch sử:** Xem lại lịch sử các cuộc hẹn đã đặt.
*   ❌ **Hủy lịch hẹn:** Cho phép người dùng tự hủy lịch hẹn của mình.

#### **Đối với Bác sĩ:**
*   🗓️ **Quản lý lịch làm việc:** Tự tạo và quản lý các ca làm việc (Schedules).
*   📋 **Quản lý lịch hẹn:** Xem danh sách các lịch hẹn đã được bệnh nhân đặt.
*   ✅ **Xác nhận/Hủy lịch hẹn:** Chấp nhận hoặc từ chối các lịch hẹn, hệ thống tự động gửi email thông báo cho bệnh nhân.
*   🏁 **Đánh dấu hoàn thành:** Ghi nhận các cuộc hẹn đã khám xong.

#### **Đối với Quản trị viên (Admin):**
*   ⚙️ **Quản lý Dữ liệu gốc (CRUD):**
    *   Quản lý danh sách Chuyên khoa.
    *   Quản lý danh sách Phòng khám/Cơ sở y tế.
*   👥 **Quản lý Tài khoản:**
    *   Xem danh sách tất cả người dùng.
    *   **Thêm tài khoản Bác sĩ** vào hệ thống.
    *   **Khóa/Mở khóa** bất kỳ tài khoản nào.

#### **Tính năng hệ thống:**
*   🚀 **Hiệu năng cao:** Sử dụng **Redis Caching** cho các API đọc dữ liệu thường xuyên (Top chuyên khoa, Top phòng khám) để giảm tải cho database và tăng tốc độ phản hồi.
*   🔒 **Bảo mật:**
    *   Sử dụng **JWT** cho xác thực stateless.
    *   Sử dụng **Redis Blacklist** để vô hiệu hóa token ngay lập tức khi người dùng đăng xuất.
    *   Phân quyền chi tiết theo vai trò cho từng API.
    *   Mã hóa mật khẩu bằng BCrypt.
*   📧 **Hệ thống Email:** Tự động gửi email cho các sự kiện quan trọng (Chào mừng, Reset mật khẩu, Xác nhận/Hủy lịch hẹn).
*   📄 **Tài liệu API tự động:** Tích hợp **Swagger UI** để cung cấp tài liệu API trực quan và tương tác.

---

## 2. Kiến trúc & Công nghệ sử dụng

Dự án được xây dựng theo kiến trúc phân lớp (Layered Architecture) và tuân thủ các nguyên tắc SOLID.

*   **Ngôn ngữ:** `Java 17`
*   **Framework:** `Spring Boot 3.2.5`
*   **Database:**
    *   `MySQL`: Cơ sở dữ liệu quan hệ chính.
    *   `Spring Data JPA / Hibernate`: Tầng truy cập dữ liệu (ORM).
*   **In-memory Data Store:**
    *   `Redis`: Được sử dụng cho cả **Caching** và **JWT Blacklist Management**.
*   **Bảo mật:**
    *   `Spring Security 6`: Xử lý xác thực và phân quyền.
    *   `JSON Web Token (JWT)`: Cơ chế xác thực stateless.
*   **Gửi Email:** `Spring Boot Starter Mail`
*   **Template Engine:** `Thymeleaf` (dùng để tạo các mẫu email HTML).
*   **API Documentation:** `Springdoc OpenAPI (Swagger UI)`
*   **Build Tool:** `Maven`
*   **Utilities:** `Lombok`, `MapStruct`

---

## 3. Yêu cầu hệ thống

Để chạy dự án này, bạn cần cài đặt:
*   **JDK 17** hoặc cao hơn.
*   **Maven 3.8** hoặc cao hơn.
*   **MySQL Server**.
*   **Redis Server**.
*   Một IDE hỗ trợ Java/Spring Boot (khuyến nghị **IntelliJ IDEA Ultimate**).

---

## 4. Hướng dẫn cài đặt & Chạy dự án

1.  **Clone a repository:**
    ```bash
    git clone <your-repository-url>
    cd doctorcare
    ```

2.  **Cấu hình Database:**
    *   Tạo một database mới trong MySQL với tên `doctorcare`.
    *   Mở file `src/main/resources/application.properties`.
    *   Cập nhật các thông tin sau:
        ```properties
        spring.datasource.username=your_mysql_username
        spring.datasource.password=your_mysql_password
        ```

3.  **Cấu hình Email:**
    *   Trong `application.properties`, cập nhật thông tin tài khoản Gmail của bạn để gửi mail:
        ```properties
        spring.mail.username=your-email@gmail.com
        spring.mail.password=your-16-character-app-password
        ```
    *   Lưu ý: Cần sử dụng "Mật khẩu ứng dụng" của Google.

4.  **Chạy Redis:**
    *   Đảm bảo Redis Server đang chạy trên `localhost:6379`.

5.  **Build và Chạy ứng dụng:**
    *   **Cách 1 (Sử dụng Maven Wrapper):**
        ```bash
        ./mvnw spring-boot:run
        ```
    *   **Cách 2 (Sử dụng IDE):** Mở dự án trong IntelliJ, đợi Maven đồng bộ hóa, sau đó chạy lớp `DoctorcareApplication`.

6.  **Khởi tạo dữ liệu mẫu:**
    *   Lần đầu tiên chạy, ứng dụng sẽ tự động thực thi `DataInitializer` để chèn dữ liệu mẫu vào database.
    *   Trong `application.properties`, đảm bảo `spring.jpa.hibernate.ddl-auto` được đặt là `create` hoặc `update`.

---


## 5. Tài liệu API (Swagger)

Sau khi khởi động ứng dụng, tài liệu API đầy đủ và trực quan có sẵn tại:
*   **URL:** `http://localhost:8080/swagger-ui.html`

Bạn có thể sử dụng giao diện Swagger để xem chi tiết tất cả các endpoint, DTOs và thực hiện các cuộc gọi API trực tiếp từ trình duyệt. Đối với các API yêu cầu xác thực, hãy đăng nhập để lấy JWT token và sử dụng nút "Authorize".

---

## 6. Dữ liệu mẫu & Tài khoản

Ứng dụng sẽ tự động tạo các tài khoản sau để kiểm thử:
*   **Admin:**
    *   Email: `admin@doctorcare.com`
    *   Mật khẩu: `admin123`
*   **Bác sĩ:**
    *   Email: `dr.hung@doctorcare.com`, Mật khẩu: `doctor123`
    *   Email: `dr.lan@doctorcare.com`, Mật khẩu: `doctor123`
*   **Bệnh nhân:**
    *   Email: `patient.an@gmail.com`, Mật khẩu: `patient123`
    *   Email: `patient.binh@gmail.com`, Mật khẩu: `patient123`
        =======
# doctorcare
>>>>>>> a802ec6269d7948f4b0f4db8b823f52c55224197
