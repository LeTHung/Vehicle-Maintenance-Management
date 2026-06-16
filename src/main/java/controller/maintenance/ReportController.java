package controller.maintenance;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import model.dto.MonthlyCostReportDTO;
import model.entity.Vehicle;
import service.ReportService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ReportController {

    private final ReportService service = new ReportService();

    @FXML private ComboBox<String> cbYear;
    @FXML private ComboBox<Vehicle> cbVehicle;

    @FXML private Label lblTotalMaintenanceCost;
    @FXML private Label lblTotalDocumentCost;
    @FXML private Label lblGrandTotal;

    @FXML private TableView<MonthlyCostReportDTO> tblReport;
    @FXML private TableColumn<MonthlyCostReportDTO, String> colReportLicensePlate;
    @FXML private TableColumn<MonthlyCostReportDTO, String> colReportPeriod;
    @FXML private TableColumn<MonthlyCostReportDTO, String> colReportMaintenanceCost;
    @FXML private TableColumn<MonthlyCostReportDTO, String> colReportDocumentCost;
    @FXML private TableColumn<MonthlyCostReportDTO, String> colReportTotalCost;

    @FXML
    public void initialize() {
        cbYear.getItems().addAll("2024", "2025", "2026");
        cbYear.setValue("2026");

        List<Vehicle> vehicles = service.listVehicles();
        cbVehicle.setItems(FXCollections.observableArrayList(vehicles));
        cbVehicle.setConverter(new StringConverter<>() {
            public String toString(Vehicle v) { return v == null ? "Tất cả xe" : v.getLicensePlate(); }
            public Vehicle fromString(String s) { return null; }
        });

        configureTable();
        handleSearch();
    }

    private void configureTable() {
        colReportLicensePlate.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getLicensePlate()));
        colReportPeriod.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getPeriodYm()));
        colReportMaintenanceCost.setCellValueFactory(c ->
            new SimpleStringProperty(formatMoney(c.getValue().getMaintenanceCost())));
        colReportDocumentCost.setCellValueFactory(c ->
            new SimpleStringProperty(formatMoney(c.getValue().getDocumentCost())));
        colReportTotalCost.setCellValueFactory(c ->
            new SimpleStringProperty(formatMoney(c.getValue().getTotalCost())));
    }

    @FXML
    private void handleSearch() {
        String year = cbYear.getValue() != null ? cbYear.getValue() : "2026";
        Vehicle vehicle = cbVehicle.getValue();
        Long vehicleId = vehicle != null ? vehicle.getVehicleId() : null;

        List<MonthlyCostReportDTO> data = service.getReport(year, vehicleId);
        tblReport.setItems(FXCollections.observableArrayList(data));
        updateSummary(data);
    }

    @FXML
    private void handleRefresh() {
        cbVehicle.setValue(null);
        cbYear.setValue("2026");
        handleSearch();
    }

    private void updateSummary(List<MonthlyCostReportDTO> data) {
        BigDecimal maintenance = data.stream()
            .map(d -> d.getMaintenanceCost() != null ? d.getMaintenanceCost() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal document = data.stream()
            .map(d -> d.getDocumentCost() != null ? d.getDocumentCost() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal grand = maintenance.add(document);

        lblTotalMaintenanceCost.setText(formatMoney(maintenance) + " VNĐ");
        lblTotalDocumentCost.setText(formatMoney(document) + " VNĐ");
        lblGrandTotal.setText(formatMoney(grand) + " VNĐ");
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0";
        return NumberFormat.getNumberInstance(Locale.of("vi", "VN")).format(value);
    }
}
