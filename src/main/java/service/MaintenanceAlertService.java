package service;

import model.dao.MaintenancePlanDAO;
import model.dto.MaintenanceDueAlertDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class MaintenanceAlertService {

    private final MaintenancePlanDAO planDAO = new MaintenancePlanDAO();

    public List<MaintenanceDueAlertDTO> listDueAlerts() {
        return planDAO.findDueAlerts().stream()
                .sorted(Comparator
                        .comparingLong(this::daysUntilDue)
                        .reversed()
                        .thenComparing(alert -> nullToEmpty(alert.getLicensePlate()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparingLong(MaintenanceDueAlertDTO::getPlanId))
                .toList();
    }

    private long daysUntilDue(MaintenanceDueAlertDTO alert) {
        if (alert == null || alert.getNextDueDate() == null) {
            return Long.MIN_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), alert.getNextDueDate());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
