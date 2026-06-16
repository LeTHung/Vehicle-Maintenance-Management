package database;

import model.dao.VehicleDAO;
import model.entity.Vehicle;

import java.util.List;

public class TestVehicleDAO {

    public static void main(String[] args) {
        VehicleDAO vehicleDAO = new VehicleDAO();

        List<Vehicle> vehicles = vehicleDAO.findAll();

        System.out.println("Số xe lấy được: " + vehicles.size());

        for (Vehicle v : vehicles) {
            System.out.println(
                    v.getVehicleId() + " - " +
                            v.getVehicleCode() + " - " +
                            v.getLicensePlate() + " - " +
                            v.getVehicleStatus());
        }
    }
}