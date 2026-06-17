package controller.maintenance;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import model.dao.ReportDAO;
import model.dao.VehicleDAO;
import model.dto.MonthlyCostReportDTO;
import model.entity.Vehicle;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ReportController {

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

    private final ReportDAO reportDAO = new ReportDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    @FXML
    public void initialize() {
        configureYearFilter();
        configureVehicleFilter();
        configureTable();
        loadReportData();
    }

    @FXML
    private void handleSearch() {
        loadReportData();
    }

    @FXML
    private void handleRefresh() {
        cbVehicle.setValue(null);
        cbYear.setValue(defaultYear());
        loadReportData();
    }

    private void configureYearFilter() {
        int year = LocalDate.now().getYear();
        cbYear.getItems().setAll(
                String.valueOf(year - 2),
                String.valueOf(year - 1),
                String.valueOf(year),
                String.valueOf(year + 1));
        cbYear.setValue(defaultYear());
    }

    private void configureVehicleFilter() {
        cbVehicle.setItems(FXCollections.observableArrayList(vehicleDAO.findAll()));
        cbVehicle.setConverter(new StringConverter<>() {
            @Override
            public String toString(Vehicle vehicle) {
                return vehicle == null ? "" : vehicle.getLicensePlate();
            }

            @Override
            public Vehicle fromString(String value) {
                return null;
            }
        });
    }

    private void configureTable() {
        colReportLicensePlate.setCellValueFactory(cell ->
                new SimpleStringProperty(nullToEmpty(cell.getValue().getLicensePlate())));
        colReportPeriod.setCellValueFactory(cell ->
                new SimpleStringProperty(nullToEmpty(cell.getValue().getPeriodYm())));
        colReportMaintenanceCost.setCellValueFactory(cell ->
                new SimpleStringProperty(formatCurrency(cell.getValue().getMaintenanceCost())));
        colReportDocumentCost.setCellValueFactory(cell ->
                new SimpleStringProperty(formatCurrency(cell.getValue().getDocumentCost())));
        colReportTotalCost.setCellValueFactory(cell ->
                new SimpleStringProperty(formatCurrency(cell.getValue().getTotalCost())));
    }

    private void loadReportData() {
        String year = cbYear.getValue() == null || cbYear.getValue().isBlank()
                ? defaultYear()
                : cbYear.getValue();
        Vehicle vehicle = cbVehicle.getValue();
        List<MonthlyCostReportDTO> rows = vehicle == null
                ? reportDAO.findMonthlyCostsByYear(year)
                : reportDAO.findMonthlyCostsByVehicleAndYear(vehicle.getVehicleId(), year);

        tblReport.setItems(FXCollections.observableArrayList(rows));
        updateSummary(rows);
    }

    private void updateSummary(List<MonthlyCostReportDTO> rows) {
        BigDecimal maintenanceTotal = BigDecimal.ZERO;
        BigDecimal documentTotal = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (MonthlyCostReportDTO row : rows) {
            maintenanceTotal = maintenanceTotal.add(nonNull(row.getMaintenanceCost()));
            documentTotal = documentTotal.add(nonNull(row.getDocumentCost()));
            grandTotal = grandTotal.add(nonNull(row.getTotalCost()));
        }

        lblTotalMaintenanceCost.setText(formatCurrency(maintenanceTotal));
        lblTotalDocumentCost.setText(formatCurrency(documentTotal));
        lblGrandTotal.setText(formatCurrency(grandTotal));
    }

    private BigDecimal nonNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String formatCurrency(BigDecimal value) {
        return currencyFormat.format(nonNull(value)) + " VNĐ";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String defaultYear() {
        return String.valueOf(LocalDate.now().getYear());
    }
}
