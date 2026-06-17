# FleetCare - Quản lý hồ sơ và bảo dưỡng phương tiện

FleetCare là ứng dụng desktop JavaFX phục vụ dự án **Quản lý hồ sơ và bảo dưỡng phương tiện**. Ứng dụng hỗ trợ quản lý đội xe, giấy tờ pháp lý, kế hoạch bảo dưỡng, phiếu bảo dưỡng, cảnh báo đến hạn, báo cáo chi phí và quản trị người dùng theo vai trò.

## Tổng quan

Dự án được tổ chức theo mô hình MVC:

- **View**: FXML và CSS trong `src/main/resources`.
- **Controller**: xử lý tương tác giao diện trong `src/main/java/controller`.
- **Service**: nghiệp vụ, validate và điều phối thao tác.
- **DAO**: truy cập dữ liệu MySQL bằng JDBC.
- **Entity/DTO**: mô hình dữ liệu và dữ liệu hiển thị.

Luồng nghiệp vụ chính:

1. Admin quản lý tài khoản, cấu hình ngưỡng cảnh báo và xem audit log.
2. Fleet Manager quản lý hồ sơ xe, giấy tờ, kế hoạch bảo dưỡng, cảnh báo và báo cáo chi phí.
3. Technician xem xe cần bảo dưỡng, tạo/cập nhật phiếu bảo dưỡng và lịch sử bảo dưỡng.

## Công nghệ sử dụng

- Java JDK 25
- JavaFX 25.0.3
- Maven Wrapper
- MySQL 8.x
- JDBC / MySQL Connector/J 9.7.0
- jBCrypt 0.4
- FXML + CSS

## Cấu trúc thư mục

```text
.
|-- config
|   `-- database.example.properties   # File cấu hình mẫu, copy thành database.properties
|-- data
|   |-- schema.sql                    # Tạo database, bảng, ràng buộc và view
|   `-- data.sql                      # Dữ liệu tham chiếu và dữ liệu demo
|-- dist
|   `-- QuanLyBaoDuongXe.zip          # Bản đóng gói
|-- lib
|   `-- mysql-connector-j-9.7.0.jar
|-- src/main/java
|   |-- app                           # MainApp và Launcher
|   |-- controller                    # Controller cho từng màn hình
|   |-- database                      # DatabaseConnection và TestConnection
|   |-- model
|   |   |-- dao                       # Lớp truy cập MySQL
|   |   |-- dto                       # DTO cho bảng/cảnh báo/báo cáo
|   |   `-- entity                    # Entity nghiệp vụ
|   |-- service                       # Xử lý nghiệp vụ và validate
|   |-- session                       # UserSession
|   `-- util                          # Alert, icon, password, stylesheet, scroll
`-- src/main/resources
    |-- css                           # Theme, layout, form, table, page CSS
    |-- images                        # Logo/icon FleetCare
    `-- view                          # FXML giao diện
```

## Chức năng chính

### Admin

- Dashboard quản trị tổng hợp người dùng, tài khoản bị khóa, cảnh báo hệ thống và audit log gần đây.
- Quản lý người dùng: thêm, sửa, khóa/mở khóa, reset mật khẩu.
- Đổi mật khẩu cá nhân.
- Cấu hình cảnh báo:
  - Số ngày cảnh báo giấy tờ.
  - Số ngày cảnh báo bảo dưỡng.
  - Số km cảnh báo bảo dưỡng.
  - Đồng bộ ngưỡng giấy tờ sang danh mục loại giấy tờ.
- Xem audit log đăng nhập, đăng xuất và thao tác quản trị.

### Fleet Manager

- Dashboard đội xe và danh sách cảnh báo tổng hợp.
- Quản lý hồ sơ phương tiện.
- Quản lý giấy tờ xe: đăng kiểm, bảo hiểm, phí đường bộ.
- Theo dõi cảnh báo giấy tờ quá hạn và sắp hết hạn.
- Lập, cập nhật, hủy kích hoạt kế hoạch bảo dưỡng.
- Xem cảnh báo bảo dưỡng theo ngày hoặc ODO.
- Xem lịch sử bảo dưỡng.
- Xem báo cáo chi phí bảo dưỡng và giấy tờ theo tháng, năm, xe.

### Technician

- Dashboard kỹ thuật.
- Xem danh sách xe cần bảo dưỡng.
- Tạo và cập nhật phiếu bảo dưỡng.
- Nhập hạng mục công việc/phụ tùng, số lượng, đơn giá và tổng chi phí.
- Khi phiếu chuyển sang `COMPLETED`, hệ thống cập nhật ODO xe và đóng chu kỳ kế hoạch liên quan.
- Xem lịch sử bảo dưỡng và chi tiết hạng mục.

## Nghiệp vụ và kiểm soát dữ liệu

Ứng dụng có các lớp validate ở tầng service và ràng buộc ở database, gồm:

- Không cho trùng mã xe, biển số, số khung, số máy.
- ODO không được âm và không được giảm so với dữ liệu bảo dưỡng đã hoàn thành.
- Không cho trùng giấy tờ hiện hành cùng xe/cùng loại.
- Ngày hết hạn giấy tờ không được trước ngày cấp hoặc ngày hiệu lực.
- Chi phí giấy tờ, chi phí bảo dưỡng và đơn giá hạng mục không được âm.
- Kế hoạch bảo dưỡng phải có chu kỳ theo ngày hoặc theo km.
- Không cho trùng kế hoạch active cùng xe/cùng loại bảo dưỡng.
- Phiếu đã `COMPLETED` không được chuyển ngược sang trạng thái khác.
- Mật khẩu tối thiểu 8 ký tự, có chữ và số, lưu bằng BCrypt.
- Admin không được khóa chính tài khoản đang đăng nhập và không reset mật khẩu của chính mình ở màn quản lý user.

## Yêu cầu môi trường

- JDK 25.
- MySQL Server 8.x.
- MySQL client CLI nếu muốn chạy script bằng terminal.
- Maven không cần cài riêng vì dự án có Maven Wrapper (`mvnw`, `mvnw.cmd`).

Kiểm tra môi trường:

```powershell
java -version
.\mvnw.cmd -v
mysql --version
```

Nếu máy có nhiều JDK, đặt `JAVA_HOME` về JDK 25 trước khi chạy:

```powershell
$env:JAVA_HOME = "D:\Java\jdk-25.0.3"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

## Cấu hình database

Ứng dụng đọc cấu hình database theo thứ tự ưu tiên:

1. System properties khi chạy Java.
2. Biến môi trường.
3. File local `config/database.properties`.

Tạo file cấu hình local:

```powershell
Copy-Item config\database.example.properties config\database.properties
```

Ví dụ cấu hình MySQL local:

```properties
DB_MODE=local
DB_URL=jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=your_mysql_password
```

`DB_MODE` có thể là:

- `local`: dùng `DB_URL`, `DB_USER`, `DB_PASSWORD`.
- `railway`: dùng `MYSQL_PUBLIC_URL` / `MYSQL_URL`, hoặc bộ `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`.
- `auto`: tự chọn Railway nếu có biến Railway, ngược lại dùng local.

Ví dụ cấu hình Railway:

```properties
DB_MODE=railway
MYSQL_PUBLIC_URL=mysql://user:password@host:port/database
```

Hoặc:

```properties
DB_MODE=railway
MYSQLHOST=host
MYSQLPORT=3306
MYSQLDATABASE=database
MYSQLUSER=user
MYSQLPASSWORD=password
```

## Tạo database và nạp dữ liệu demo

File SQL có tiếng Việt, nên trên Windows nên chạy bằng redirection. Không dùng dạng `Get-Content data\schema.sql | mysql` vì PowerShell có thể làm hỏng dấu tiếng Việt.

Chạy bằng PowerShell qua `cmd /c`:

```powershell
cmd /c "mysql -u root -p --default-character-set=utf8mb4 < data\schema.sql"
cmd /c "mysql -u root -p --default-character-set=utf8mb4 < data\data.sql"
```

Nếu tài khoản MySQL root không có mật khẩu, bỏ `-p`:

```powershell
cmd /c "mysql -u root --default-character-set=utf8mb4 < data\schema.sql"
cmd /c "mysql -u root --default-character-set=utf8mb4 < data\data.sql"
```

Nếu MySQL client chưa có trong `PATH`, thêm tạm trong PowerShell:

```powershell
$env:Path = "C:\Program Files\MySQL\MySQL Server 8.0\bin;$env:Path"
mysql --version
```

Database mặc định được tạo là:

```text
vehicle_maintenance_management
```

## Dữ liệu demo

`data/data.sql` nạp dữ liệu tham chiếu và dữ liệu demo gồm:

- 3 vai trò: `ADMIN`, `FLEET_MANAGER`, `TECHNICIAN`.
- 16 quyền nghiệp vụ.
- 3 loại giấy tờ: đăng kiểm, bảo hiểm, phí đường bộ.
- 5 loại bảo dưỡng.
- 20 hạng mục công việc/phụ tùng.
- 10 phương tiện.
- 30 giấy tờ pháp lý.
- Nhiều kế hoạch và phiếu bảo dưỡng, bao gồm dữ liệu lịch sử để xem báo cáo.
- Cảnh báo giấy tờ và bảo dưỡng theo dữ liệu tương đối với `CURDATE()`, giúp luôn có trạng thái `OVERDUE` và `COMING_DUE`.

Tài khoản demo:

| Username  | Password      | Vai trò         | Ghi chú           |
| --------- | ------------- | --------------- | ----------------- |
| `admin`   | `admin1234`   | `ADMIN`         | Quản trị hệ thống |
| `manager` | `manager1234` | `FLEET_MANAGER` | Quản lý đội xe    |
| `tech`    | `tech1234`    | `TECHNICIAN`    | Nguyễn Văn An     |
| `tech2`   | `tech21234`   | `TECHNICIAN`    | Nguyễn Văn Minh   |
| `tech3`   | `tech31234`   | `TECHNICIAN`    | Trần Quốc Hùng    |
| `tech4`   | `tech41234`   | `TECHNICIAN`    | Lê Hoàng Phúc     |

Kiểm tra nhanh sau khi seed:

```sql
USE vehicle_maintenance_management;

SELECT role_id, role_code, role_name, is_active FROM roles;
SELECT user_id, username, full_name, role_id, account_status FROM users;
SELECT vehicle_id, license_plate, vehicle_status FROM vehicles;
SELECT due_status, COUNT(*) FROM vw_due_vehicle_documents GROUP BY due_status;
SELECT due_status, COUNT(*) FROM vw_due_maintenance_plans GROUP BY due_status;
SELECT period_ym, SUM(total_cost) FROM vw_vehicle_cost_monthly GROUP BY period_ym;
```

## Chạy ứng dụng

Chạy app bằng Maven Wrapper:

```powershell
.\mvnw.cmd javafx:run
```

Main class được cấu hình trong `pom.xml`:

```text
app.Launcher
```

Màn hình đầu tiên là đăng nhập. Sau khi đăng nhập, menu và dashboard thay đổi theo vai trò của tài khoản.

## Kiểm tra kết nối database

Dự án có class kiểm tra kết nối:

```powershell
.\mvnw.cmd exec:java -Dexec.mainClass="database.TestConnection"
```

Nếu plugin `exec` chưa được tải/cấu hình, có thể kiểm tra bằng cách chạy ứng dụng trực tiếp với `javafx:run`; lỗi thiếu cấu hình database sẽ được in ra console khi app khởi động hoặc khi đăng nhập.

## Build

Biên dịch dự án:

```powershell
.\mvnw.cmd clean compile
```

Đóng gói jar:

```powershell
.\mvnw.cmd clean package
```

Kết quả build nằm trong thư mục `target/`.

## Cài đặt bản EXE trên Windows

Nếu dùng bản đóng gói sẵn, file nén nằm trong:

```text
dist/QuanLyBaoDuongXe.zip
```

Cách cài/chạy:

1. Giải nén `QuanLyBaoDuongXe.zip` ra một thư mục bất kỳ trên máy Windows.
2. Mở thư mục vừa giải nén và chạy file `QuanLyBaoDuongXe.exe`.
3. Đảm bảo MySQL đã được tạo database và nạp dữ liệu theo phần **Tạo database và nạp dữ liệu demo**.
4. Cấu hình kết nối database bằng biến môi trường hoặc file `config/database.properties` nếu bản đóng gói được chạy cùng thư mục cấu hình.

Lưu ý khi chia sẻ bản `.exe`:

- Không xóa các thư mục đi kèm trong bản giải nén, vì file `.exe` cần runtime và thư viện đi cùng.
- Không đưa mật khẩu database thật vào file cấu hình nếu nộp/chia sẻ công khai.
- Nếu Windows SmartScreen cảnh báo ứng dụng không rõ nhà phát hành, chọn **More info** rồi **Run anyway** nếu chắc chắn file được build từ dự án này.

## Các bảng và view chính

Các bảng chính:

- `roles`, `permissions`, `role_permissions`
- `users`
- `vehicles`
- `document_types`, `vehicle_documents`
- `maintenance_types`, `maintenance_items`
- `maintenance_plans`
- `maintenance_records`, `maintenance_record_items`
- `alert_settings`, `alerts`
- `audit_logs` được tạo tự động bởi `AuditLogDAO` khi ghi/xem log nếu bảng chưa tồn tại.

Các view nghiệp vụ:

- `vw_due_maintenance_plans`: xác định kế hoạch bảo dưỡng `OVERDUE`, `COMING_DUE`, `NORMAL`.
- `vw_due_vehicle_documents`: xác định giấy tờ xe quá hạn hoặc sắp hết hạn.
- `vw_vehicle_cost_monthly`: tổng hợp chi phí bảo dưỡng và giấy tờ theo tháng/xe.

## Mục đích

Dự án được xây dựng cho học phần thực tập cơ sở / đồ án JavaFX MVC, tập trung vào bài toán quản lý hồ sơ và bảo dưỡng phương tiện trong đội xe.

## Kết luận

FleetCare đã hoàn thiện các nghiệp vụ cốt lõi của một hệ thống quản lý đội xe ở mức ứng dụng desktop: quản lý người dùng theo vai trò, lưu trữ hồ sơ phương tiện, theo dõi giấy tờ pháp lý, lập kế hoạch bảo dưỡng, cập nhật phiếu bảo dưỡng, cảnh báo đến hạn và tổng hợp chi phí. Dự án cũng thể hiện được cách tổ chức mã nguồn theo mô hình MVC, tách rõ controller, service, DAO và model để dễ bảo trì.

Với dữ liệu demo, cấu hình database linh hoạt và hướng dẫn chạy chi tiết, ứng dụng có thể dùng để trình bày luồng nghiệp vụ thực tế từ quản trị hệ thống đến vận hành đội xe. Trong tương lai, dự án có thể mở rộng thêm upload file giấy tờ, phân quyền động chi tiết hơn, xuất báo cáo PDF/Excel và bổ sung test tự động cho các luồng quan trọng.
