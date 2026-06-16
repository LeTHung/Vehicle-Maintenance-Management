# Vehicle Maintenance Management

Ung dung desktop JavaFX cho do an **Quan ly ho so va bao duong phuong tien**.
Du an duoc to chuc theo huong MVC, dung FXML/CSS cho giao dien va MySQL lam co so du lieu.

## Trang thai hien tai

- Da co man hinh dang nhap, dang xuat, session va phan quyen menu theo role.
- Da co quan ly tai khoan co ban: xem danh sach, them, sua, khoa/mo khoa user.
- Da co seed du lieu role/user demo trong `data/seed-auth.sql`.
- Cac man hinh vehicle/document/maintenance/report van do cac thanh vien/pham vi khac tiep tuc hoan thien.

## Muc tieu

- Quan ly ho so phuong tien.
- Quan ly tai khoan va vai tro nguoi dung.
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

Mac dinh project ket noi toi database:

```text
jdbc:mysql://localhost:3306/vehicle_maintenance_management
user: root
password: 123456
```

Thong tin ket noi nam trong file local:

```text
config/database.properties
```

File nay khong commit len git. Co the copy tu:

```text
config/database.example.properties
```

### Chay local

Dat `DB_MODE=local`, sau do cau hinh trong `config/database.properties`:

```properties
DB_URL=jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
DB_USER=root
DB_PASSWORD=mat_khau_mysql
```

Neu muon cau hinh bang bien moi truong trong terminal:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh"
$env:DB_USER = "root"
$env:DB_PASSWORD = "mat_khau_mysql"
```

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

Tai khoan demo:

| Username | Password | Role |
|---|---|---|
| `admin` | `123456` | `ADMIN` |
| `manager` | `123456` | `FLEET_MANAGER` |
| `tech` | `123456` | `TECHNICIAN` |

Kiem tra nhanh sau khi seed:

```sql
SELECT role_id, role_code, role_name, is_active FROM roles;
SELECT user_id, username, full_name, role_id, account_status FROM users;
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
- Admin co menu Quan ly nguoi dung.
- Manager co menu phuong tien/giay to/canh bao/bao cao.
- Technician co menu bao duong.

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
