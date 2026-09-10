package com.traffic;

import com.traffic.exception.DuplicateChallanException;
import com.traffic.exception.InvalidVehicleException;
import com.traffic.model.Challan;
import com.traffic.model.Vehicle;
import com.traffic.model.VehicleType;
import com.traffic.model.ViolationType;
import com.traffic.service.ChallanService;
import com.traffic.service.VehicleService;

import java.time.LocalDateTime;

public class App {

    public static void main(String[] args) {

        VehicleService vehicleService = new VehicleService();

        ChallanService challanService =
                new ChallanService(vehicleService);

        try {

            // Register vehicles
            vehicleService.registerVehicle(
                    "TN01AB1234",
                    "Ravi Kumar",
                    "9876543210",
                    VehicleType.CAR);

            vehicleService.registerVehicle(
                    "TN02CD5678",
                    "Priya Sharma",
                    "9876501234",
                    VehicleType.BIKE);

            vehicleService.registerVehicle(
                    "TN03EF9012",
                    "Arun Kumar",
                    "9876512345",
                    VehicleType.BUS);

            LocalDateTime time1 = LocalDateTime.now();

            // Over-speeding
            Challan challan1 =
                    challanService.generateChallan(
                            "TN01AB1234",
                            ViolationType.OVER_SPEEDING,
                            "Chennai GST Road",
                            time1,
                            85,
                            60);

            System.out.println(challan1);

            // Signal violation
            Challan challan2 =
                    challanService.generateChallan(
                            "TN02CD5678",
                            ViolationType.SIGNAL_VIOLATION,
                            "Anna Salai",
                            LocalDateTime.now(),
                            0,
                            50);

            System.out.println(challan2);

            // Illegal parking
            Challan challan3 =
                    challanService.generateChallan(
                            "TN03EF9012",
                            ViolationType.ILLEGAL_PARKING,
                            "T Nagar",
                            LocalDateTime.now(),
                            0,
                            50);

            System.out.println(challan3);

            // Repeated violation
            Challan repeated =
                    challanService.generateChallan(
                            "TN01AB1234",
                            ViolationType.OVER_SPEEDING,
                            "OMR Road",
                            LocalDateTime.now(),
                            95,
                            60);

            System.out.println(repeated);

            // Display vehicle history
            Vehicle vehicle =
                    vehicleService.getVehicle("TN01AB1234");

            System.out.println("\nVehicle Classification:");
            System.out.println(vehicle);

            // Outstanding fine
            System.out.println(
                    "\nTotal Outstanding Fine: ₹"
                            + challanService.calculateOutstandingFine());

            // Payment
            challanService.payChallan(
                    challan1.getChallanId());

            System.out.println(
                    "Outstanding Fine After Payment: ₹"
                            + challanService.calculateOutstandingFine());

        } catch (InvalidVehicleException
                 | DuplicateChallanException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}
