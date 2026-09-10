package com.traffic.service;

import com.traffic.exception.InvalidVehicleException;
import com.traffic.model.Vehicle;
import com.traffic.model.VehicleType;

import java.util.HashMap;
import java.util.Map;

public class VehicleService {

    private Map<String, Vehicle> vehicles = new HashMap<>();

    public void registerVehicle(
            String vehicleNumber,
            String ownerName,
            String ownerContact,
            VehicleType vehicleType)
            throws InvalidVehicleException {

        validateVehicle(vehicleNumber, ownerName, ownerContact, vehicleType);

        if (vehicles.containsKey(vehicleNumber)) {
            throw new InvalidVehicleException(
                    "Vehicle already registered: " + vehicleNumber);
        }

        Vehicle vehicle = new Vehicle(
                vehicleNumber,
                ownerName,
                ownerContact,
                vehicleType);

        vehicles.put(vehicleNumber, vehicle);

        System.out.println(
                "Vehicle registered successfully: " + vehicleNumber);
    }

    private void validateVehicle(
            String vehicleNumber,
            String ownerName,
            String ownerContact,
            VehicleType vehicleType)
            throws InvalidVehicleException {

        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new InvalidVehicleException(
                    "Invalid vehicle number.");
        }

        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new InvalidVehicleException(
                    "Owner name cannot be empty.");
        }

        if (ownerContact == null || ownerContact.trim().isEmpty()) {
            throw new InvalidVehicleException(
                    "Owner contact cannot be empty.");
        }

        if (vehicleType == null) {
            throw new InvalidVehicleException(
                    "Vehicle type is required.");
        }
    }

    public Vehicle getVehicle(String vehicleNumber)
            throws InvalidVehicleException {

        Vehicle vehicle = vehicles.get(vehicleNumber);

        if (vehicle == null) {
            throw new InvalidVehicleException(
                    "Vehicle not found: " + vehicleNumber);
        }

        return vehicle;
    }

    public boolean vehicleExists(String vehicleNumber) {
        return vehicles.containsKey(vehicleNumber);
    }
}
