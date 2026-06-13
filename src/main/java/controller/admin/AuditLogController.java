package controller.admin;

import controller.layout.QuickSearchAware;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.entity.AuditLog;
import service.AuditLogService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TreeSet;

public class AuditLogController implements QuickSearchAware {

    private static final String ALL_ACTIONS = "Tất cả hành động";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final AuditLogService auditLogService = new AuditLogService();
    private final ObservableList<AuditLog> allLogs = FXCollections.observableArrayList();
    private final ObservableList<AuditLog> filteredLogs = FXCollections.observableArrayList();

    @FXML private Label errorLabel;
    @FXML private ComboBox<String> cbActionFilter;
    @FXML private DatePicker dpFromDate;
    @FXML private DatePicker dpToDate;
    @FXML private TableView<AuditLog> auditLogTable;
    @FXML private TableColumn<AuditLog, Long> colAuditLogId;
    @FXML private TableColumn<AuditLog, String> colUsername;
    @FXML private TableColumn<AuditLog, String> colAction;
    @FXML private TableColumn<AuditLog, String> colEntity;
    @FXML private TableColumn<AuditLog, String> colDescription;
    @FXML private TableColumn<AuditLog, String> colCreatedAt;

    private String currentKeyword = "";

    @FXML
    public void initialize() {
        configureTable();
        configureFilters();
        hideError();
        reloadData();
    }

    @FXML
    private void onRefreshClick() {
        reloadData();
    }

    @FXML
    private void onFilterChanged() {
        applyFilter();
    }

    @FXML
    private void onClearFiltersClick() {
        cbActionFilter.setValue(ALL_ACTIONS);
        dpFromDate.setValue(null);
        dpToDate.setValue(null);
        applyFilter();
    }

    @Override
    public void applyQuickSearch(String keyword) {
        currentKeyword = keyword == null ? "" : keyword.trim();
        applyFilter();
    }

    private void configureTable() {
        colAuditLogId.setCellValueFactory(cell ->
                new SimpleLongProperty(nullToZero(cell.getValue().getAuditLogId())).asObject());
        colUsername.setCellValueFactory(cell ->
                new SimpleStringProperty(formatUser(cell.getValue())));
        colAction.setCellValueFactory(cell ->
                new SimpleStringProperty(nullToEmpty(cell.getValue().getAction())));
        colEntity.setCellValueFactory(cell ->
                new SimpleStringProperty(formatEntity(cell.getValue())));
        colDescription.setCellValueFactory(cell ->
                new SimpleStringProperty(nullToEmpty(cell.getValue().getDescription())));
        colCreatedAt.setCellValueFactory(cell ->
                new SimpleStringProperty(formatDateTime(cell.getValue().getCreatedAt())));

        auditLogTable.setItems(filteredLogs);
        auditLogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        updateTablePlaceholder();
    }

    private void configureFilters() {
        cbActionFilter.setItems(FXCollections.observableArrayList(ALL_ACTIONS));
        cbActionFilter.setValue(ALL_ACTIONS);
    }

    private void reloadData() {
        try {
            hideError();
            allLogs.setAll(auditLogService.listRecentLogs());
            populateActionFilter();
            applyFilter();
        } catch (RuntimeException e) {
            allLogs.clear();
            filteredLogs.clear();
            showError("Không thể tải nhật ký hoạt động");
        }
    }

    private void applyFilter() {
        String keyword = currentKeyword.toLowerCase(Locale.ROOT);
        String selectedAction = cbActionFilter.getValue();
        LocalDate fromDate = dpFromDate.getValue();
        LocalDate toDate = dpToDate.getValue();

        filteredLogs.setAll(allLogs.stream()
                .filter(log -> matchesKeyword(log, keyword))
                .filter(log -> matchesAction(log, selectedAction))
                .filter(log -> matchesDateRange(log, fromDate, toDate))
                .toList());
        updateTablePlaceholder();
    }

    private void populateActionFilter() {
        String previousAction = cbActionFilter.getValue();
        TreeSet<String> actions = new TreeSet<>();
        for (AuditLog log : allLogs) {
            String action = nullToEmpty(log.getAction());
            if (!action.isBlank()) {
                actions.add(action);
            }
        }

        cbActionFilter.getItems().setAll(ALL_ACTIONS);
        cbActionFilter.getItems().addAll(actions);
        cbActionFilter.setValue(cbActionFilter.getItems().contains(previousAction) ? previousAction : ALL_ACTIONS);
    }

    private boolean matchesKeyword(AuditLog log, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }

        return formatUser(log).toLowerCase(Locale.ROOT).contains(keyword)
                || nullToEmpty(log.getAction()).toLowerCase(Locale.ROOT).contains(keyword)
                || formatEntity(log).toLowerCase(Locale.ROOT).contains(keyword)
                || nullToEmpty(log.getDescription()).toLowerCase(Locale.ROOT).contains(keyword)
                || formatDateTime(log.getCreatedAt()).toLowerCase(Locale.ROOT).contains(keyword);
    }

    private boolean matchesAction(AuditLog log, String selectedAction) {
        return selectedAction == null
                || ALL_ACTIONS.equals(selectedAction)
                || selectedAction.equals(log.getAction());
    }

    private boolean matchesDateRange(AuditLog log, LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null && toDate == null) {
            return true;
        }

        LocalDate createdDate = log.getCreatedAt() == null ? null : log.getCreatedAt().toLocalDate();
        if (createdDate == null) {
            return false;
        }
        if (fromDate != null && createdDate.isBefore(fromDate)) {
            return false;
        }

        return toDate == null || !createdDate.isAfter(toDate);
    }

    private String formatUser(AuditLog log) {
        String username = nullToEmpty(log.getUsername());
        Long userId = log.getUserId();
        if (!username.isBlank() && userId != null) {
            return username + " (#" + userId + ")";
        }
        if (!username.isBlank()) {
            return username;
        }
        if (userId != null) {
            return "#" + userId;
        }
        return "Hệ thống";
    }

    private String formatEntity(AuditLog log) {
        String entityType = nullToEmpty(log.getEntityType());
        String entityId = nullToEmpty(log.getEntityId());
        if (entityType.isBlank()) {
            return entityId;
        }
        if (entityId.isBlank()) {
            return entityType;
        }
        return entityType + " #" + entityId;
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FORMATTER.format(value);
    }

    private long nullToZero(Long value) {
        return value == null ? 0L : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        auditLogTable.setPlaceholder(new Label(message));
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void updateTablePlaceholder() {
        String message = allLogs.isEmpty()
                ? "Không có dữ liệu nhật ký"
                : "Không tìm thấy nhật ký phù hợp";
        auditLogTable.setPlaceholder(new Label(message));
    }
}
