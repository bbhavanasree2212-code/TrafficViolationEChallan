package com.traffic.service;

import com.traffic.exception.DuplicateChallanException;
import com.traffic.exception.InvalidVehicleException;
import com.traffic.model.Challan;
import com.traffic.model.PaymentStatus;
import com.traffic.model.Vehicle;
import com.traffic.model.ViolationType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChallanService {

    private List<Challan> challans = new ArrayList<>();

    private VehicleService vehicleService;

    private int challanCounter = 1001;

    public ChallanService(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public Challan generateChallan(
            String vehicleNumber,
            ViolationType violationType,
            String location,
            LocalDateTime timestamp,
            double speed,
            double permittedSpeed)
            throws InvalidVehicleException, DuplicateChallanException {

        Vehicle vehicle = vehicleService.getVehicle(vehicleNumber);

        validateViolation(
                violationType,
                location,
                speed,
                permittedSpeed);

        if (isDuplicate(
                vehicleNumber,
                violationType,
                location,
                timestamp)) {

            throw new DuplicateChallanException(
                    "Duplicate challan for the same violation event.");
        }

        double fine = calculateFine(
                violationType,
                speed,
                permittedSpeed,
                vehicle.getViolationCount());

        String challanId = "CH" + challanCounter++;

        Challan challan = new Challan(
                challanId,
                vehicleNumber,
                violationType,
                location,
                timestamp,
                speed,
                permittedSpeed,
                fine);

        challans.add(challan);

        vehicle.addViolation(challan);

        System.out.println(
                "Electronic Challan Generated: " + challanId);

        System.out.println(
                "Fine Amount: ₹" + fine);

        return challan;
    }

    private void validateViolation(
            ViolationType violationType,
            String location,
            double speed,
            double permittedSpeed)
            throws InvalidVehicleException {

        if (violationType == null) {
            throw new InvalidVehicleException(
                    "Violation type is required.");
        }

        if (location == null || location.trim().isEmpty()) {
            throw new InvalidVehicleException(
                    "Violation location cannot be empty.");
        }

        if (speed < 0) {
            throw new InvalidVehicleException(
                    "Speed cannot be negative.");
        }

        if (permittedSpeed <= 0) {
            throw new InvalidVehicleException(
                    "Permitted speed must be greater than zero.");
        }
    }

    public double calculateFine(
            ViolationType violationType,
            double speed,
            double permittedSpeed,
            int previousViolations) {

        double baseFine;

        switch (violationType) {

            case OVER_SPEEDING:

                double excessSpeed = speed - permittedSpeed;

                if (excessSpeed <= 0) {
                    baseFine = 0;
                } else if (excessSpeed <= 10) {
                    baseFine = 500;
                } else if (excessSpeed <= 20) {
                    baseFine = 1000;
                } else {
                    baseFine = 2000;
                }

                break;

            case SIGNAL_VIOLATION:
                baseFine = 1500;
                break;

            case ILLEGAL_PARKING:
                baseFine = 500;
                break;

            default:
                baseFine = 0;
        }

        if (previousViolations > 0) {
            baseFine = baseFine * 1.5;
        }

        return baseFine;
    }

    private boolean isDuplicate(
            String vehicleNumber,
            ViolationType violationType,
            String location,
            LocalDateTime timestamp) {

        for (Challan challan : challans) {

            if (challan.getVehicleNumber().equals(vehicleNumber)
                    && challan.getViolationType() == violationType
                    && challan.getLocation().equals(location)
                    && challan.getTimestamp().equals(timestamp)) {

                return true;
            }
        }

        return false;
    }

    public void payChallan(String challanId) {

        for (Challan challan : challans) {

            if (challan.getChallanId().equals(challanId)) {

                if (challan.getPaymentStatus() == PaymentStatus.PAID) {
                    System.out.println("Challan is already paid.");
                    return;
                }

                challan.markAsPaid();

                System.out.println(
                        "Payment successful for " + challanId);

                return;
            }
        }

        System.out.println("Challan not found: " + challanId);
    }

    public double calculateOutstandingFine() {

        double total = 0;

        for (Challan challan : challans) {

            if (challan.getPaymentStatus() == PaymentStatus.UNPAID) {
                total += challan.getFineAmount();
            }
        }

        return total;
    }

    public List<Challan> getAllChallans() {
        return challans;
    }

    public List<Challan> getUnpaidChallans() {

        List<Challan> unpaid = new ArrayList<>();

        for (Challan challan : challans) {

            if (challan.getPaymentStatus() == PaymentStatus.UNPAID) {
                unpaid.add(challan);
            }
        }

        return unpaid;
    }

    public List<Challan> getPaidChallans() {

        List<Challan> paid = new ArrayList<>();

        for (Challan challan : challans) {

            if (challan.getPaymentStatus() == PaymentStatus.PAID) {
                paid.add(challan);
            }
        }

        return paid;
    }
}
