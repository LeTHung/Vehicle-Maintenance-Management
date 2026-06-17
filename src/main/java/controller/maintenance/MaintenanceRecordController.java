package controller.maintenance;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.dao.UserDAO;
import model.entity.MaintenanceItemDetail;
import model.entity.MaintenancePlan;
import model.entity.MaintenanceRecord;
import model.entity.User;
import model.entity.Vehicle;
import session.UserSession;
import service.MaintenancePlanService;
import service.MaintenanceRecordService;
import util.DetailWindow;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MaintenanceRecordController {

    private final MaintenanceRecordService service = new MaintenanceRecordService();
    private final MaintenancePlanService planService = new MaintenancePlanService();
    private final UserDAO userDAO = new UserDAO();

    @FXML private VBox recordDetailPanel;
    @FXML private Label lblRecordDetailTitle;

    @FXML private ComboBox<Vehicle> cbFilterVehicle;
    @FXML private ComboBox<Vehicle> cbVehicle;
    @FXML private ComboBox<MaintenancePlan> cbPlan;
    @FXML private ComboBox<String> cbRecordType;
    @FXML private ComboBox<String> cbRecordStatus;
    @FXML private TextField txtTitle;
    @FXML private DatePicker dpServiceDate;
    @FXML private TextField txtOdometer;
    @FXML private TextField txtTotalCost;
    @FXML private TextField txtServiceProvider;
    @FXML private TextField txtWorkSummary;
    @FXML private TextField txtNotes;

    @FXML private TableView<MaintenanceRecord> tblRecord;
    @FXML private TableColumn<MaintenanceRecord, Long> colRecordId;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordLicensePlate;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordType;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordTitle;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordServiceDate;
    @FXML private TableColumn<MaintenanceRecord, Integer> colRecordOdometer;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordTechnician;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordTotalCost;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordStatus;

    // ─── Form hạng mục ────────────────────────────────────────────────────────
    @FXML private ComboBox<String> cbItemType;
    @FXML private TextField txtItemDesc;
    @FXML private TextField txtItemQty;
    @FXML private TextField txtItemUnit;
    @FXML private TextField txtItemUnitCost;
    @FXML private Label lblItemLineTotal;

    // ─── Bảng hạng mục ────────────────────────────────────────────────────────
    @FXML private TableView<MaintenanceItemDetail> tblItems;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemType;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemDesc;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemQty;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemUnit;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemUnitCost;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemLineTotal;
    @FXML private TableColumn<MaintenanceItemDetail, Void>   colItemRemove;

    private Long selectedRecordId;
    private Long selectedTechnicianId;
    private Long selectedCreatedBy;
    private Stage recordDetailStage;
    private final Map<Long, String> vehicleNameMap = new HashMap<>();
    private final Map<Long, String> technicianNameMap = new HashMap<>();
    private final Map<Integer, String> typeNameMap = new HashMap<>();
    private final ObservableList<MaintenanceItemDetail> currentItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbRecordType.getItems().addAll(recordTypeLabel("PREVENTIVE"), recordTypeLabel("CORRECTIVE"));
        cbRecordStatus.getItems().addAll(
                recordStatusLabel("OPEN"),
                recordStatusLabel("IN_PROGRESS"),
                recordStatusLabel("COMPLETED"),
                recordStatusLabel("CANCELLED"));
        cbItemType.getItems().addAll("WORK", "PART");
        cbItemType.setValue("WORK");
        loadTechnicianNames();
        loadTypeNames();
        setupVehicleComboBoxes();
        setupPlanComboBox();
        configureTable();
        configureItemTable();
        setupFilterListener();
        setupRowSelectionListener();
        setupLineTotalListener();
        setupTotalCostAutoCalc();
        loadTable(service.listAll());
    }

    private void loadTypeNames() {
        typeNameMap.clear();
        planService.listActiveTypes()
                .forEach(t -> typeNameMap.put(t.getMaintenanceTypeId(), t.getMaintenanceName()));
    }

    private void setupPlanComboBox() {
        cbPlan.setConverter(new StringConverter<>() {
            public String toString(MaintenancePlan p) {
                if (p == null) return "";
                String typeName = typeNameMap.getOrDefault(p.getMaintenanceTypeId(),
                        "Loại #" + p.getMaintenanceTypeId());
                return p.getNextDueDate() != null
                        ? typeName + " (đến hạn " + p.getNextDueDate() + ")"
                        : typeName;
            }
            public MaintenancePlan fromString(String s) { return null; }
        });
        // Nạp lại danh sách kế hoạch active mỗi khi đổi xe trong form
        cbVehicle.valueProperty().addListener((obs, oldV, newV) -> reloadPlansForSelectedVehicle());
    }

    private void reloadPlansForSelectedVehicle() {
        Vehicle vehicle = cbVehicle.getValue();
        cbPlan.getItems().clear();
        cbPlan.setValue(null);
        if (vehicle == null) {
            return;
        }
        List<MaintenancePlan> activePlans = planService.listByVehicle(vehicle.getVehicleId()).stream()
                .filter(MaintenancePlan::isActive)
                .toList();
        cbPlan.setItems(FXCollections.observableArrayList(activePlans));
    }

    /** Khi có hạng mục/phụ tùng, Tổng chi phí = tổng thành tiền và khóa nhập tay để tránh lệch số liệu báo cáo. */
    private void setupTotalCostAutoCalc() {
        currentItems.addListener((javafx.collections.ListChangeListener<MaintenanceItemDetail>) c -> recalcTotalCostFromItems());
    }

    private void recalcTotalCostFromItems() {
        if (currentItems.isEmpty()) {
            txtTotalCost.setEditable(true);
            return;
        }
        BigDecimal sum = currentItems.stream()
                .map(i -> i.getLineTotal() != null ? i.getLineTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        txtTotalCost.setText(sum.toPlainString());
        txtTotalCost.setEditable(false);
    }

    private void setupVehicleComboBoxes() {
        List<Vehicle> vehicles = service.listVehicles();
        vehicles.forEach(v -> vehicleNameMap.put(v.getVehicleId(), v.getLicensePlate()));

        StringConverter<Vehicle> converter = new StringConverter<>() {
            public String toString(Vehicle v) { return v == null ? "" : v.getLicensePlate(); }
            public Vehicle fromString(String s) { return null; }
        };

        cbVehicle.setItems(FXCollections.observableArrayList(vehicles));
        cbVehicle.setConverter(converter);

        cbFilterVehicle.setItems(FXCollections.observableArrayList(vehicles));
        cbFilterVehicle.setConverter(converter);
    }

    private void configureTable() {
        colRecordId.setCellValueFactory(c ->
            new SimpleLongProperty(c.getValue().getRecordId()).asObject());
        colRecordLicensePlate.setCellValueFactory(c ->
            new SimpleStringProperty(vehicleNameMap.getOrDefault(c.getValue().getVehicleId(), "")));
        colRecordType.setCellValueFactory(c ->
            new SimpleStringProperty(recordTypeLabel(c.getValue().getRecordType())));
        colRecordTitle.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getTitle() == null ? "" : c.getValue().getTitle()));
        colRecordServiceDate.setCellValueFactory(c -> {
            LocalDate d = c.getValue().getServiceDate();
            return new SimpleStringProperty(d == null ? "" : d.toString());
        });
        colRecordOdometer.setCellValueFactory(c ->
            new SimpleObjectProperty<>(c.getValue().getOdometer()));
        colRecordTechnician.setCellValueFactory(c ->
            new SimpleStringProperty(resolveTechnicianName(c.getValue().getTechnicianId())));
        colRecordTotalCost.setCellValueFactory(c -> {
            BigDecimal cost = c.getValue().getTotalCost();
            return new SimpleStringProperty(cost == null ? "" : cost.toPlainString());
        });
        colRecordStatus.setCellValueFactory(c ->
            new SimpleStringProperty(recordStatusLabel(c.getValue().getRecordStatus())));
    }

    private void configureItemTable() {
        colItemType.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getItemType() != null ? c.getValue().getItemType() : ""));
        colItemDesc.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getDescription() != null ? c.getValue().getDescription() : ""));
        colItemQty.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getQuantity() != null ? c.getValue().getQuantity().toPlainString() : ""));
        colItemUnit.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getUnit() != null ? c.getValue().getUnit() : ""));
        colItemUnitCost.setCellValueFactory(c ->
            new SimpleStringProperty(formatMoney(c.getValue().getUnitCost())));
        colItemLineTotal.setCellValueFactory(c ->
            new SimpleStringProperty(formatMoney(c.getValue().getLineTotal())));

        colItemRemove.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Xóa");
            {
                btn.setOnAction(e -> {
                    MaintenanceItemDetail item = getTableRow() == null ? null : getTableRow().getItem();
                    if (item != null) currentItems.remove(item);
                });
                btn.getStyleClass().add("btn-ghost");
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tblItems.setItems(currentItems);
    }

    private void setupLineTotalListener() {
        txtItemQty.textProperty().addListener((obs, o, n) -> updateLineTotalLabel());
        txtItemUnitCost.textProperty().addListener((obs, o, n) -> updateLineTotalLabel());
    }

    private void updateLineTotalLabel() {
        try {
            BigDecimal qty = new BigDecimal(txtItemQty.getText().trim());
            BigDecimal unitCost = new BigDecimal(txtItemUnitCost.getText().trim());
            lblItemLineTotal.setText("= " + formatMoney(qty.multiply(unitCost)) + " VNĐ");
        } catch (NumberFormatException e) {
            lblItemLineTotal.setText("= 0 VNĐ");
        }
    }

    private void setupFilterListener() {
        cbFilterVehicle.setOnAction(e -> {
            Vehicle selected = cbFilterVehicle.getValue();
            if (selected == null) {
                loadTable(service.listAll());
            } else {
                loadTable(service.listByVehicle(selected.getVehicleId()));
            }
        });
    }

    private void loadTechnicianNames() {
        technicianNameMap.clear();
        userDAO.findAll().forEach(user -> {
            if (user.getUserId() != null) {
                technicianNameMap.put(user.getUserId(), resolveUserDisplayName(user));
            }
        });
    }

    private void setupRowSelectionListener() {
        tblRecord.setRowFactory(table -> {
            TableRow<MaintenanceRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    populateForm(row.getItem());
                    showRecordDetailWindow();
                }
            });
            return row;
        });
    }

    private void loadTable(List<MaintenanceRecord> records) {
        tblRecord.setItems(FXCollections.observableArrayList(records));
    }

    private void populateForm(MaintenanceRecord record) {
        if (record == null) return;
        selectedRecordId = record.getRecordId();
        selectedTechnicianId = record.getTechnicianId();
        selectedCreatedBy = record.getCreatedBy();
        lblRecordDetailTitle.setText("Chi tiáº¿t phiáº¿u: "
                + (record.getTitle() == null || record.getTitle().isBlank()
                ? "#" + record.getRecordId()
                : record.getTitle()));

        // setValue kích hoạt listener nạp lại danh sách kế hoạch active của xe
        cbVehicle.getItems().stream()
            .filter(v -> v.getVehicleId() == record.getVehicleId())
            .findFirst().ifPresent(cbVehicle::setValue);

        if (record.getPlanId() != null) {
            cbPlan.getItems().stream()
                .filter(p -> p.getPlanId() == record.getPlanId())
                .findFirst().ifPresent(cbPlan::setValue);
        }

        cbRecordType.setValue(recordTypeLabel(record.getRecordType()));
        cbRecordStatus.setValue(recordStatusLabel(record.getRecordStatus()));
        txtTitle.setText(record.getTitle() == null ? "" : record.getTitle());
        dpServiceDate.setValue(record.getServiceDate());
        txtOdometer.setText(record.getOdometer() == null ? "" : String.valueOf(record.getOdometer()));
        txtTotalCost.setText(record.getTotalCost() == null ? "" : record.getTotalCost().toPlainString());
        txtServiceProvider.setText(record.getServiceProviderName() == null ? "" : record.getServiceProviderName());
        txtWorkSummary.setText(record.getWorkSummary() == null ? "" : record.getWorkSummary());
        txtNotes.setText(record.getNotes() == null ? "" : record.getNotes());

        currentItems.setAll(service.listItems(record.getRecordId()));
    }

    @FXML
    private void handleNewRecord() {
        handleClear();
        lblRecordDetailTitle.setText("Táº¡o phiáº¿u báº£o dÆ°á»¡ng má»›i");
        showRecordDetailWindow();
    }

    @FXML
    private void handleSave() {
        try {
            MaintenanceRecord record = readFromForm();
            applyItemsTotal(record);
            validateCompletionEffects(record);
            Long currentUserId = requireCurrentUserId();
            record.setTechnicianId(currentUserId);
            record.setCreatedBy(currentUserId);
            record.setUpdatedBy(currentUserId);
            int itemCount = currentItems.size();
            Long recordId = service.save(record);
            for (MaintenanceItemDetail item : currentItems) {
                item.setRecordId(recordId);
                service.saveItem(item);
            }
            String extra = applyCompletionEffects(record, currentUserId);
            loadTable(service.listAll());
            handleClear();
            DetailWindow.hide(recordDetailStage);
            showInfo("Đã lưu phiếu bảo dưỡng (" + itemCount + " hạng mục)." + extra);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Lỗi khi lưu: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedRecordId == null) {
            showError("Vui lòng chọn phiếu cần cập nhật.");
            return;
        }
        try {
            MaintenanceRecord record = readFromForm();
            record.setRecordId(selectedRecordId);
            applyItemsTotal(record);
            validateCompletionEffects(record);
            Long currentUserId = requireCurrentUserId();
            record.setTechnicianId(selectedTechnicianId != null ? selectedTechnicianId : currentUserId);
            record.setCreatedBy(selectedCreatedBy);
            record.setUpdatedBy(currentUserId);
            service.update(record);
            service.deleteItems(selectedRecordId);
            for (MaintenanceItemDetail item : currentItems) {
                item.setRecordId(selectedRecordId);
                service.saveItem(item);
            }
            String extra = applyCompletionEffects(record, currentUserId);
            loadTable(service.listAll());
            handleClear();
            DetailWindow.hide(recordDetailStage);
            showInfo("Đã cập nhật phiếu bảo dưỡng." + extra);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        cbVehicle.setValue(null);
        cbPlan.setValue(null);
        cbRecordType.setValue(null);
        cbRecordStatus.setValue(null);
        txtTitle.clear();
        dpServiceDate.setValue(null);
        txtOdometer.clear();
        txtTotalCost.clear();
        txtServiceProvider.clear();
        txtWorkSummary.clear();
        txtNotes.clear();
        currentItems.clear();
        clearItemForm();
        selectedRecordId = null;
        selectedTechnicianId = null;
        selectedCreatedBy = null;
        tblRecord.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        cbFilterVehicle.setValue(null);
        handleClear();
        DetailWindow.hide(recordDetailStage);
        loadTable(service.listAll());
    }

    private void showRecordDetailWindow() {
        recordDetailStage = DetailWindow.show(
                recordDetailStage,
                recordDetailPanel,
                tblRecord,
                "ThÃ´ng tin phiáº¿u báº£o dÆ°á»¡ng",
                1080,
                760);
    }

    @FXML
    private void handleAddItem() {
        String desc = txtItemDesc.getText().trim();
        if (desc.isBlank()) { showError("Vui lòng nhập mô tả hạng mục."); return; }

        BigDecimal qty;
        BigDecimal unitCost;
        try {
            qty = new BigDecimal(txtItemQty.getText().trim());
        } catch (NumberFormatException e) { showError("Số lượng phải là số."); return; }
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Số lượng hạng mục phải lớn hơn 0.");
            return;
        }
        try {
            unitCost = new BigDecimal(txtItemUnitCost.getText().trim());
        } catch (NumberFormatException e) { showError("Đơn giá phải là số."); return; }
        if (unitCost.compareTo(BigDecimal.ZERO) < 0) {
            showError("Đơn giá hạng mục không được âm.");
            return;
        }

        MaintenanceItemDetail item = new MaintenanceItemDetail();
        item.setItemType(cbItemType.getValue() != null ? cbItemType.getValue() : "WORK");
        item.setDescription(desc);
        item.setQuantity(qty);
        item.setUnit(txtItemUnit.getText().isBlank() ? null : txtItemUnit.getText().trim());
        item.setUnitCost(unitCost);
        item.setLineTotal(qty.multiply(unitCost));

        currentItems.add(item);
        clearItemForm();
    }

    @FXML
    private void handleClearItems() {
        currentItems.clear();
    }

    private void clearItemForm() {
        txtItemDesc.clear();
        txtItemQty.clear();
        txtItemUnit.clear();
        txtItemUnitCost.clear();
        lblItemLineTotal.setText("= 0 VNĐ");
        cbItemType.setValue("WORK");
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0";
        return NumberFormat.getNumberInstance(Locale.of("vi", "VN")).format(value);
    }

    private MaintenanceRecord readFromForm() {
        Vehicle vehicle = cbVehicle.getValue();
        if (vehicle == null) throw new IllegalArgumentException("Vui lòng chọn xe.");

        String recordType = recordTypeValue(cbRecordType.getValue());
        if (recordType == null) throw new IllegalArgumentException("Vui lòng chọn loại phiếu.");

        LocalDate serviceDate = dpServiceDate.getValue();
        if (serviceDate == null) throw new IllegalArgumentException("Vui lòng nhập ngày thực hiện.");

        MaintenanceRecord record = new MaintenanceRecord();
        record.setVehicleId(vehicle.getVehicleId());
        record.setRecordType(recordType);
        record.setRecordStatus(cbRecordStatus.getValue() != null ? recordStatusValue(cbRecordStatus.getValue()) : "OPEN");
        record.setTitle(txtTitle.getText().isBlank() ? null : txtTitle.getText().trim());
        record.setServiceDate(serviceDate);
        record.setOdometer(parseOptionalInt(txtOdometer.getText(), "ODO"));
        record.setTotalCost(parseOptionalBigDecimal(txtTotalCost.getText(), "Tổng chi phí"));
        record.setServiceProviderName(txtServiceProvider.getText().isBlank() ? null : txtServiceProvider.getText().trim());
        record.setWorkSummary(txtWorkSummary.getText().isBlank() ? null : txtWorkSummary.getText().trim());
        record.setNotes(txtNotes.getText().isBlank() ? null : txtNotes.getText().trim());

        MaintenancePlan plan = cbPlan.getValue();
        record.setPlanId(plan != null ? plan.getPlanId() : null);
        return record;
    }

    /** Nếu phiếu có hạng mục/phụ tùng thì tổng chi phí = tổng thành tiền (ưu tiên hơn ô nhập tay). */
    private void applyItemsTotal(MaintenanceRecord record) {
        if (currentItems.isEmpty()) {
            return;
        }
        BigDecimal sum = currentItems.stream()
                .map(i -> i.getLineTotal() != null ? i.getLineTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        record.setTotalCost(sum);
    }

    private void validateCompletionEffects(MaintenanceRecord record) {
        if (!"COMPLETED".equals(record.getRecordStatus())) {
            return;
        }
        planService.validateServiceCompletion(
                record.getPlanId(),
                record.getVehicleId(),
                record.getServiceDate(),
                record.getOdometer());
    }

    /**
     * Khi phiếu ở trạng thái "Hoàn thành": đóng chu kỳ kế hoạch liên quan (dời mốc đến hạn kế tiếp)
     * và cập nhật ODO hiện tại của xe. Trả về phần mô tả thêm cho thông báo thành công.
     */
    private String applyCompletionEffects(MaintenanceRecord record, Long currentUserId) {
        if (!"COMPLETED".equals(record.getRecordStatus())) {
            return "";
        }
        StringBuilder note = new StringBuilder();
        if (record.getPlanId() != null
                && planService.markServiced(record.getPlanId(), record.getServiceDate(),
                                            record.getOdometer(), currentUserId)) {
            note.append(" Đã dời mốc kế hoạch sang kỳ kế tiếp.");
        }
        if (record.getOdometer() != null
                && service.updateVehicleOdometer(record.getVehicleId(), record.getOdometer())) {
            note.append(" Đã cập nhật ODO xe.");
        }
        return note.toString();
    }

    private Long requireCurrentUserId() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getUserId() == null || currentUser.getUserId() <= 0) {
            throw new IllegalArgumentException("Không xác định được nhân viên kỹ thuật đang đăng nhập.");
        }
        return currentUser.getUserId();
    }

    private String resolveTechnicianName(Long technicianId) {
        if (technicianId == null) {
            return "";
        }
        return technicianNameMap.getOrDefault(technicianId, "#" + technicianId);
    }

    private String resolveUserDisplayName(User user) {
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName().trim();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername().trim();
        }
        return "#" + user.getUserId();
    }

    private Integer parseOptionalInt(String text, String fieldName) {
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên.");
        }
    }

    private BigDecimal parseOptionalBigDecimal(String text, String fieldName) {
        if (text == null || text.isBlank()) return null;
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private String recordTypeLabel(String type) {
        if (type == null || type.isBlank()) {
            return "";
        }
        return switch (type) {
            case "PREVENTIVE" -> "Bảo dưỡng định kỳ";
            case "CORRECTIVE" -> "Sửa chữa phát sinh";
            default -> type;
        };
    }

    private String recordTypeValue(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        return switch (label) {
            case "Bảo dưỡng định kỳ" -> "PREVENTIVE";
            case "Sửa chữa phát sinh" -> "CORRECTIVE";
            default -> label;
        };
    }

    private String recordStatusLabel(String status) {
        if (status == null || status.isBlank()) {
            return "";
        }
        return switch (status) {
            case "OPEN" -> "Chờ xử lý";
            case "IN_PROGRESS" -> "Đang xử lý";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private String recordStatusValue(String label) {
        if (label == null || label.isBlank()) {
            return "";
        }
        return switch (label) {
            case "Chờ xử lý" -> "OPEN";
            case "Đang xử lý" -> "IN_PROGRESS";
            case "Hoàn thành" -> "COMPLETED";
            case "Đã hủy" -> "CANCELLED";
            default -> label;
        };
    }
}
