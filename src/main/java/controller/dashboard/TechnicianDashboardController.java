package controller.dashboard;

import javafx.fxml.FXML;

public class TechnicianDashboardController {

    private Runnable openMaintenanceAlertHandler = () -> {};
    private Runnable openMaintenanceRecordHandler = () -> {};

    public void setNavigationHandlers(Runnable openMaintenanceAlertHandler,
                                      Runnable openMaintenanceRecordHandler) {
        this.openMaintenanceAlertHandler = safeHandler(openMaintenanceAlertHandler);
        this.openMaintenanceRecordHandler = safeHandler(openMaintenanceRecordHandler);
    }

    @FXML
    private void handleOpenMaintenanceAlert() {
        openMaintenanceAlertHandler.run();
    }

    @FXML
    private void handleOpenMaintenanceRecord() {
        openMaintenanceRecordHandler.run();
    }

    private Runnable safeHandler(Runnable handler) {
        return handler == null ? () -> {} : handler;
    }
}
