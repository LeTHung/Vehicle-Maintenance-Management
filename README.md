# DoAn JavaFX MVC

Skeleton ung dung desktop Java cho do an mon hoc, dung JavaFX, FXML, CSS va mo hinh MVC.

## Yeu cau

- Java JDK 25.
- MySQL Server.
- VS Code.
- Extension khuyen nghi: **Extension Pack for Java** (`vscjava.vscode-java-pack`).
- Khong can cai Maven rieng vi project da co Maven Wrapper.

Neu may co nhieu JDK, dat `JAVA_HOME` ve JDK 25 truoc khi build:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.3"
```

## Chay project

Mo thu muc project nay trong VS Code, sau do chay:

```powershell
.\mvnw -v
.\mvnw clean package
.\mvnw javafx:run
```

Lan dau chay, wrapper se tai Maven 3.9.11 ve thu muc Maven cache cua user.

## Cau hinh MySQL

Project dung MySQL Connector/J va JDBC. File driver MySQL da duoc dat trong:

```text
lib/mysql-connector-j-9.7.0.jar
```

`pom.xml` dang tro truc tiep toi file `.jar` nay bang `systemPath`, nen VS Code/Maven se nap driver tu thu muc `src/lib`. Mac dinh app ket noi toi:

```text
jdbc:mysql://localhost:3306/vehicle_maintenance_management
user: root
password: rong
```

Khi chay app, DAO se tu tao database `vehicle_maintenance_management`, bang `vehicles`, va 2 dong du lieu mau neu database dang rong. Neu MySQL cua ban dung mat khau, dat bien moi truong trong terminal truoc khi chay:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/vehicle_maintenance_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh"
$env:DB_USER = "root"
$env:DB_PASSWORD = ""
.\mvnw javafx:run
```

## Cau truc MVC

```text
src/main/java
|-- app          # Entry point JavaFX
|-- controller   # Dieu phoi FXML va su kien UI
|-- database     # Ket noi MySQL
|-- model        # Entity va DAO
|   |-- entity   # Lop mo ta du lieu
|   `-- dao      # Lop truy cap MySQL
|-- service      # Xu ly nghiep vu
`-- util         # Helper dung chung

src/main/resources
|-- view         # File .fxml
`-- css          # File .css
```

Controller chi doc/ghi control UI va goi service. Logic nghiep vu nam trong service. `database` chi giu lop ket noi MySQL. Entity dat trong `model/entity`, DAO dat trong `model/dao`.

## Them thu vien Java vao lib

Dat file `.jar` vao `src/lib`, sau do khai bao dependency trong `pom.xml`. Vi du MySQL Connector/J hien tai:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.7.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/mysql-connector-j-9.7.0.jar</systemPath>
</dependency>
```

Sau khi luu `pom.xml`, VS Code Java Projects se tu dong import lai dependency. Neu chua cap nhat, chay:

```powershell
.\mvnw clean package
```

## Them man hinh FXML moi

1. Tao FXML trong `src/main/resources/view`.
2. Tao controller trong `src/main/java/controller`.
3. Khai bao `fx:controller="controller.TenController"` trong FXML.
4. Dua style vao `src/main/resources/css/app.css` hoac tao file CSS rieng.
5. Load FXML bang `FXMLLoader` tu `MainApp` hoac tu controller dieu huong.
