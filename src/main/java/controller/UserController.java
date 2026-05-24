package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import model.entity.User;

/**
 * Controller cho màn hình quản lý người dùng ({@code user-view.fxml}).
 *
 * <p>Day 1: skeleton. Day 5 sẽ implement CRUD: thêm / sửa / khoá - mở khoá
 * tài khoản thông qua {@code UserService}.</p>
 */
public class UserController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button lockButton;

    @FXML
    public void initialize() {
        // TODO Day 5: cấu hình các TableColumn, nạp danh sách user từ UserService.listUsers()
    }

    @FXML
    private void onAddClick() {
        // TODO Day 5: mở dialog tạo user mới -> UserService.createUser
    }

    @FXML
    private void onEditClick() {
        // TODO Day 5: mở dialog sửa user đang chọn -> UserService.updateUser
    }

    @FXML
    private void onLockClick() {
        // TODO Day 5: toggle khoá/mở khoá user đang chọn qua UserService.lockUser/unlockUser
    }
}
