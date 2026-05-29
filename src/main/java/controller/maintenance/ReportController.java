package controller.maintenance;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.dto.MonthlyCostReportDTO;

public class ReportController {

    @FXML private ComboBox<String> cbYear;
    @FXML private ComboBox<String> cbVehicle;

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
        // TODO: load vehicles into cbVehicle, bind table columns, load report data
    }

    @FXML
    private void handleSearch() {
        // TODO: call ReportService with selected year/vehicle, update table and summary labels
    }

    @FXML
    private void handleRefresh() {
        cbVehicle.setValue(null);
        cbYear.setValue("2026");
        lblTotalMaintenanceCost.setText("0 VNĐ");
        lblTotalDocumentCost.setText("0 VNĐ");
        lblGrandTotal.setText("0 VNĐ");
        tblReport.getItems().clear();
    }
}
