package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.dao.DocumentAlertDAO;
import model.dao.VehicleDAO;

public class DashboardController {

    @FXML
    private Label lblTotalVehicleCount;
    @FXML
    private Label lblComingDueCount;
    @FXML
    private Label lblOverdueCount;

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DocumentAlertDAO documentAlertDAO = new DocumentAlertDAO();

    @FXML
    public void initialize() {
        lblTotalVehicleCount.setText(String.valueOf(vehicleDAO.findAll().size()));
        lblComingDueCount.setText(String.valueOf(documentAlertDAO.countByStatus("COMING_DUE")));
        lblOverdueCount.setText(String.valueOf(documentAlertDAO.countByStatus("OVERDUE")));
    }
}
