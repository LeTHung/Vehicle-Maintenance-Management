package controller;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.entity.Role;
import model.entity.User;
import service.UserService;
import session.UserSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller cho man hinh quan ly nguoi dung.
 *
 * <p>Day 5: them / sua / khoa - mo khoa tai khoan qua UserService.</p>
 */
public class UserController {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_LOCKED = "LOCKED";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final UserService userService = new UserService();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final Map<Long, Role> roleById = new HashMap<>();

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Long> colUserId;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colFullName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colPhone;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TableColumn<User, String> colStatus;
    @FXML
    private TableColumn<User, String> colLastLogin;

    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button lockButton;
    @FXML
    private Button refreshButton;

    @FXML
    public void initialize() {
        configureTable();
        configureSelectionState();
        reloadData();
    }

    @FXML
    private void onAddClick() {
        Optional<UserFormData> formData = showUserDialog(null);
        if (formData.isEmpty()) {
            return;
        }

        try {
            UserFormData data = formData.get();
            userService.createUser(
                    data.username(),
                    data.password(),
                    data.fullName(),
                    data.email(),
                    data.phone(),
                    data.roleId());
            reloadData();
            showInfo("Da tao tai khoan moi.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onEditClick() {
        User selected = getSelectedUser();
        if (selected == null) {
            showWarning("Vui long chon tai khoan can sua.");
            return;
        }

        Optional<UserFormData> formData = showUserDialog(selected);
        if (formData.isEmpty()) {
            return;
        }

        try {
            UserFormData data = formData.get();
            User updated = new User();
            updated.setUserId(selected.getUserId());
            updated.setUsername(data.username());
            updated.setFullName(data.fullName());
            updated.setEmail(data.email());
            updated.setPhone(data.phone());
            updated.setRoleId(data.roleId());
            updated.setAccountStatus(selected.getAccountStatus());
            updated.setMustChangePassword(selected.isMustChangePassword());

            userService.updateUser(updated);
            reloadData();
            selectUser(updated.getUserId());
            showInfo("Da cap nhat tai khoan.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onLockClick() {
        User selected = getSelectedUser();
        if (selected == null) {
            showWarning("Vui long chon tai khoan.");
            return;
        }

        boolean locked = isLocked(selected);
        if (!locked && isCurrentUser(selected)) {
            showWarning("Khong the khoa chinh tai khoan dang dang nhap.");
            return;
        }

        String action = locked ? "mo khoa" : "khoa";
        if (!confirm("Xac nhan " + action, "Ban co muon " + action + " tai khoan " + selected.getUsername() + "?")) {
            return;
        }

        try {
            if (locked) {
                userService.unlockUser(selected.getUserId());
            } else {
                userService.lockUser(selected.getUserId());
            }

            reloadData();
            selectUser(selected.getUserId());
            showInfo("Da cap nhat trang thai tai khoan.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onRefreshClick() {
        reloadData();
    }

    private void configureTable() {
        colUserId.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getUserId()).asObject());
        colUsername.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getUsername())));
        colFullName.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getFullName())));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getEmail())));
        colPhone.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getPhone())));
        colRole.setCellValueFactory(cell -> new SimpleStringProperty(resolveRoleName(cell.getValue().getRoleId())));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(resolveStatusLabel(cell.getValue().getAccountStatus())));
        colLastLogin.setCellValueFactory(cell -> new SimpleStringProperty(formatDateTime(cell.getValue().getLastLoginAt())));

        userTable.setItems(users);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        userTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && getSelectedUser() != null) {
                onEditClick();
            }
        });
    }

    private void configureSelectionState() {
        editButton.disableProperty().bind(userTable.getSelectionModel().selectedItemProperty().isNull());
        lockButton.disableProperty().bind(userTable.getSelectionModel().selectedItemProperty().isNull());
        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateLockButton(newValue));
        updateLockButton(null);
    }

    private void reloadData() {
        try {
            loadRoles();
            users.setAll(userService.listUsers());
            updateLockButton(getSelectedUser());
        } catch (RuntimeException e) {
            showError("Khong the tai danh sach tai khoan. " + nullToEmpty(e.getMessage()));
        }
    }

    private void loadRoles() {
        roleById.clear();
        List<Role> roles = userService.listActiveRoles();
        for (Role role : roles) {
            roleById.put(role.getRoleId(), role);
        }
    }

    private Optional<UserFormData> showUserDialog(User existingUser) {
        boolean editMode = existingUser != null;

        Dialog<UserFormData> dialog = new Dialog<>();
        dialog.setTitle(editMode ? "Sua tai khoan" : "Them tai khoan");
        dialog.setHeaderText(editMode ? "Cap nhat thong tin tai khoan" : "Tao tai khoan dang nhap moi");

        ButtonType saveButtonType = new ButtonType(editMode ? "Cap nhat" : "Tao moi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField usernameField = new TextField(editMode ? existingUser.getUsername() : "");
        PasswordField passwordField = new PasswordField();
        TextField fullNameField = new TextField(editMode ? existingUser.getFullName() : "");
        TextField emailField = new TextField(editMode ? existingUser.getEmail() : "");
        TextField phoneField = new TextField(editMode ? existingUser.getPhone() : "");
        ComboBox<Role> roleComboBox = new ComboBox<>();

        usernameField.setPromptText("VD: admin");
        passwordField.setPromptText("Mat khau");
        fullNameField.setPromptText("Ho ten");
        emailField.setPromptText("email@fleetcare.local");
        phoneField.setPromptText("So dien thoai");

        roleComboBox.getItems().setAll(roleById.values());
        roleComboBox.setCellFactory(comboBox -> new RoleListCell());
        roleComboBox.setButtonCell(new RoleListCell());
        if (editMode) {
            roleComboBox.setValue(roleById.get(existingUser.getRoleId()));
        }

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(12));
        form.add(new Label("Username"), 0, 0);
        form.add(usernameField, 1, 0);

        int row = 1;
        if (!editMode) {
            form.add(new Label("Mat khau"), 0, row);
            form.add(passwordField, 1, row);
            row++;
        }

        form.add(new Label("Ho ten"), 0, row);
        form.add(fullNameField, 1, row++);
        form.add(new Label("Email"), 0, row);
        form.add(emailField, 1, row++);
        form.add(new Label("Dien thoai"), 0, row);
        form.add(phoneField, 1, row++);
        form.add(new Label("Vai tro"), 0, row);
        form.add(roleComboBox, 1, row);

        usernameField.setPrefWidth(280);
        passwordField.setPrefWidth(280);
        fullNameField.setPrefWidth(280);
        emailField.setPrefWidth(280);
        phoneField.setPrefWidth(280);
        roleComboBox.setPrefWidth(280);

        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            String message = validateForm(editMode, usernameField, passwordField, fullNameField, roleComboBox);
            if (!message.isEmpty()) {
                showWarning(message);
                event.consume();
            }
        });

        dialog.setResultConverter(buttonType -> {
            if (buttonType != saveButtonType) {
                return null;
            }

            Role selectedRole = roleComboBox.getValue();
            return new UserFormData(
                    usernameField.getText(),
                    editMode ? null : passwordField.getText(),
                    fullNameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    selectedRole == null ? null : selectedRole.getRoleId());
        });

        return dialog.showAndWait();
    }

    private String validateForm(boolean editMode,
                                TextField usernameField,
                                PasswordField passwordField,
                                TextField fullNameField,
                                ComboBox<Role> roleComboBox) {
        if (isBlank(usernameField.getText())) {
            return "Vui long nhap username.";
        }
        if (!editMode && isBlank(passwordField.getText())) {
            return "Vui long nhap mat khau.";
        }
        if (isBlank(fullNameField.getText())) {
            return "Vui long nhap ho ten.";
        }
        if (roleComboBox.getValue() == null) {
            return "Vui long chon vai tro.";
        }

        return "";
    }

    private User getSelectedUser() {
        return userTable.getSelectionModel().getSelectedItem();
    }

    private void selectUser(Long userId) {
        if (userId == null) {
            return;
        }

        for (User user : users) {
            if (userId.equals(user.getUserId())) {
                userTable.getSelectionModel().select(user);
                userTable.scrollTo(user);
                return;
            }
        }
    }

    private void updateLockButton(User user) {
        if (lockButton == null) {
            return;
        }

        lockButton.setText(user != null && isLocked(user) ? "Mo khoa" : "Khoa");
    }

    private boolean isCurrentUser(User user) {
        User currentUser = UserSession.getInstance().getCurrentUser();
        return user != null
                && currentUser != null
                && user.getUserId() != null
                && user.getUserId().equals(currentUser.getUserId());
    }

    private boolean isLocked(User user) {
        return user != null && STATUS_LOCKED.equalsIgnoreCase(nullToEmpty(user.getAccountStatus()));
    }

    private String resolveRoleName(Long roleId) {
        Role role = roleById.get(roleId);
        if (role == null) {
            return roleId == null ? "" : "Role #" + roleId;
        }

        return formatRole(role);
    }

    private String formatRole(Role role) {
        String code = nullToEmpty(role.getRoleCode());
        String name = nullToEmpty(role.getRoleName());
        if (name.isEmpty()) {
            return code;
        }
        if (code.isEmpty()) {
            return name;
        }

        return name + " (" + code + ")";
    }

    private String resolveStatusLabel(String status) {
        if (STATUS_LOCKED.equalsIgnoreCase(nullToEmpty(status))) {
            return "LOCKED";
        }

        return STATUS_ACTIVE;
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FORMATTER.format(value);
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Thong bao", message);
    }

    private void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Canh bao", message);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Loi", message == null || message.isBlank() ? "Da co loi xay ra." : message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record UserFormData(String username,
                                String password,
                                String fullName,
                                String email,
                                String phone,
                                Long roleId) {
    }

    private class RoleListCell extends ListCell<Role> {
        @Override
        protected void updateItem(Role item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? "" : formatRole(item));
        }
    }
}
