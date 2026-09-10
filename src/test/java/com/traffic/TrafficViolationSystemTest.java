package com.traffic;

import com.traffic.exception.DuplicateChallanException;
import com.traffic.exception.InvalidVehicleException;
import com.traffic.model.Challan;
import com.traffic.model.Vehicle;
import com.traffic.model.VehicleType;
import com.traffic.model.ViolationType;
import com.traffic.service.ChallanService;
import com.traffic.service.VehicleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TrafficViolationSystemTest {

    private VehicleService vehicleService;
    private ChallanService challanService;

    @BeforeEach
    void setup() {

        vehicleService = new VehicleService();
        challanService = new ChallanService(vehicleService);
    }

    @Test
    void testVehicleRegistration()
            throws InvalidVehicleException {

        vehicleService.registerVehicle(
                "TN01AB1234",
                "Ravi Kumar",
                "9876543210",
                VehicleType.CAR);

        assertTrue(
                vehicleService.vehicleExists("TN01AB1234"));
    }

    @Test
    void testInvalidVehicleNumber() {

        assertThrows(
                InvalidVehicleException.class,
                () -> vehicleService.registerVehicle(
                        "",
                        "Ravi",
                        "9876543210",
                        VehicleType.CAR));
    }

    @Test
    void testOverSpeedingFine()
            throws InvalidVehicleException,
            DuplicateChallanException {

        vehicleService.registerVehicle(
                "TN01AB1234",
                "Ravi",
                "9876543210",
                VehicleType.CAR);

        Challan challan =
                challanService.generateChallan(
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        "Chennai",
                        LocalDateTime.now(),
                        80,
                        60);

        assertEquals(1000, challan.getFineAmount());
    }

    @Test
    void testBoundarySpeed()
            throws InvalidVehicleException,
            DuplicateChallanException {

        vehicleService.registerVehicle(
                "TN01AB1234",
                "Ravi",
                "9876543210",
                VehicleType.CAR);

        Challan challan =
                challanService.generateChallan(
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        "Chennai",
                        LocalDateTime.now(),
                        60,
                        60);

        assertEquals(0, challan.getFineAmount());
    }

    @Test
    void testDuplicateChallan()
            throws InvalidVehicleException,
            DuplicateChallanException {

        vehicleService.registerVehicle(
                "TN01AB1234",
                "Ravi",
                "9876543210",
                VehicleType.CAR);

        LocalDateTime timestamp =
                LocalDateTime.of(
                        2026, 9, 10, 10, 30);

        challanService.generateChallan(
                "TN01AB1234",
                ViolationType.OVER_SPEEDING,
                "Chennai",
                timestamp,
                80,
                60);

        assertThrows(
                DuplicateChallanException.class,
                () -> challanService.generateChallan(
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        "Chennai",
                        timestamp,
                        80,
                        60));
    }

    @Test
    void testRepeatedViolationHasHigherPenalty()
            throws InvalidVehicleException,
            DuplicateChallanException {

        vehicleService.registerVehicle(
                "TN01AB1234",
                "Ravi",
                "9876543210",
                VehicleType.CAR);

        Challan first =
                challanService.generateChallan(
                        "TN01AB1234",
                        ViolationType.SIGNAL_VIOLATION,
                        "Chennai",
                        LocalDateTime.of(
                                2026, 9, 10, 10, 0),
                        0,
                        60);

        Challan second =
                challanService.generateChallan(
                        "TN01AB1234",
                        ViolationType.SIGNAL_VIOLATION,
                        "Chennai",
                        LocalDateTime.of(
                                2026, 9, 10, 11, 0),
                        0,
                        60);

        assertTrue(
                second.getFineAmount()
                        > first.getFineAmount());
    }

    @Test
    void testChallanPayment()
            throws InvalidVehicleException,
            DuplicateChallanException {

        vehicleService.registerVehicle(
                "TN01AB1234",
                "Ravi",
                "9876543210",
                VehicleType.CAR);

        Challan challan =
                challanService.generateChallan(
                        "TN01AB1234",
                        ViolationType.ILLEGAL_PARKING,
                        "Chennai",
                        LocalDateTime.now(),
                        0,
                        50);

        assertEquals(
                500,
                challan.getFineAmount());

        challanService.payChallan(
                challan.getChallanId());

        assertEquals(
                com.traffic.model.PaymentStatus.PAID,
                challan.getPaymentStatus());
    }

    @Test
    void testOutstandingFine()
            throws InvalidVehicleException,
            DuplicateChallanException {

        vehicleService.registerVehicle(
                "TN01AB1234",
                "Ravi",
                "9876543210",
                VehicleType.CAR);

        vehicleService.registerVehicle(
                "TN02CD5678",
                "Priya",
                "9876501234",
                VehicleType.BIKE);

        challanService.generateChallan(
                "TN01AB1234",
                ViolationType.ILLEGAL_PARKING,
                "Chennai",
                LocalDateTime.now(),
                0,
                50);

        challanService.generateChallan(
                "TN02CD5678",
                ViolationType.SIGNAL_VIOLATION,
                "Chennai",
                LocalDateTime.now(),
                0,
                50);

        assertEquals(
                2000,
                challanService.calculateOutstandingFine());
    }

    @Test
    void testVehicleClassification()
            throws InvalidVehicleException,
            DuplicateChallanException {

        vehicleService.registerVehicle(
                "TN01AB1234",
                "Ravi",
                "9876543210",
                VehicleType.CAR);

        Vehicle vehicle =
                vehicleService.getVehicle("TN01AB1234");

        assertEquals(
                "GOOD",
                vehicle.getClassification());

        challanService.generateChallan(
                "TN01AB1234",
                ViolationType.ILLEGAL_PARKING,
                "Chennai",
                LocalDateTime.now(),
                0,
                50);

        assertEquals(
                "LOW RISK",
                vehicle.getClassification());
    }
}
