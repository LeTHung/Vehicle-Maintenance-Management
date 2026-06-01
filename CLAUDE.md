# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**FleetCare** — A JavaFX desktop application for vehicle maintenance management (quản lý hồ sơ & bảo dưỡng phương tiện). Direct MySQL connection via JDBC; no REST backend.

**Tech Stack:** Java 25 (JDK Temurin 25.0.3 tại `D:\Java\jdk-25.0.3`), JavaFX 25.0.3, FXML, Maven, MySQL (via `lib/mysql-connector-j-9.7.0.jar`), jBCrypt 0.4

## Commands

```powershell
# Run the application
.\mvnw javafx:run

# Build (produces JAR in target/)
.\mvnw clean package

# Run with custom DB credentials
$env:DB_URL="jdbc:mysql://localhost:3306/vehicle_maintenance_management"; $env:DB_USER="root"; $env:DB_PASSWORD=""; .\mvnw javafx:run
```

**Prerequisites:** JDK 25, MySQL running at `localhost:3306` with database `vehicle_maintenance_management`. Default credentials: `root` / empty password.

No test or lint commands are configured.

## Architecture

### Package Layout

```
src/main/java/
├── app/           # Entry point: Launcher → MainApp (JavaFX Application)
├── controller/    # FXML controllers (event handlers, UI logic)
├── database/      # DatabaseConnection — reads DB config from env vars with defaults
├── model/
│   ├── dao/       # Data Access Objects (DB queries)
│   ├── dto/       # Data Transfer Objects
│   └── entity/    # Domain entities (mirrors DB tables)
├── service/       # Business logic layer
├── session/       # UserSession — current logged-in user state
└── util/          # Shared utilities (AlertUtil, etc.)

src/main/resources/
├── view/          # FXML layout files
└── css/
    ├── global/    # theme.css, layout.css, buttons.css, forms.css, cards.css, tables.css
    └── pages/     # Page-specific CSS overrides
```

### Key Patterns

**Page Loading:** `MainLayoutController` dynamically loads FXML files into a central `StackPane` content area using `FXMLLoader.load()`. To add a new page, create an FXML in `src/main/resources/view/` and wire a menu item to load it.

**Role-Based Access Control:** `MainLayoutController.applyRolePermission()` controls menu item visibility based on roles: `ADMIN`, `MANAGER`, `TECH`. Apply this when adding new menu items.

**Database Access:** Always go through a DAO class. `DatabaseConnection` provides the connection; DAO classes own query logic; service classes call DAOs and enforce business rules.

**CSS Conventions:** Global variables and component styles live in `css/global/`. Page-specific overrides go in `css/pages/`. Colors and fonts are defined in `theme.css` — reference those variables rather than hardcoding values.

### Data Flow

```
FXML (view) ↔ Controller → Service → DAO → DatabaseConnection → MySQL
                                         ↕
                                      Entity/DTO
```

`UserSession` holds the authenticated user and role across the session lifetime.

## Development Status

### Cập nhật nhanh (2026-05-27)

- Day 5 CRUD user cơ bản đã hoàn thành trên branch `feature/khoa-day05-user-crud`.
- `UserService` đã implement create/update/list/lock/unlock và hash mật khẩu khi tạo tài khoản.
- `UserController` đã bind TableView, dialog thêm/sửa, khóa/mở khóa, refresh dữ liệu; `user-view.fxml` đã có `fx:id` cho các cột.
- Menu Admin → Quản lý người dùng đã mở màn hình thật `user-view.fxml` thay vì placeholder.
- Build kiểm tra cuối Day 5: `.\mvnw clean package` → BUILD SUCCESS.
- Day 6 đã hoàn thành trên branch `feature/khoa-day06-seed-user-role-data`: chuẩn hóa seed user/role và cập nhật tài liệu sau Day 5.
- `data/seed-auth.sql` đã chạy thử 2 lần trên MySQL local, kết quả giữ đúng 3 role + 3 user demo, không tạo trùng.
- Build kiểm tra cuối Day 6: `.\mvnw clean package` → BUILD SUCCESS.
- Day 7 đã hoàn thành trên branch `feature/khoa-day07-vehicle-document-rbac`: `dev` hiện đã có màn hình giấy tờ xe thật; phần của Khoa là siết RBAC/điều hướng cho role Quản lý đội xe.
- `MainLayoutController` không còn fallback role lạ/null về `MANAGER`; topbar `Thông báo` chỉ hiện cho manager và các handler giấy tờ xe có guard quyền trực tiếp.
- Build kiểm tra cuối Day 7: `.\mvnw clean package` → BUILD SUCCESS.
- Day 8 đã hoàn thành trên branch stacked `feature/khoa-day08-dashboard-alert-navigation`: manager dashboard có nút điều hướng thật sang hồ sơ phương tiện, giấy tờ xe, cảnh báo giấy tờ và báo cáo chi phí.
- `MainLayoutController.loadPage()` chuyển sang dùng `FXMLLoader` instance để inject navigation handler cho `ManagerDashboardController`.
- Build kiểm tra cuối Day 8: `.\mvnw clean package` → BUILD SUCCESS.
- Day 9 đã hoàn thành trên branch stacked `feature/khoa-day09-maintenance-navigation`: technician dashboard có controller riêng và nút điều hướng sang cảnh báo/cập nhật bảo dưỡng.
- `MainLayoutController` tiếp tục inject navigation handler cho `TechnicianDashboardController` sau khi load FXML.
- Build kiểm tra cuối Day 9: `.\mvnw clean package` → BUILD SUCCESS.

### Đã hoàn thành — Day 1 (2026-05-24)

- **pom.xml:** `maven.compiler.release=25`, thêm dependency jBCrypt 0.4
- **Entities:** `User.java`, `Role.java` — đầy đủ fields, constructors, getters/setters
- **DAOs:** `UserDAO.java` (7 methods), `RoleDAO.java` (3 methods) — skeleton ban đầu
- **Services:** `AuthenticationException.java`, `AuthService.java`, `UserService.java` — skeleton
- **Session:** `UserSession.java` — **FULLY IMPLEMENTED** (Singleton thread-safe)
- **Util:** `PasswordUtil.java` — skeleton ban đầu cho BCrypt hash/verify
- **Controllers:** `LoginController.java`, `DashboardController.java`, `UserController.java` — skeleton
- **FXML:** `login-view.fxml`, `dashboard-view.fxml`, `user-view.fxml` — hoàn chỉnh
- App compile sạch (`mvn clean compile` → BUILD SUCCESS), launch thành công

### Đã hoàn thành — Day 2 (2026-05-26)

- **Branch:** `feature/khoa-day02-user-role-dao` từ `dev`; commit `0f1b619 Implement Day 2 user role DAO` đã merge vào `origin/dev` qua PR #11 (`bc907e4 Merge pull request #11 from LeTHung/feature/khoa-day02-user-role-dao`).
- **PasswordUtil:** BCrypt thật với `BCrypt.gensalt(12)`, `hash(rawPassword)` và `verify(rawPassword, hash)`; mật khẩu rỗng/null không được hash, verify lỗi trả `false`.
- **RoleDAO:** JDBC thật cho `findById`, `findByCode`, `findAllActive`; map đủ `role_id`, `role_code`, `role_name`, `description`, `is_active`, `created_at`, `updated_at`.
- **UserDAO:** JDBC thật cho `findByUsername`, `findById`, `insert`, `update`, `updateAccountStatus`, `updateLastLogin`, `findAll`; map đủ entity `User`.
- **Smoke test DB:** `mvn compile` pass; BCrypt đúng/sai pass; `RoleDAO.findAllActive()` đọc được 3 role; `UserDAO` insert/find/update/lock/unlock/update last login pass và cleanup user test thành công.

### Đã hoàn thành — Day 3 (2026-05-26)

- **Branch:** `feature/khoa-day03-fxml-review` từ `dev` mới nhất sau khi Day 2 đã merge.
- **FXML chính:** `login-view.fxml`, `user-view.fxml`, `dashboard-view.fxml` đã được tạo sớm từ Day 1 và được verify lại theo tiêu chí Day 3.
- **FXML binding check:** `fx:controller`, `fx:id` và `onAction` đều khớp với `LoginController`, `UserController`, `DashboardController`; không thiếu field/method FXML.
- **Build check:** `mvn compile` pass; không có `TODO Day 3` hoặc `UnsupportedOperationException` trong 3 FXML/controller liên quan.
- **Không sửa code:** Day 3 là bước xác nhận/ghi nhận vì FXML đã hoàn thành trước đó; logic đăng nhập/RBAC vẫn để đúng phạm vi Day 4, CRUD user vẫn để Day 5.

### Đã hoàn thành — Day 4 (2026-05-26)

- **AuthService:** triển khai `login/logout`, kiểm tra status, BCrypt verify, set `UserSession`, cập nhật `last_login_at`.
- **LoginController:** validate input, hiển thị lỗi, điều hướng sang `main-layout.fxml`, áp global CSS khi chuyển scene.
- **MainLayoutController:** đọc session, áp RBAC theo role, hiển thị tên người dùng, logout về login, normalize role code.
- **MainApp:** đổi điểm vào mở `login-view.fxml` để tránh lỗi session khi vào app.
- **Seed dữ liệu:** tạo `data/seed-auth.sql` gồm role + 3 tài khoản mẫu (`admin/manager/tech`) mật khẩu `123456`.
- **Run check:** `mvnw javafx:run` thành công sau cập nhật.

### Đã hoàn thành — Day 5 (2026-05-27)

- **Branch:** `feature/khoa-day05-user-crud` từ `dev`.
- **UserService:** triển khai `createUser`, `updateUser`, `lockUser`, `unlockUser`, `listUsers`, `listActiveRoles`; validate username/full name/role/status; hash mật khẩu bằng `PasswordUtil.hash()` khi tạo user.
- **UserController:** bind `TableView`, nạp role active để hiển thị tên vai trò, dialog thêm/sửa user, khóa/mở khóa tài khoản, chặn khóa chính tài khoản đang đăng nhập.
- **User FXML:** thêm `fx:id` cho các cột, thêm nút refresh, dùng style class chung `page`, `toolbar`, `data-table`.
- **MainLayoutController:** menu `Quản lý người dùng` đã load `user-view.fxml` thay vì placeholder.
- **Build check:** `.\mvnw clean package` pass.

### Đã hoàn thành — Day 6 (2026-05-27)

- **Branch:** `feature/khoa-day06-seed-user-role-data` từ `dev` sau khi Day 5 đã merge.
- **Seed auth data:** chuẩn hóa `data/seed-auth.sql` để chạy nhiều lần không tạo trùng, tạo DB nếu chưa có, dùng đúng DB `vehicle_maintenance_management`.
- **Role/user demo:** giữ 3 role DB chuẩn `ADMIN`, `FLEET_MANAGER`, `TECHNICIAN` và 3 tài khoản `admin/manager/tech` mật khẩu `123456`.
- **Seed validation:** thêm câu `SELECT` cuối seed file để kiểm tra role/user sau khi chạy script.
- **README:** cập nhật trạng thái thật sau Day 4/Day 5, hướng dẫn cấu hình DB, seed dữ liệu và tài khoản demo.
- **Verify DB:** chạy `data/seed-auth.sql` 2 lần bằng MySQL CLI, kết quả vẫn chỉ có 3 role + 3 user demo.
- **Build check:** `.\mvnw clean package` pass.

### Đã hoàn thành — Day 7 (2026-06-01)

- **Branch:** `feature/khoa-day07-vehicle-document-rbac` từ `dev` mới nhất sau PR #20.
- **Bối cảnh:** `dev` đã có màn hình thật `vehicle/vehicle-document-view.fxml` và `vehicle/document-alert-view.fxml`; không tạo thêm CRUD/schema mới.
- **MainLayoutController:** role null/lạ trả về `UNKNOWN`, không còn mặc định thành `MANAGER`; giữ normalize `FLEET_MANAGER → MANAGER`, `TECHNICIAN → TECH`.
- **RBAC handler:** `openVehicleDocument()` và `openDocumentAlert()` chỉ cho `MANAGER`; role khác thấy placeholder không có quyền nếu gọi nhầm handler.
- **Topbar notification:** nút `Thông báo` có `fx:id=btnTopbarNotification` và chỉ hiển thị/enable cho `MANAGER`.
- **Build check:** `.\mvnw clean package` pass.

### Đã hoàn thành — Day 8 (2026-06-01)

- **Branch:** `feature/khoa-day08-dashboard-alert-navigation` xếp trên Day 7 vì PR #21 chưa merge vào `dev`.
- **Manager dashboard:** các nút hero đã có action thật: mở Hồ sơ phương tiện, Giấy tờ xe và Báo cáo chi phí.
- **Cảnh báo giấy tờ:** panel “Cảnh báo giấy tờ ưu tiên” có nút `Xem tất cả` để mở màn hình `document-alert-view.fxml`.
- **Navigation injection:** `MainLayoutController.loadPage()` dùng `FXMLLoader` instance để lấy controller sau khi load FXML và truyền callback điều hướng cho `ManagerDashboardController`.
- **Build check:** `.\mvnw clean package` pass.

### Đã hoàn thành — Day 9 (2026-06-01)

- **Branch:** `feature/khoa-day09-maintenance-navigation` xếp trên Day 8 vì PR #21/#22 chưa merge vào `dev`.
- **Technician dashboard:** thêm `TechnicianDashboardController` và gắn `fx:controller` cho `technician-dashboard-view.fxml`.
- **Bảo dưỡng navigation:** các nút “Tạo phiếu bảo dưỡng”, “Xem lịch hôm nay”, “Xem xe đến hạn”, “Mở phiếu bảo dưỡng”, “Tra cứu phiếu” điều hướng sang màn cảnh báo/cập nhật bảo dưỡng hiện có.
- **MainLayoutController:** inject callback `openMaintenanceAlert` và `openMaintenanceRecord` cho dashboard kỹ thuật sau khi load FXML.
- **Build check:** `.\mvnw clean package` pass.

### Trạng thái từng component

| Component | File | Trạng thái |
|---|---|---|
| UserSession | `session/UserSession.java` | ✅ Hoàn chỉnh |
| User entity | `model/entity/User.java` | ✅ Hoàn chỉnh |
| Role entity | `model/entity/Role.java` | ✅ Hoàn chỉnh |
| PasswordUtil | `util/PasswordUtil.java` | ✅ Hoàn chỉnh Day 2 |
| RoleDAO | `model/dao/RoleDAO.java` | ✅ Hoàn chỉnh Day 2 |
| UserDAO | `model/dao/UserDAO.java` | ✅ Hoàn chỉnh Day 2 |
| Login FXML | `view/login-view.fxml` | ✅ Hoàn chỉnh/verify Day 3 |
| User FXML | `view/user-view.fxml` | ✅ Hoàn chỉnh Day 5: bind cột, toolbar, refresh |
| Dashboard FXML | `view/dashboard-view.fxml` | ✅ Hoàn chỉnh/verify Day 3 |
| AuthService | `service/AuthService.java` | ✅ Hoàn chỉnh Day 4 |
| UserService | `service/UserService.java` | ✅ Hoàn chỉnh CRUD cơ bản Day 5 |
| LoginController | `controller/LoginController.java` | ✅ Hoàn chỉnh Day 4 |
| DashboardController | `controller/DashboardController.java` | ✅ Có số liệu dashboard; chưa gắn thông tin user |
| ManagerDashboardController | `controller/dashboard/ManagerDashboardController.java` | ✅ Có số liệu/cảnh báo giấy tờ; Day 8 có nút điều hướng sang màn liên quan |
| TechnicianDashboardController | `controller/dashboard/TechnicianDashboardController.java` | ✅ Day 9 điều hướng dashboard kỹ thuật sang cảnh báo/cập nhật bảo dưỡng |
| MainLayoutController | `controller/MainLayoutController.java` | ✅ RBAC + logout; mở user-view thật; Day 7 guard giấy tờ xe; Day 8/9 inject navigation handler |
| MainApp | `app/MainApp.java` | ✅ Start từ login-view Day 4 |
| UserController | `controller/UserController.java` | ✅ Hoàn chỉnh CRUD cơ bản Day 5 |
| Seed auth data | `data/seed-auth.sql` | ✅ Hoàn chỉnh Day 6: idempotent, có hướng dẫn và SELECT kiểm tra |
| README | `README.md` | ✅ Cập nhật Day 6, không còn mô tả MainApp/AuthService là skeleton |

### Bước tiếp theo

- **Review/Merge Day 7:** PR #21 (`feature/khoa-day07-vehicle-document-rbac` → `dev`) đang là draft, mergeable; cần review trước khi merge.
- **Review/Merge Day 8:** PR #22 (`feature/khoa-day08-dashboard-alert-navigation` → `feature/khoa-day07-vehicle-document-rbac`) đang là draft, mergeable; retarget về `dev` sau khi PR #21 merge.
- **Publish Day 9:** push branch `feature/khoa-day09-maintenance-navigation` và mở draft PR base vào Day 8; retarget/rebase theo chuỗi sau khi PR #21/#22 merge.
- **Verify Day 8:** đăng nhập `manager/123456`, từ dashboard bấm Hồ sơ phương tiện, Giấy tờ xe, Xem tất cả cảnh báo và Báo cáo chi phí; xác nhận các màn load đúng và RBAC Day 7 vẫn chặn admin/tech.
- **Verify Day 9:** đăng nhập `tech/123456`, từ dashboard kỹ thuật bấm các nút bảo dưỡng; xác nhận mở đúng cảnh báo/cập nhật bảo dưỡng và không lỗi FXML handler.
- **Day 10:** kiểm tra quyền Nhân viên kỹ thuật với danh sách xe cần bảo dưỡng và quyết định có mở sidebar cảnh báo bảo dưỡng cho tech hay không.
- **Tinh chỉnh encoding:** nếu nhóm thống nhất chuẩn UTF-8, chuyển dần text không dấu trong các file mới về tiếng Việt có dấu.

### Kế hoạch công việc Nguyễn Đăng Khoa

**Cụm chức năng phụ trách:** Xác thực, tài khoản, phân quyền, session, dashboard và điều hướng hệ thống.

**Vai trò nghiệp vụ:** Phục vụ Quản trị hệ thống và là nền đăng nhập cho Quản lý đội xe, Nhân viên kỹ thuật.

**Phạm vi file chính:**

- **Entity:** `User.java`, `Role.java`
- **DAO:** `UserDAO.java`, `RoleDAO.java`
- **Service:** `AuthService.java`, `UserService.java`
- **Controller:** `LoginController.java`, `UserController.java`, `DashboardController.java`
- **FXML:** `login-view.fxml`, `user-view.fxml`, `dashboard-view.fxml`
- **Session/Util:** `UserSession.java`, `PasswordUtil.java`

**Definition of Done:**

- **Đăng nhập:** Nhập đúng username/password thì vào dashboard; sai thì báo lỗi rõ ràng.
- **Đăng xuất:** Thoát session và quay lại màn hình login.
- **Phân quyền:** Admin thấy quản lý tài khoản; Quản lý đội xe thấy phương tiện/giấy tờ/cảnh báo/báo cáo; Nhân viên kỹ thuật thấy bảo dưỡng.
- **Quản lý tài khoản:** Tạo, sửa, khóa/mở khóa tài khoản cơ bản.
- **Dashboard:** Hiển thị thông tin người đăng nhập, role, các nút/menu điều hướng theo quyền.

**Kế hoạch 15 ngày của Khoa:**

| Ngày | Mục tiêu chung | Công việc của Nguyễn Đăng Khoa |
|---|---|---|
| Ngày 1 | Chốt tên bảng, tên file, branch Git | Chốt `users`, `roles`; kiểm tra Login skeleton; UserSession skeleton. |
| Ngày 2 | Tạo Entity + DAO cơ bản | Tạo/hoàn thiện `User.java`, `Role.java`, `UserDAO`, `RoleDAO`. |
| Ngày 3 | Tạo FXML chính | Tạo/hoàn thiện `login-view.fxml`, `user-view.fxml`, `dashboard-view.fxml`. |
| Ngày 4 | Đăng nhập và điều hướng | Hoàn thiện `LoginController`, `AuthService`, đăng xuất, session. |
| Ngày 5 | Quản lý phương tiện - CRUD | Hỗ trợ phân quyền màn hình phương tiện. |
| Ngày 6 | Validate phương tiện + dữ liệu mẫu | Tạo dữ liệu mẫu user/role. |
| Ngày 7 | Quản lý giấy tờ xe | Hỗ trợ quyền Quản lý đội xe truy cập giấy tờ. |
| Ngày 8 | Cảnh báo giấy tờ | Hiển thị số cảnh báo trên dashboard. |
| Ngày 9 | Lập kế hoạch bảo dưỡng | Hỗ trợ điều hướng sang màn hình bảo dưỡng. |
| Ngày 10 | Danh sách xe cần bảo dưỡng | Kiểm tra quyền Nhân viên kỹ thuật. |
| Ngày 11 | Cập nhật bảo dưỡng/sửa chữa | Cung cấp user kỹ thuật đang đăng nhập cho phiếu. |
| Ngày 12 | Phụ tùng + lịch sử bảo dưỡng | Test đăng nhập bằng tài khoản kỹ thuật. |
| Ngày 13 | Báo cáo chi phí | Hỗ trợ lọc báo cáo theo vai trò. |
| Ngày 14 | Tích hợp và sửa lỗi | Test auth, user, role, dashboard. |
| Ngày 15 | Chốt bản nộp và demo | README, tài khoản demo, kiểm tra chạy project. |

**Rule Git bắt buộc trước khi làm công việc mới:**

- Trước khi thực hiện một tính năng/công việc mới, bắt buộc tạo branch mới phát triển từ `dev`.
- Không phát triển trực tiếp trên `main` hoặc `dev`.
- Quy trình chuẩn:
  1. Kiểm tra worktree sạch bằng `git status`.
  2. Chuyển về `dev`: `git switch dev`.
  3. Cập nhật `dev`: `git pull origin dev`.
  4. Tạo branch mới: `git switch -c feature/khoa-dayNN-task-slug`.
- Chuẩn đặt tên branch: `feature/khoa-dayNN-task-slug`.
- Quy tắc tên branch: dùng chữ thường, không dấu, ngăn cách bằng dấu `-`; `NN` là số ngày có 2 chữ số như `day02`, `day04`, `day15`; `task-slug` mô tả ngắn công việc.
- Ví dụ: `feature/khoa-day02-user-role-dao`, `feature/khoa-day04-auth-login-session`, `feature/khoa-day05-user-crud`, `feature/khoa-day08-dashboard-alert-count`.

### Quyết định quan trọng

| Quyết định | Lý do |
|---|---|
| Java 25 (không phải 24) | Theo README; JDK Temurin 25.0.3+9 đã cài tại `D:\Java\jdk-25.0.3` |
| Flat packages (không có root `com.fleetcare.*`) | Giữ nhất quán với codebase hiện tại |
| DB name: `vehicle_maintenance_management` | Tên chuẩn dự án — nhưng `DatabaseConnection.java` mặc định vẫn trỏ `vehicle_maintenance_management`; phải override: `$env:DB_URL="jdbc:mysql://localhost:3306/vehicle_maintenance_management"` |
| Không sửa file của Hưng | `vehicle-*.fxml`, `maintenance-*.fxml`, `report-*.fxml` — off-limits; `MainLayoutController` đã được cho phép chỉnh cho auth/RBAC |
| Vehicle/document views đã có thật trên `dev` | `MainLayoutController` load `vehicle/vehicle-document-view.fxml` và `vehicle/document-alert-view.fxml`; Day 7 chỉ siết quyền mở màn hình |
| Day 2 dùng BCrypt cost 12 | Đủ an toàn cho đồ án desktop, đúng TODO ban đầu và tương thích jBCrypt 0.4 |
| DAO bắt `SQLException`, trả `Optional.empty`/`false`/list rỗng | Giữ phong cách giống các DAO vehicle/document hiện có; service/controller chịu trách nhiệm hiển thị lỗi rõ ràng ở Day 4/5 |
| `UserDAO.update` không cập nhật `password_hash` | Tránh ghi đè mật khẩu ngoài ý muốn; đổi mật khẩu sẽ đi qua flow riêng hoặc `UserService.createUser` hash trước khi insert |
| Day 5 chỉ làm create/update/lock/unlock, không delete user | Tài khoản có thể được tham chiếu bởi nhiều bảng nghiệp vụ; khóa tài khoản an toàn hơn xóa dữ liệu |
| Dialog thêm/sửa user đặt trong `UserController` | Phạm vi Day 5 nhỏ, chưa cần tách FXML dialog riêng; giảm số file và vẫn đủ CRUD cơ bản |
| Khi sửa user không cho đổi mật khẩu | Tránh thay đổi mật khẩu ngoài ý muốn; nếu cần sẽ làm flow đổi/reset mật khẩu riêng |
| Không cho admin tự khóa tài khoản đang đăng nhập | Tránh tự khóa phiên hiện tại rồi mất quyền thao tác trong demo/test |
| `UserController` nạp role active từ `UserService.listActiveRoles()` | Form thêm/sửa chỉ cho chọn role còn hiệu lực, không hardcode role trong UI |
| Text mới trong `user-view.fxml`/dialog tạm dùng không dấu | Repo đang có dấu hiệu lệch encoding ở nhiều file; dùng ASCII cho phần mới để tránh mojibake lan rộng, có thể chuẩn hóa UTF-8 sau |
| Day 6 seed không hardcode `role_id` khi insert role | Tránh lỗi khi DB đã có role với ID khác; user demo lấy `role_id` bằng `role_code` |
| `seed-auth.sql` tạo DB nếu chưa có | Giúp chạy seed nhanh trên máy mới và khớp DB mặc định của `DatabaseConnection` |
| Tài khoản demo giữ mật khẩu chung `123456` | Dễ demo/test login cho 3 role; mật khẩu đã lưu bằng BCrypt cost 12 |
| Role code DB giữ `FLEET_MANAGER`/`TECHNICIAN` | Khớp schema/seed gốc; app đã normalize sang `MANAGER`/`TECH` khi áp RBAC |
| `seed-auth.sql` có câu `SELECT` kiểm tra cuối file | Giúp xác nhận nhanh seed đã tạo đúng role/user ngay trong MySQL Workbench hoặc CLI |
| Day 6 không đụng schema và không sửa module vehicle | Phạm vi của Khoa là user/role/auth; tránh ảnh hưởng phần việc của thành viên khác |
| Role DB chưa khớp role UI | DB seed dùng `FLEET_MANAGER`, `TECHNICIAN`; app normalize sang `MANAGER`, `TECH` trong layout/dashboard để RBAC khớp UI |
| Role null/lạ không fallback về manager | Tránh mở nhầm màn hình nghiệp vụ cho phiên không có role hợp lệ; dùng badge `UNKNOWN` và placeholder không có quyền |
| Day 2 đã merge vào `origin/dev` | PR #11 đã merge commit `0f1b619`; local `dev` hiện có thể còn behind nên phải pull trước khi tạo branch Day 4 |
| Day 3 là verify-only | `login-view.fxml`, `user-view.fxml`, `dashboard-view.fxml` đã được tạo sớm từ Day 1; Day 3 chỉ xác nhận binding/load readiness, không nhét logic Day 4/5 vào FXML |
| Chưa cấu hình TableColumn của `user-view.fxml` ở Day 3 | Đây là phạm vi Day 5 `UserController`/`UserService`; Day 3 chỉ yêu cầu FXML chính tồn tại và binding không lỗi |
| Start app từ login-view | Tránh lỗi session khi vào `main-layout.fxml` và đúng flow xác thực |
| Normalize role code khi đăng nhập | Mapping `FLEET_MANAGER/TECHNICIAN` → `MANAGER/TECH` giúp RBAC khớp UI |
| Seed auth data trong `data/seed-auth.sql` | Có sẵn tài khoản mẫu để test login nhanh |
| `CLAUDE.md` đang bị `.gitignore` ignore | Nếu muốn lưu thay đổi file hướng dẫn này vào Git phải dùng `git add -f CLAUDE.md` |
