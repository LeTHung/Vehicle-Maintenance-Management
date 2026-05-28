package service;

import model.dao.DocumentAlertDAO;
import model.dto.DocumentAlertDTO;

import java.util.List;

public class DocumentAlertService {

    private final DocumentAlertDAO documentAlertDAO = new DocumentAlertDAO();

    public List<DocumentAlertDTO> listAlerts() {
        return documentAlertDAO.findAll();
    }

    public List<DocumentAlertDTO> searchAlerts(String keyword, String dueStatus) {
        return documentAlertDAO.search(keyword, dueStatus);
    }

    public int countExpired() {
        return documentAlertDAO.countByStatus("OVERDUE");
    }

    public int countComingDue() {
        return documentAlertDAO.countByStatus("COMING_DUE");
    }
}
