package service;

import model.dao.MaintenancePlanDAO;
import model.dto.MaintenanceDueAlertDTO;

import java.util.List;

public class MaintenanceAlertService {

    private final MaintenancePlanDAO planDAO = new MaintenancePlanDAO();

    public List<MaintenanceDueAlertDTO> listDueAlerts() {
        return planDAO.findDueAlerts();
    }
}
