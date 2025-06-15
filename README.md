## Kiến trúc

Theo kiến trúc Modulith (Tách Module trong Monolith) và Domain Driven Design

- **DDD**: chia thành 4 layer `domain`, `application`, `infrastructure` và `api`. 
- **Modulith**: mỗi module có file `*Module.java` khai báo cấu hình Spring. Các module có thể hoạt động độc lập nhưng vẫn nằm trong cùng ứng dụng Spring Boot. Bounded Context và giao tiếp bằng Event ( na ná Observer Pattern )

## Công nghệ

- Java 21
- Spring Boot 3.5
- Spring Data JPA, PostgreSQL
- Spring Security, OAuth2 Client
- Redis cache
- Spring Modulith
- JWT
- Spring Mail
- ModelMapper, Lombok, SpringDoc OpenAPI

##Kiến trúc DDD của từng module như sau:

1. domain/
- Entity, Value Object, Aggregate Root
- Repository interface (chỉ interface)
- Domain Service (logic nghiệp vụ thuần)
- Domain Event (event nghiệp vụ)
2. application/
- Application Service (điều phối use case)
- Use case (logic thao tác business flow)
- DTO (trao đổi dữ liệu với API)
- Mapper (chuyển đổi giữa Domain & DTO)
3. infrastructure/
- Triển khai Repository (JPA, Redis, ...)
- Entity JPA, cấu hình persistence
- Cấu hình bảo mật, cấu hình hạ tầng
4. api/
- REST Controller (định nghĩa endpoint)
- Input/Output model cho request/response
- Xử lý validate, mapping dữ liệu request vào service
5. Event để giao tiếp giữa các module

##Luồng xử lí request như sau:
- Client gửi request (JSON DTO) → Controller nhận DTO
- Controller gọi Application Service → truyền DTO
- Application Service → map DTO thành Domain Model/Entity → gọi Domain/Repo (nếu không có nghiệp vụ đặc biệt thì bỏ qua domain model và domain service)
- Repo (infra) → lưu/truy vấn Entity → trả Domain Model
- Application Service → map Domain Model/Entity sang Response DTO
- Controller trả Response DTO → Client
Module `auth` chịu trách nhiệm quản lý xác thực và phân quyền người dùng.
Các chức năng chính:
## Module Auth
- Đăng ký tài khoản và gửi email xác nhận.
- Đăng nhập qua email/ mật khẩu, yêu cầu xác nhận qua email trước khi phát hành JWT.
- Refresh token và lưu trữ refresh token trên Redis.
- Gửi và xác thực OTP để xác thực email.
- Tích hợp đăng nhập OAuth2 thông qua Spring Security.
