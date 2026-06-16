# Vehicle Maintenance Management

Ung dung desktop JavaFX cho do an **Quan ly ho so va bao duong phuong tien**.
Du an duoc to chuc theo huong MVC, dung FXML/CSS cho giao dien va MySQL lam co so du lieu.

## Trang thai hien tai

- Da co man hinh dang nhap, dang xuat, session va phan quyen menu theo role.
- Da co quan ly tai khoan co ban: xem danh sach, them, sua, khoa/mo khoa user.
- Da co seed du lieu role/user demo trong `data/seed-auth.sql`.
- Da co Audit logs doc du lieu tu database, tu tao bang `audit_logs` khi can va ghi log dang nhap/dang xuat, quan tri user.
- Da bo sung seed demo dong bo cho team trong `data/seed-team-demo-reset.sql`: reset du lieu demo, 3 tai khoan, 6 xe, 18 giay to, canh bao theo ngay hien tai, ke hoach/lich su bao duong va chi phi bao cao.
- Admin co man hinh cau hinh canh bao de cap nhat nguong giay to va bao duong theo ngay/km.
- Da co man hinh ho so phuong tien, giay to xe, canh bao giay to, ke hoach/canh bao/cap nhat bao duong va bao cao chi phi.
- Dashboard Admin, Manager va Technician uu tien lay so lieu tu DB thay vi dung so lieu ao/hard-code.
- RBAC hien tai duoc guard o menu va handler: Admin quan ly tai khoan, Manager quan ly doi xe/bao cao, Technician xu ly bao duong.

## Muc tieu

- Quan ly ho so phuong tien.
- Quan ly tai khoan va vai tro nguoi dung.
- Theo doi nhat ky hoat dong quan tri va dang nhap.
- Theo doi canh bao lien quan den giay to, bao duong va trang thai phuong tien.
- Ket noi MySQL qua JDBC.

## Cong nghe su dung

- Java JDK 25
- JavaFX 25.0.3
- FXML + CSS
- MySQL
- MySQL Connector/J
- Maven Wrapper
- jBCrypt 0.4
- VS Code

## Cau truc thu muc

```text
src/main/java
|-- app          # Diem khoi chay ung dung
|-- controller   # Controller cua cac man hinh FXML
|-- database     # Ket noi MySQL
|-- model
|   |-- dao      # Lop truy cap du lieu
|   |-- dto      # Lop truyen du lieu giua cac tang
|   `-- entity   # Lop anh xa du lieu nghiep vu
|-- service      # Xu ly nghiep vu
|-- session      # Luu thong tin phien dang nhap
`-- util         # Tien ich dung chung

src/main/resources
|-- css          # File giao dien CSS
|-- images       # Hinh anh, icon
`-- view         # File FXML

data
|-- Dump20260524.sql              # Schema goc va danh muc nen
|-- seed-auth.sql                 # Tai khoan demo admin/manager/tech
|-- audit-logs.sql                # Schema/seed demo thu cong cho audit logs
|-- seed-team-demo-reset.sql      # Seed demo reset dong bo cho ca nhom, nen dung khi demo
|-- seed-demo-realistic.sql       # Seed demo tong hop cu
|-- data-vehicles.sql             # Seed rieng cho xe + giay to xe
|-- seed-maintenance-plans.sql    # Seed rieng cho ke hoach bao duong
`-- seed-maintenance.sql          # Seed rieng cho phieu/lich su bao duong
```

## Yeu cau moi truong

- Cai Java JDK 25.
- Cai MySQL Server.
- Cai VS Code.
- Cai extension **Extension Pack for Java** trong VS Code.

Neu may co nhieu JDK, dat `JAVA_HOME` ve JDK 25 truoc khi build:

```powershell
$env:JAVA_HOME = "D:\Java\jdk-25.0.3"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

Kiem tra:

```powershell
java -version
.\mvnw -v
```

Ket qua Maven nen hien thi Java `25.x`.

## Cau hinh MySQL

App doc cau hinh database theo thu tu: system properties, bien moi truong, roi `config/database.properties`.
File `config/database.properties` la file local va khong commit len git. Co the copy tu:

```text
config/database.example.properties
```

Vi du cau hinh local:

```properties
DB_MODE=local
DB_URL=jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
DB_USER=root
DB_PASSWORD=mat_khau_mysql
```

Neu MySQL local khong co mat khau, de trong `DB_PASSWORD=`.

Thong tin ket noi bang bien moi truong cung duoc ho tro:

```powershell
$env:DB_MODE = "local"
$env:DB_URL = "jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh"
$env:DB_USER = "root"
$env:DB_PASSWORD = "mat_khau_mysql"
```

### Chay local

Khi dung file local, tao/cap nhat `config/database.properties` theo vi du tren.
Khi dung bien moi truong, dat cac bien trong cung terminal truoc khi chay build hoac chay app.

### Chay Railway

Dat `DB_MODE=railway`. App se doc thong tin Railway theo 1 trong 2 cach:

```properties
MYSQL_PUBLIC_URL=mysql://user:password@host:port/database
```

Co the dung `MYSQL_URL` thay cho `MYSQL_PUBLIC_URL`, nhung khi chay app tu may ca nhan thi public URL thuong de ket noi hon.

Hoac:

```properties
MYSQLHOST=host
MYSQLPORT=port
MYSQLDATABASE=database
MYSQLUSER=user
MYSQLPASSWORD=password
```

## Tao database va seed du lieu demo

Chay schema goc neu database chua co bang:

```powershell
Get-Content data\Dump20260524.sql | mysql -u root -p
```

Chay seed demo reset dong bo cho ca nhom:

```powershell
Get-Content data\seed-team-demo-reset.sql | mysql -u root -p
```

File `seed-team-demo-reset.sql` se xoa va nap lai du lieu demo nghiep vu gom:

- 6 phuong tien voi bien so, so khung, so may, ODO va trang thai gan voi nghiep vu doi xe.
- 18 giay to phap ly, moi xe co Dang kiem, Bao hiem va Phi duong bo.
- Ngay het han dung `CURDATE()` nen khi demo se luon co ca nhom qua han, sap het han trong 15 ngay va con hieu luc.
- Ke hoach bao duong theo ngay/km, phieu bao duong, phu tung/cong viec va chi phi bao cao.
- 3 tai khoan demo `admin`, `manager`, `tech` va permissions/RBAC can thiet.

`seed-auth.sql` chi can dung khi muon nap rieng tai khoan demo ma khong reset du lieu nghiep vu.

Neu muon nap rieng tung module, chay theo thu tu:

```powershell
Get-Content data\data-vehicles.sql | mysql -u root -p
Get-Content data\seed-maintenance-plans.sql | mysql -u root -p
Get-Content data\seed-maintenance.sql | mysql -u root -p
```

Audit logs tu tao bang `audit_logs` khi app ghi/doc log. Neu muon nap du lieu demo hoac kiem tra thu cong bang mysql CLI, chay them:

```powershell
Get-Content data\audit-logs.sql | mysql -u root -p
```

Tai khoan demo:

| Username | Password | Role |
|---|---|---|
| `admin` | `123456` | `ADMIN` |
| `manager` | `123456` | `FLEET_MANAGER` |
| `tech` | `123456` | `TECHNICIAN` |

Pham vi demo theo role:

| Username | Man hinh/chuc nang chinh |
|---|---|
| `admin` | Dang nhap/dang xuat, Quan ly nguoi dung, Cau hinh canh bao, Audit logs doc DB/ghi log auth-user admin, dashboard tong hop user/role/xe/canh bao lay tu DB |
| `manager` | Dashboard doi xe, Ho so phuong tien, Giay to xe, Canh bao giay to, Ke hoach/canh bao/lich su bao duong, Bao cao chi phi |
| `tech` | Dashboard ky thuat, Xe can bao duong, Cap nhat bao duong (nhap phu tung/hang muc), Lich su bao duong (tra cuu theo xe) |

Kiem tra nhanh sau khi seed:

```sql
SELECT role_id, role_code, role_name, is_active FROM roles;
SELECT user_id, username, full_name, role_id, account_status FROM users;
SELECT audit_log_id, username, action, created_at FROM audit_logs ORDER BY created_at DESC LIMIT 10;
```

## Luu y nghiep vu module Bao duong / Bao cao

- Lap ke hoach bao duong: nhap chu ky (ngay hoac km) + moc bao duong gan nhat; he thong **tu tinh ngay/ODO den han** neu de trong (ngay den han = ngay bao duong cuoi + chu ky ngay; ODO den han = ODO cuoi + chu ky km). Co `interval_days` thi bat buoc nhap `Ngay bao duong cuoi` (hoac nhap tay `Ngay den han`); tuong tu cho km.
- Nguong "canh bao truoc" lay theo gia tri nguoi dung nhap; neu de trong thi lay mac dinh tu bang `alert_settings` (mac dinh 7 ngay / 500 km).
- Man **Canh bao bao duong** chi hien xe **qua han (OVERDUE)** hoac **sap den han (COMING_DUE)**; ke hoach co ngay den han con xa se o trang thai NORMAL va khong hien.
- Phieu bao duong co the nhap nhieu **hang muc/phu tung** (cong viec WORK + phu tung PART), thanh tien tu tinh client-side (cot `line_total` trong DB la GENERATED).
- **Bao cao chi phi chi tinh phieu o trang thai COMPLETED** (theo view `vw_vehicle_cost_monthly`). Phieu OPEN/IN_PROGRESS chua tinh vao chi phi -> khi demo bao cao, dam bao co phieu COMPLETED.
- **Lich su bao duong**: chon xe de tra cuu ho so sua chua theo xe, click 1 phieu de xem chi tiet hang muc/phu tung. Ca Manager va Technician deu xem duoc.

## Build project

Chay trong thu muc goc project:

```powershell
.\mvnw clean package
```

Neu build loi do Java version, kiem tra lai `JAVA_HOME` dang tro dung JDK 25.

## Chay ung dung

```powershell
.\mvnw javafx:run
```

Luong dang nhap hien tai:

- Dang nhap bang tai khoan demo.
- Admin co menu Quan ly nguoi dung va Audit logs.
- Manager co menu phuong tien/giay to/canh bao/lich su bao duong/bao cao.
- Technician co menu Xe can bao duong, Cap nhat bao duong va Lich su bao duong.

## Checklist truoc khi demo/nop bai

Chay build:

```powershell
.\mvnw clean package
```

Neu may moi chua co database, chay schema va seed:

```powershell
Get-Content data\Dump20260524.sql | mysql -u root -p
Get-Content data\seed-team-demo-reset.sql | mysql -u root -p
```

Dong `audit-logs.sql` trong checklist la tuy chon de nap demo data; neu bo qua, app van tu tao bang khi mo/ghi Audit logs.

Chay ung dung va test 3 role:

```powershell
.\mvnw javafx:run
```

- `admin/123456`: mo Quan ly nguoi dung, them/sua/khoa/mo khoa user mau neu can, mo Cau hinh canh bao de doi nguong ngay/km, mo Audit logs, loc theo hanh dong/ngay va thu tim kiem nhanh.
- `manager/123456`: mo dashboard, ho so phuong tien, giay to xe, canh bao giay to, lich su bao duong va bao cao chi phi.
- `tech/123456`: mo dashboard ky thuat, Xe can bao duong, Cap nhat bao duong (them phu tung/hang muc), Lich su bao duong (tra cuu ho so sua chua theo xe).
- Moi role deu co nut Doi mat khau o sidebar. Mat khau moi phai co it nhat 8 ky tu, gom chu va so, va khong trung mat khau cu.
- Admin co the reset mat khau cho tai khoan khac trong man hinh Quan ly nguoi dung. Tai khoan bi reset se buoc doi lai mat khau khi dang nhap.
- Dang xuat sau moi role de xac nhan session duoc clear.
- Neu thay loi ket noi DB, kiem tra lai `DB_MODE`, `DB_URL`, `DB_USER`, `DB_PASSWORD`.

## Quy uoc phat trien

- Entity dat trong `model/entity`.
- DAO dat trong `model/dao`.
- DTO dat trong `model/dto`.
- Logic nghiep vu dat trong `service`.
- Ket noi MySQL dat trong `database`.
- FXML dat trong `src/main/resources/view`.
- CSS dat trong `src/main/resources/css`.
- Khong commit thong tin mat khau MySQL ca nhan.
- Moi thanh vien cau hinh `DB_USER`, `DB_PASSWORD` rieng bang bien moi truong.

Truoc khi push code, nen chay:

```powershell
.\mvnw clean package
```
