# Vehicle Maintenance Management

Ung dung desktop JavaFX cho do an **Quan ly ho so va bao duong phuong tien**. Du an duoc to chuc theo huong MVC, dung FXML/CSS cho giao dien va MySQL lam co so du lieu.

## Muc tieu

- Quan ly ho so phuong tien.
- Quan ly tai khoan va vai tro nguoi dung.
- Theo doi canh bao lien quan den giay to, bao duong va trang thai phuong tien.
- Ket noi MySQL qua JDBC.

> Trang thai hien tai: project dang o giai doan dung khung. Mot so lop nhu `MainApp`, `AuthService` dang la skeleton va can duoc hoan thien truoc khi chay day du chuc nang.

## Cong nghe su dung

- Java JDK 25
- JavaFX 25.0.3
- FXML + CSS
- MySQL
- MySQL Connector/J 9.7.0
- Maven Wrapper
- VS Code

## Cau truc thu muc

```text
src/main/java
|-- app          # Diem khoi chay ung dung
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

lib
`-- mysql-connector-j-9.7.0.jar
```

## Yeu cau moi truong

- Cai Java JDK 25.
- Cai MySQL Server.
- Cai VS Code.
- Cai extension **Extension Pack for Java** trong VS Code.

Neu may co nhieu JDK, dat `JAVA_HOME` ve JDK 25 truoc khi build:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.3"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

Kiem tra:

```powershell
java -version
.\mvnw -v
```

Ket qua Maven nen hien thi Java `25.0.3`.

## Cau hinh MySQL

Mac dinh project ket noi toi database:

```text
jdbc:mysql://localhost:3306/vehicle_maintenance_management
user: root
password: rong
```

Thong tin nay nam trong:

```text
src/main/java/database/DatabaseConnection.java
```

Neu MySQL cua ban co mat khau, cau hinh trong terminal truoc khi chay:

```powershell
$env:DB_USER = "root"
$env:DB_PASSWORD = "mat_khau_mysql"
```

Neu muon doi URL database:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh"
```

## Build project

Chay trong thu muc goc project:

```powershell
.\mvnw clean package
```

Neu build loi do Java version, kiem tra lai `JAVA_HOME` dang tro dung JDK 25.

## Chay ung dung

Khi cac file khoi chay va giao dien da duoc hoan thien, chay:

```powershell
.\mvnw javafx:run
```

Hien tai can hoan thien `MainApp` va cac man hinh FXML truoc khi app chay day du.

## Thu vien MySQL trong `lib`

Project dang dung file driver:

```text
lib/mysql-connector-j-9.7.0.jar
```

Trong `pom.xml`, thu vien nay duoc khai bao bang `systemPath`:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.7.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/mysql-connector-j-9.7.0.jar</systemPath>
</dependency>
```

## Quy uoc phat trien

- Entity dat trong `model/entity`.
- DAO dat trong `model/dao`.
- DTO dat trong `model/dto`.
- Logic nghiep vu dat trong `service`.
- Ket noi MySQL dat trong `database`.
- FXML dat trong `src/main/resources/view`.
- CSS dat trong `src/main/resources/css`.

## Ghi chu cho nhom

- Khong commit thong tin mat khau MySQL ca nhan.
- Moi thanh vien cau hinh `DB_USER`, `DB_PASSWORD` rieng bang bien moi truong.
- Truoc khi push code, nen chay:

```powershell
.\mvnw clean package
```
