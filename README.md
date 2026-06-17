# FleetCare - Quản lý hồ sơ và bảo dưỡng phương tiện

Ứng dụng desktop JavaFX cho đồ án **Quản lý hồ sơ và bảo dưỡng phương tiện**.
Dự án được tổ chức theo mô hình MVC, giao diện FXML/CSS, nghiệp vụ tách ở tầng service và lưu trữ bằng MySQL.

## Trạng thái bản cuối

Bản hiện tại đã được merge lên `origin/dev` và dùng làm bản hoàn thiện để demo/báo cáo.

Tính năng đã hoàn thành:

- Đăng nhập, đăng xuất, lưu session và phân quyền menu theo 3 role.
- Giao diện đã gắn nhận diện FleetCare: logo, app icon, sidebar, dialog và màu nền đồng bộ.
- Admin dashboard lấy số liệu từ DB: user, role, cảnh báo, audit log gần đây.
- Admin quản lý tài khoản: xem, tìm kiếm nhanh, thêm, sửa, khóa/mở khóa, reset mật khẩu cho user khác.
- Admin cấu hình ngưỡng cảnh báo giấy tờ và bảo dưỡng theo ngày/km.
- Admin xem audit logs, lọc theo hành động, ngày và tìm kiếm nhanh.
- Manager quản lý hồ sơ phương tiện, giấy tờ xe, cảnh báo giấy tờ, kế hoạch bảo dưỡng, cảnh báo bảo dưỡng, lịch sử bảo dưỡng và báo cáo chi phí.
- Technician xem xe cần bảo dưỡng, cập nhật phiếu bảo dưỡng, nhập hạng mục/phụ tùng và xem lịch sử bảo dưỡng.
- Các bảng chi tiết hồ sơ xe, giấy tờ, kế hoạch, phiếu và lịch sử bảo dưỡng mở bằng cửa sổ riêng khi nhấn đúp chuột.
- Bảo dưỡng có nghiệp vụ đóng chu kỳ kế hoạch khi phiếu hoàn thành, cập nhật ODO xe và tính tổng chi phí theo hạng mục.
- Báo cáo chi phí lấy theo năm hiện tại, không còn hard-code năm 2026.
- Seed demo đồng bộ cho cả nhóm trong `data/seed-team-demo-reset.sql`.

## Nhận xét đề tài

Đề tài phù hợp mục tiêu đồ án cơ sở: có phân quyền người dùng, dữ liệu demo thực tế, các module nghiệp vụ liên kết với nhau và có báo cáo tổng hợp. Luồng Manager và Technician đã bám sát bài toán quản lý đội xe: xe có hồ sơ, giấy tờ có hạn, kế hoạch bảo dưỡng có chu kỳ, phiếu hoàn thành cập nhật lại ODO/kế hoạch và chi phí được đưa vào báo cáo.

Giới hạn còn lại:

- Vai trò và phân quyền đang dùng 3 role cố định `ADMIN`, `FLEET_MANAGER`, `TECHNICIAN`; chưa có màn permission matrix để cấu hình quyền động.
- Hồ sơ giấy tờ mới lưu thông tin nghiệp vụ, chưa có upload file scan/PDF đính kèm.
- Chưa có test UI tự động bằng công cụ như TestFX/Playwright cho JavaFX; hiện đang dùng build test + smoke test FXML/service.

## Công nghệ sử dụng

- Java JDK 25
- JavaFX 25.0.3
- FXML + CSS
- MySQL + JDBC
- MySQL Connector/J
- Maven Wrapper
- jBCrypt 0.4

## Cấu trúc thư mục

```text
src/main/java
|-- app          # Điểm khởi chạy ứng dụng
|-- controller   # Controller của các màn hình FXML
|-- database     # Kết nối MySQL
|-- model
|   |-- dao      # Lớp truy cập dữ liệu
|   |-- dto      # Lớp truyền dữ liệu giữa các tầng
|   `-- entity   # Lớp ánh xạ bảng/nghiệp vụ
|-- service      # Xử lý nghiệp vụ và validate
|-- session      # Lưu phiên đăng nhập
`-- util         # Tiện ích chung: icon, dialog, smooth scroll, detail window

src/main/resources
|-- css          # Theme, layout, button, form, table và CSS từng page
|-- images       # Logo/icon FleetCare
`-- view         # FXML

data
|-- Dump20260524.sql              # Schema gốc và danh mục nền
|-- seed-team-demo-reset.sql      # Seed demo reset đồng bộ nên dùng khi demo
|-- seed-auth.sql                 # Seed riêng tài khoản demo
|-- audit-logs.sql                # Schema/seed audit log thủ công
|-- data-vehicles.sql             # Seed riêng xe + giấy tờ
|-- seed-maintenance-plans.sql    # Seed riêng kế hoạch bảo dưỡng
`-- seed-maintenance.sql          # Seed riêng phiếu/lịch sử bảo dưỡng
```

## Cấu hình môi trường

Yêu cầu:

- Java JDK 25
- MySQL Server
- Maven Wrapper có sẵn trong project

Kiểm tra Java/Maven:

```powershell
java -version
.\mvnw -v
```

Nếu máy có nhiều JDK, đặt `JAVA_HOME` về JDK 25:

```powershell
$env:JAVA_HOME = "D:\Java\jdk-25.0.3"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

## Cấu hình MySQL

App đọc cấu hình database theo thứ tự:

1. System properties.
2. Biến môi trường.
3. File local `config/database.properties`.

Tạo file local bằng cách copy:

```text
config/database.example.properties -> config/database.properties
```

Ví dụ:

```properties
DB_MODE=local
DB_URL=jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
DB_USER=root
DB_PASSWORD=mat_khau_mysql
```

Nếu MySQL local không có mật khẩu:

```properties
DB_PASSWORD=
```

Có thể dùng biến môi trường thay cho file local:

```powershell
$env:DB_MODE = "local"
$env:DB_URL = "jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh"
$env:DB_USER = "root"
$env:DB_PASSWORD = "mat_khau_mysql"
```

## Tạo database và seed demo

Nếu PowerShell không nhận `mysql`, thêm MySQL client vào PATH:

```powershell
$env:Path = "C:\Program Files\MySQL\MySQL Server 8.0\bin;$env:Path"
mysql --version
```

Chạy schema gốc:

```powershell
mysql --default-character-set=utf8mb4 -u root -p -e "source data/Dump20260524.sql"
```

Chạy seed demo đồng bộ cho cả nhóm:

```powershell
mysql --default-character-set=utf8mb4 -u root -p -e "source data/seed-team-demo-reset.sql"
```

Nếu MySQL root không có mật khẩu, bỏ `-p`.

Seed demo gồm:

- 3 role và 3 tài khoản demo.
- 6 phương tiện.
- 18 giấy tờ xe.
- Cảnh báo giấy tờ quá hạn/sắp hết hạn tính theo `CURDATE()`.
- 6 kế hoạch bảo dưỡng.
- 5 phiếu bảo dưỡng và hạng mục/phụ tùng.
- Dữ liệu báo cáo chi phí theo năm hiện tại.

Tài khoản demo:

| Username | Password | Role |
|---|---|---|
| `admin` | `123456` | `ADMIN` |
| `manager` | `123456` | `FLEET_MANAGER` |
| `tech` | `123456` | `TECHNICIAN` |

Kiểm tra nhanh sau seed:

```sql
SELECT role_id, role_code, role_name, is_active FROM roles;
SELECT user_id, username, full_name, role_id, account_status FROM users;
SELECT vehicle_id, license_plate, vehicle_status FROM vehicles;
SELECT audit_log_id, username, action, created_at FROM audit_logs ORDER BY created_at DESC LIMIT 10;
```

## Chạy và build

Chạy test/build:

```powershell
.\mvnw clean test
.\mvnw clean package
```

Chạy ứng dụng:

```powershell
.\mvnw javafx:run
```

## Phạm vi chức năng theo role

### Admin

- Dashboard quản trị tổng hợp user, role, cảnh báo và audit log gần đây.
- Quản lý người dùng:
  - Thêm user.
  - Sửa username, họ tên, email, số điện thoại, role.
  - Khóa/mở khóa tài khoản.
  - Reset mật khẩu cho tài khoản khác.
  - Tìm kiếm nhanh theo username, họ tên, email, điện thoại, role, trạng thái.
- Cấu hình cảnh báo:
  - Số ngày cảnh báo giấy tờ.
  - Số ngày/km cảnh báo bảo dưỡng.
  - Đồng bộ ngưỡng giấy tờ sang danh mục loại giấy tờ.
- Audit logs:
  - Xem log đăng nhập/đăng xuất và thao tác admin.
  - Lọc theo hành động/ngày.

Nghiệp vụ đã chặn:

- User thường không được gọi chức năng admin.
- Admin không được khóa tài khoản đang đăng nhập.
- Admin không reset mật khẩu của chính mình ở màn Quản lý người dùng.
- Mật khẩu mới phải có ít nhất 8 ký tự, gồm chữ và số, không trùng mật khẩu cũ.

### Manager

- Dashboard đội xe và cảnh báo.
- Hồ sơ phương tiện:
  - Thêm/sửa xe.
  - Chống trùng mã xe, biển số, số khung, số máy.
  - Không chấp nhận ODO âm, năm sản xuất không hợp lệ.
- Giấy tờ xe:
  - Thêm/sửa giấy tờ.
  - Lọc theo xe, loại giấy tờ, trạng thái và từ khóa.
  - Chống trùng giấy tờ hiện hành cùng xe/cùng loại.
  - Ngày hết hạn không được trước ngày cấp/ngày hiệu lực.
  - Phí không được âm.
- Cảnh báo giấy tờ:
  - Hiện quá hạn và sắp hết hạn.
  - Lọc `Tất cả`, `OVERDUE`, `COMING_DUE`.
- Kế hoạch bảo dưỡng:
  - Tạo/sửa kế hoạch theo ngày/km.
  - Tự tính ngày/ODO đến hạn nếu có mốc nền.
  - Không cho trùng kế hoạch active cùng xe/cùng loại.
- Cảnh báo bảo dưỡng và lịch sử bảo dưỡng.
- Báo cáo chi phí theo năm/xe.

### Technician

- Dashboard kỹ thuật.
- Xe cần bảo dưỡng.
- Cập nhật phiếu bảo dưỡng:
  - Tạo/sửa phiếu.
  - Chọn kế hoạch liên quan nếu có.
  - Nhập hạng mục công việc/phụ tùng.
  - Tổng chi phí tự tính theo hạng mục.
  - Khi phiếu `COMPLETED`, hệ thống đóng chu kỳ kế hoạch liên quan và cập nhật ODO xe.
- Lịch sử bảo dưỡng:
  - Tra cứu theo xe.
  - Xem chi tiết phiếu và hạng mục.

Nghiệp vụ đã chặn:

- Loại/trạng thái phiếu không hợp lệ.
- ODO âm.
- Tổng chi phí âm.
- Số lượng hạng mục bằng 0/âm.
- Đơn giá hạng mục âm.

## Luồng demo đề xuất

1. Chạy seed reset.
2. Đăng nhập `admin`:
   - Mở Dashboard.
   - Mở Quản lý người dùng, tìm kiếm, sửa user demo.
   - Thử khóa chính admin: hệ thống phải chặn.
   - Mở Cấu hình cảnh báo, nhập ngoài khoảng: hệ thống phải chặn.
   - Mở Audit logs.
3. Đăng xuất, đăng nhập `manager`:
   - Mở Hồ sơ phương tiện, Giấy tờ xe.
   - Mở Cảnh báo giấy tờ, lọc `Tất cả` phải hiện đầy đủ cảnh báo.
   - Mở Kế hoạch/Cảnh báo/Lịch sử bảo dưỡng.
   - Mở Báo cáo chi phí năm hiện tại.
4. Đăng xuất, đăng nhập `tech`:
   - Mở Xe cần bảo dưỡng.
   - Mở Cập nhật bảo dưỡng.
   - Nhấn đúp vào phiếu để mở cửa sổ chi tiết.
   - Thêm hạng mục/phụ tùng để xem tổng chi phí tự tính.
   - Mở Lịch sử bảo dưỡng.

## Kết quả kiểm thử bản cuối

Đã kiểm thử trên DB local sau khi merge vào `origin/dev`.

Build:

- `.\mvnw clean test`: PASS.

FXML smoke:

- Load thành công 16 màn hình FXML:
  - Login.
  - Main layout.
  - Admin/Manager/Technician dashboard.
  - Quản lý người dùng.
  - Audit logs.
  - Cấu hình cảnh báo.
  - Hồ sơ phương tiện.
  - Giấy tờ xe.
  - Cảnh báo giấy tờ.
  - Kế hoạch bảo dưỡng.
  - Cảnh báo bảo dưỡng.
  - Cập nhật bảo dưỡng.
  - Lịch sử bảo dưỡng.
  - Báo cáo chi phí.

Service smoke với seed demo:

| Hạng mục | Kết quả |
|---|---:|
| Users | 3 |
| Roles | 3 |
| Vehicles | 6 |
| Vehicle documents | 18 |
| Document alerts | 9 |
| Document alerts overdue | 5 |
| Document alerts coming due | 4 |
| Maintenance plans | 6 |
| Maintenance due alerts | 4 |
| Maintenance records | 5 |
| Report rows năm hiện tại | 10 |

Case lỗi đã test và được chặn:

- Admin tạo user trùng username.
- Admin tạo mật khẩu yếu.
- Admin khóa chính tài khoản đang đăng nhập.
- Manager/Technician gọi chức năng admin.
- Xe thiếu biển số, ODO âm.
- Giấy tờ có ngày hết hạn trước ngày cấp.
- Kế hoạch không có chu kỳ, ngưỡng cảnh báo âm.
- Phiếu bảo dưỡng có tổng tiền âm, trạng thái sai.
- Hạng mục có số lượng bằng 0, đơn giá âm.

## Ghi chú phát triển

- Không commit `config/database.properties` nếu chưa chắc chắn đã xóa thông tin riêng.
- Không commit mật khẩu MySQL cá nhân.
- Trước khi push:

```powershell
git status
.\mvnw clean test
```
