# FleetCare - Quan ly ho so va bao duong phuong tien

Ung dung desktop JavaFX cho do an **Quan ly ho so va bao duong phuong tien**.
Du an duoc to chuc theo mo hinh MVC, giao dien FXML/CSS, nghiep vu tach o tang service va luu tru bang MySQL.

## Trang thai ban cuoi

Ban hien tai da merge vao `origin/dev` tai commit:

```text
4c8f6e6 Polish FleetCare UI and merge latest dev
```

Tinh nang da hoan thanh:

- Dang nhap, dang xuat, luu session va phan quyen menu theo 3 role.
- Giao dien da gan nhan dien FleetCare: logo, app icon, sidebar, dialog va mau nen dong bo.
- Admin dashboard lay so lieu tu DB: user, role, canh bao, audit log gan day.
- Admin quan ly tai khoan: xem, tim kiem nhanh, them, sua, khoa/mo khoa, reset mat khau cho user khac.
- Admin cau hinh nguong canh bao giay to va bao duong theo ngay/km.
- Admin xem audit logs, loc theo hanh dong, ngay va tim kiem nhanh.
- Manager quan ly ho so phuong tien, giay to xe, canh bao giay to, ke hoach bao duong, canh bao bao duong, lich su bao duong va bao cao chi phi.
- Technician xem xe can bao duong, cap nhat phieu bao duong, nhap hang muc/phu tung va xem lich su bao duong.
- Cac bang chi tiet ho so xe, giay to, ke hoach, phieu va lich su bao duong mo bang cua so rieng khi nhan dup chuot.
- Bao duong co nghiep vu dong chu ky ke hoach khi phieu hoan thanh, cap nhat ODO xe va tinh tong chi phi theo hang muc.
- Bao cao chi phi lay theo nam hien tai, khong con hard-code nam 2026.
- Seed demo dong bo cho ca nhom trong `data/seed-team-demo-reset.sql`.

## Nhan xet de tai

De tai phu hop muc tieu do an co so: co phan quyen nguoi dung, du lieu demo thuc te, cac module nghiep vu lien ket voi nhau va co bao cao tong hop. Luong Manager va Technician da bam sat bai toan quan ly doi xe: xe co ho so, giay to co han, ke hoach bao duong co chu ky, phieu hoan thanh cap nhat lai ODO/ke hoach va chi phi duoc dua vao bao cao.

Gioi han con lai:

- Vai tro va phan quyen dang dung 3 role co dinh `ADMIN`, `FLEET_MANAGER`, `TECHNICIAN`; chua co man permission matrix de cau hinh quyen dong.
- Ho so giay to moi luu thong tin nghiep vu, chua co upload file scan/PDF dinh kem.
- Chua co test UI tu dong bang cong cu nhu TestFX/Playwright cho JavaFX; hien dang dung build test + smoke test FXML/service.

## Cong nghe su dung

- Java JDK 25
- JavaFX 25.0.3
- FXML + CSS
- MySQL + JDBC
- MySQL Connector/J
- Maven Wrapper
- jBCrypt 0.4

## Cau truc thu muc

```text
src/main/java
|-- app          # Diem khoi chay ung dung
|-- controller   # Controller cua cac man hinh FXML
|-- database     # Ket noi MySQL
|-- model
|   |-- dao      # Lop truy cap du lieu
|   |-- dto      # Lop truyen du lieu giua cac tang
|   `-- entity   # Lop anh xa bang/nghiep vu
|-- service      # Xu ly nghiep vu va validate
|-- session      # Luu phien dang nhap
`-- util         # Tien ich chung: icon, dialog, smooth scroll, detail window

src/main/resources
|-- css          # Theme, layout, button, form, table va CSS tung page
|-- images       # Logo/icon FleetCare
`-- view         # FXML

data
|-- Dump20260524.sql              # Schema goc va danh muc nen
|-- seed-team-demo-reset.sql      # Seed demo reset dong bo nen dung khi demo
|-- seed-auth.sql                 # Seed rieng tai khoan demo
|-- audit-logs.sql                # Schema/seed audit log thu cong
|-- data-vehicles.sql             # Seed rieng xe + giay to
|-- seed-maintenance-plans.sql    # Seed rieng ke hoach bao duong
`-- seed-maintenance.sql          # Seed rieng phieu/lich su bao duong
```

## Cau hinh moi truong

Yeu cau:

- Java JDK 25
- MySQL Server
- Maven Wrapper co san trong project

Kiem tra Java/Maven:

```powershell
java -version
.\mvnw -v
```

Neu may co nhieu JDK, dat `JAVA_HOME` ve JDK 25:

```powershell
$env:JAVA_HOME = "D:\Java\jdk-25.0.3"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

## Cau hinh MySQL

App doc cau hinh database theo thu tu:

1. System properties.
2. Bien moi truong.
3. File local `config/database.properties`.

Tao file local bang cach copy:

```text
config/database.example.properties -> config/database.properties
```

Vi du:

```properties
DB_MODE=local
DB_URL=jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
DB_USER=root
DB_PASSWORD=mat_khau_mysql
```

Neu MySQL local khong co mat khau:

```properties
DB_PASSWORD=
```

Co the dung bien moi truong thay cho file local:

```powershell
$env:DB_MODE = "local"
$env:DB_URL = "jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh"
$env:DB_USER = "root"
$env:DB_PASSWORD = "mat_khau_mysql"
```

## Tao database va seed demo

Neu PowerShell khong nhan `mysql`, them MySQL client vao PATH:

```powershell
$env:Path = "C:\Program Files\MySQL\MySQL Server 8.0\bin;$env:Path"
mysql --version
```

Chay schema goc:

```powershell
mysql --default-character-set=utf8mb4 -u root -p -e "source data/Dump20260524.sql"
```

Chay seed demo dong bo cho ca nhom:

```powershell
mysql --default-character-set=utf8mb4 -u root -p -e "source data/seed-team-demo-reset.sql"
```

Neu MySQL root khong co mat khau, bo `-p`.

Seed demo gom:

- 3 role va 3 tai khoan demo.
- 6 phuong tien.
- 18 giay to xe.
- Canh bao giay to qua han/sap het han tinh theo `CURDATE()`.
- 6 ke hoach bao duong.
- 5 phieu bao duong va hang muc/phu tung.
- Du lieu bao cao chi phi theo nam hien tai.

Tai khoan demo:

| Username | Password | Role |
|---|---|---|
| `admin` | `123456` | `ADMIN` |
| `manager` | `123456` | `FLEET_MANAGER` |
| `tech` | `123456` | `TECHNICIAN` |

Kiem tra nhanh sau seed:

```sql
SELECT role_id, role_code, role_name, is_active FROM roles;
SELECT user_id, username, full_name, role_id, account_status FROM users;
SELECT vehicle_id, license_plate, vehicle_status FROM vehicles;
SELECT audit_log_id, username, action, created_at FROM audit_logs ORDER BY created_at DESC LIMIT 10;
```

## Chay va build

Chay test/build:

```powershell
.\mvnw clean test
.\mvnw clean package
```

Chay ung dung:

```powershell
.\mvnw javafx:run
```

## Pham vi chuc nang theo role

### Admin

- Dashboard quan tri tong hop user, role, canh bao va audit log gan day.
- Quan ly nguoi dung:
  - Them user.
  - Sua username, ho ten, email, so dien thoai, role.
  - Khoa/mo khoa tai khoan.
  - Reset mat khau cho tai khoan khac.
  - Tim kiem nhanh theo username, ho ten, email, dien thoai, role, trang thai.
- Cau hinh canh bao:
  - So ngay canh bao giay to.
  - So ngay/km canh bao bao duong.
  - Dong bo nguong giay to sang danh muc loai giay to.
- Audit logs:
  - Xem log dang nhap/dang xuat va thao tac admin.
  - Loc theo hanh dong/ngay.

Nghiep vu da chan:

- User thuong khong duoc goi chuc nang admin.
- Admin khong duoc khoa tai khoan dang dang nhap.
- Admin khong reset mat khau cua chinh minh o man Quan ly nguoi dung.
- Mat khau moi phai co it nhat 8 ky tu, gom chu va so, khong trung mat khau cu.

### Manager

- Dashboard doi xe va canh bao.
- Ho so phuong tien:
  - Them/sua xe.
  - Chong trung ma xe, bien so, so khung, so may.
  - Khong chap nhan ODO am, nam san xuat khong hop le.
- Giay to xe:
  - Them/sua giay to.
  - Loc theo xe, loai giay to, trang thai va tu khoa.
  - Chong trung giay to hien hanh cung xe/cung loai.
  - Ngay het han khong duoc truoc ngay cap/ngay hieu luc.
  - Phi khong duoc am.
- Canh bao giay to:
  - Hien qua han va sap het han.
  - Loc `Tat ca`, `OVERDUE`, `COMING_DUE`.
- Ke hoach bao duong:
  - Tao/sua ke hoach theo ngay/km.
  - Tu tinh ngay/ODO den han neu co moc nen.
  - Khong cho trung ke hoach active cung xe/cung loai.
- Canh bao bao duong va lich su bao duong.
- Bao cao chi phi theo nam/xe.

### Technician

- Dashboard ky thuat.
- Xe can bao duong.
- Cap nhat phieu bao duong:
  - Tao/sua phieu.
  - Chon ke hoach lien quan neu co.
  - Nhap hang muc cong viec/phu tung.
  - Tong chi phi tu tinh theo hang muc.
  - Khi phieu `COMPLETED`, he thong dong chu ky ke hoach lien quan va cap nhat ODO xe.
- Lich su bao duong:
  - Tra cuu theo xe.
  - Xem chi tiet phieu va hang muc.

Nghiep vu da chan:

- Loai/trang thai phieu khong hop le.
- ODO am.
- Tong chi phi am.
- So luong hang muc bang 0/am.
- Don gia hang muc am.

## Luong demo de xuat

1. Chay seed reset.
2. Dang nhap `admin`:
   - Mo Dashboard.
   - Mo Quan ly nguoi dung, tim kiem, sua user demo.
   - Thu khoa chinh admin: he thong phai chan.
   - Mo Cau hinh canh bao, nhap ngoai khoang: he thong phai chan.
   - Mo Audit logs.
3. Dang xuat, dang nhap `manager`:
   - Mo Ho so phuong tien, Giay to xe.
   - Mo Canh bao giay to, loc `Tat ca` phai hien day du canh bao.
   - Mo Ke hoach/Canh bao/Lich su bao duong.
   - Mo Bao cao chi phi nam hien tai.
4. Dang xuat, dang nhap `tech`:
   - Mo Xe can bao duong.
   - Mo Cap nhat bao duong.
   - Nhan dup vao phieu de mo cua so chi tiet.
   - Them hang muc/phu tung de xem tong chi phi tu tinh.
   - Mo Lich su bao duong.

## Ket qua kiem thu ban cuoi

Da kiem thu tren DB local sau khi merge vao `origin/dev`.

Build:

- `.\mvnw clean test`: PASS.

FXML smoke:

- Load thanh cong 16 man hinh FXML:
  - Login.
  - Main layout.
  - Admin/Manager/Technician dashboard.
  - Quan ly nguoi dung.
  - Audit logs.
  - Cau hinh canh bao.
  - Ho so phuong tien.
  - Giay to xe.
  - Canh bao giay to.
  - Ke hoach bao duong.
  - Canh bao bao duong.
  - Cap nhat bao duong.
  - Lich su bao duong.
  - Bao cao chi phi.

Service smoke voi seed demo:

| Hang muc | Ket qua |
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
| Report rows nam hien tai | 10 |

Case loi da test va duoc chan:

- Admin tao user trung username.
- Admin tao mat khau yeu.
- Admin khoa chinh tai khoan dang dang nhap.
- Manager/Technician goi chuc nang admin.
- Xe thieu bien so, ODO am.
- Giay to co ngay het han truoc ngay cap.
- Ke hoach khong co chu ky, nguong canh bao am.
- Phieu bao duong co tong tien am, trang thai sai.
- Hang muc co so luong bang 0, don gia am.

## Ghi chu phat trien

- Khong commit `config/database.properties` neu chua chac chan da xoa thong tin rieng.
- Khong commit mat khau MySQL ca nhan.
- Truoc khi push:

```powershell
git status
.\mvnw clean test
```
