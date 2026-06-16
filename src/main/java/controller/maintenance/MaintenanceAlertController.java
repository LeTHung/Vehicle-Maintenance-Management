package controller.maintenance;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.dto.MaintenanceDueAlertDTO;
import service.MaintenanceAlertService;

import java.util.List;

public class MaintenanceAlertController {

    private final MaintenanceAlertService service = new MaintenanceAlertService();

    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private Label lblOverdueCount;
    @FXML private Label lblComingDueCount;

    @FXML private TableView<MaintenanceDueAlertDTO> tblAlert;
    @FXML private TableColumn<MaintenanceDueAlertDTO, Long>   colAlertPlanId;
    @FXML private TableColumn<MaintenanceDueAlertDTO, String> colAlertLicensePlate;
    @FXML private TableColumn<MaintenanceDueAlertDTO, String> colAlertVehicleType;
    @FXML private TableColumn<MaintenanceDueAlertDTO, String> colAlertMaintenanceName;
    @FXML private TableColumn<MaintenanceDueAlertDTO, String> colAlertCurrentOdo;
    @FXML private TableColumn<MaintenanceDueAlertDTO, String> colAlertNextDueDate;
    @FXML private TableColumn<MaintenanceDueAlertDTO, String> colAlertNextDueOdo;
    @FXML private TableColumn<MaintenanceDueAlertDTO, String> colAlertStatus;

    @FXML
    public void initialize() {
        cbFilterStatus.getItems().addAll("Tất cả", "OVERDUE", "COMING_DUE");
        cbFilterStatus.setValue("Tất cả");
        cbFilterStatus.setOnAction(e -> applyFilter());

        configureTable();
        loadTable();
    }

    private void configureTable() {
        colAlertPlanId.setCellValueFactory(c ->
            new SimpleLongProperty(c.getValue().getPlanId()).asObject());

        colAlertLicensePlate.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getLicensePlate()));

        colAlertVehicleType.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getVehicleType()));

        colAlertMaintenanceName.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getMaintenanceName()));

        colAlertCurrentOdo.setCellValueFactory(c -> {
            Integer odo = c.getValue().getCurrentOdometer();
            return new SimpleStringProperty(odo == null ? "" : String.format("%,d km", odo));
        });

        colAlertNextDueDate.setCellValueFactory(c -> {
            var date = c.getValue().getNextDueDate();
            return new SimpleStringProperty(date == null ? "" : date.toString());
        });

        colAlertNextDueOdo.setCellValueFactory(c -> {
            Integer odo = c.getValue().getNextDueOdometer();
            return new SimpleStringProperty(odo == null ? "" : String.format("%,d km", odo));
        });

        colAlertStatus.setCellValueFactory(c ->
            new SimpleStringProperty(formatStatus(c.getValue().getDueStatus())));

        colAlertStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                getStyleClass().removeAll("cell-overdue", "cell-coming-due");
                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status);
                    MaintenanceDueAlertDTO row = getTableRow() == null ? null : getTableRow().getItem();
                    if (row != null) {
                        if ("OVERDUE".equals(row.getDueStatus())) getStyleClass().add("cell-overdue");
                        else if ("COMING_DUE".equals(row.getDueStatus())) getStyleClass().add("cell-coming-due");
                    }
                }
            }
        });
    }

    private void loadTable() {
        List<MaintenanceDueAlertDTO> all = service.listDueAlerts();
        updateCounters(all);

        String filter = cbFilterStatus.getValue();
        List<MaintenanceDueAlertDTO> displayed = "Tất cả".equals(filter)
            ? all
            : all.stream().filter(d -> filter.equals(d.getDueStatus())).toList();

        tblAlert.setItems(FXCollections.observableArrayList(displayed));
    }

    private void applyFilter() {
        loadTable();
    }

    @FXML
    private void handleRefresh() {
        cbFilterStatus.setValue("Tất cả");
        loadTable();
    }

    private void updateCounters(List<MaintenanceDueAlertDTO> list) {
        long overdue    = list.stream().filter(d -> "OVERDUE".equals(d.getDueStatus())).count();
        long comingDue  = list.stream().filter(d -> "COMING_DUE".equals(d.getDueStatus())).count();
        lblOverdueCount.setText(String.valueOf(overdue));
        lblComingDueCount.setText(String.valueOf(comingDue));
    }

    private String formatStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "OVERDUE"    -> "Quá hạn";
            case "COMING_DUE" -> "Sắp đến hạn";
            default           -> status;
        };
    }
}
