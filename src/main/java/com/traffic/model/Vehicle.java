package com.traffic.model;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private String vehicleNumber;
    private String ownerName;
    private String ownerContact;
    private VehicleType vehicleType;

    private List<Challan> violationHistory;

    public Vehicle(
            String vehicleNumber,
            String ownerName,
            String ownerContact,
            VehicleType vehicleType) {

        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.vehicleType = vehicleType;
        this.violationHistory = new ArrayList<>();
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public List<Challan> getViolationHistory() {
        return violationHistory;
    }

    public void addViolation(Challan challan) {
        violationHistory.add(challan);
    }

    public int getViolationCount() {
        return violationHistory.size();
    }

    public String getClassification() {

        int count = violationHistory.size();

        if (count == 0) {
            return "GOOD";
        } else if (count <= 2) {
            return "LOW RISK";
        } else if (count <= 4) {
            return "MEDIUM RISK";
        } else {
            return "HIGH RISK";
        }
    }

    @Override
    public String toString() {

        return "Vehicle Number: " + vehicleNumber +
                " | Owner: " + ownerName +
                " | Contact: " + ownerContact +
                " | Type: " + vehicleType +
                " | Violations: " + violationHistory.size() +
                " | Classification: " + getClassification();
    }
}
