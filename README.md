# Vehicle Maintenance Management

Ung dung desktop JavaFX cho do an **Quan ly ho so va bao duong phuong tien**.
Du an duoc to chuc theo huong MVC, dung FXML/CSS cho giao dien va MySQL lam co so du lieu.

## Trang thai hien tai

- Da co man hinh dang nhap, dang xuat, session va phan quyen menu theo role.
- Da co quan ly tai khoan co ban: xem danh sach, them, sua, khoa/mo khoa user.
- Da co seed du lieu role/user demo trong `data/seed-auth.sql`.
- Da co Audit logs doc du lieu tu database, tu tao bang `audit_logs` khi can va ghi log dang nhap/dang xuat, quan tri user.
- Da co man hinh ho so phuong tien, giay to xe, canh bao giay to, ke hoach/canh bao/cap nhat bao duong va bao cao chi phi.
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
|-- audit-logs.sql
|-- Dump20260524.sql
`-- seed-auth.sql
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

## Tao database va seed tai khoan demo

Chay schema goc neu database chua co bang:

```powershell
Get-Content data\Dump20260524.sql | mysql -u root -p
```

Chay seed auth/user/role:

```powershell
Get-Content data\seed-auth.sql | mysql -u root -p
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
| `admin` | Dang nhap/dang xuat, Quan ly nguoi dung, Audit logs doc DB va ghi log auth/user admin |
| `manager` | Dashboard doi xe, Ho so phuong tien, Giay to xe, Canh bao giay to, Ke hoach/canh bao/lich su bao duong, Bao cao chi phi |
| `tech` | Dashboard ky thuat, Xe can bao duong, Cap nhat bao duong |

Kiem tra nhanh sau khi seed:

```sql
SELECT role_id, role_code, role_name, is_active FROM roles;
SELECT user_id, username, full_name, role_id, account_status FROM users;
SELECT audit_log_id, username, action, created_at FROM audit_logs ORDER BY created_at DESC LIMIT 10;
```

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
- Technician co menu bao duong.

## Checklist truoc khi demo/nop bai

Chay build:

```powershell
.\mvnw clean package
```

Neu may moi chua co database, chay schema va seed:

```powershell
Get-Content data\Dump20260524.sql | mysql -u root -p
Get-Content data\seed-auth.sql | mysql -u root -p
Get-Content data\audit-logs.sql | mysql -u root -p
```

Dong `audit-logs.sql` trong checklist la tuy chon de nap demo data; neu bo qua, app van tu tao bang khi mo/ghi Audit logs.

Chay ung dung va test 3 role:

```powershell
.\mvnw javafx:run
```

- `admin/123456`: mo Quan ly nguoi dung, them/sua/khoa/mo khoa user mau neu can, mo Audit logs, loc theo hanh dong/ngay va thu tim kiem nhanh.
- `manager/123456`: mo dashboard, ho so phuong tien, giay to xe, canh bao giay to, lich su bao duong va bao cao chi phi.
- `tech/123456`: mo dashboard ky thuat, Xe can bao duong va Cap nhat bao duong.
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
