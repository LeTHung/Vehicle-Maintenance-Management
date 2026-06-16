package service;

import model.dao.ReportDAO;
import model.dao.VehicleDAO;
import model.dto.MonthlyCostReportDTO;
import model.entity.Vehicle;

import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    public List<Vehicle> listVehicles() {
        return vehicleDAO.findAll();
    }

    public List<MonthlyCostReportDTO> getReport(String year, Long vehicleId) {
        if (vehicleId != null) {
            return reportDAO.findMonthlyCostsByVehicleAndYear(vehicleId, year);
        }
        return reportDAO.findMonthlyCostsByYear(year);
    }
}
