package com.kartal.garageapi.service;

import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.exception.GarageFullException;
import com.kartal.garageapi.exception.VehicleNotFoundException;
import com.kartal.garageapi.model.Ticket;
import com.kartal.garageapi.model.Vehicle;
import com.kartal.garageapi.model.VehicleFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
class ParkAndLeaveTests {

    VehicleParkingDto car = VehicleParkingDto
            .builder()
            .plate("16-JGR-40")
            .color("black")
            .type("car")
            .build();

    VehicleParkingDto truck = VehicleParkingDto
            .builder()
            .plate("16-JGR-41")
            .color("black")
            .type("truck")
            .build();

    VehicleParkingDto jeep = VehicleParkingDto
            .builder()
            .plate("16-JGR-42")
            .color("black")
            .type("jeep")
            .build();

    Vehicle carVehicle = VehicleFactory.buildVehicle(car.getType());
    Vehicle jeepVehicle = VehicleFactory.buildVehicle(jeep.getType());
    Vehicle truckVehicle = VehicleFactory.buildVehicle(truck.getType());

    @Autowired
    private VehicleService vehicleService;

    ParkAndLeaveTests() throws VehicleNotFoundException {
    }

    @Test
    void vehicleParkingTest() throws GarageFullException {
        Ticket carTicket = vehicleService.parkVehicle(carVehicle, car);
        Ticket jeepTicket = vehicleService.parkVehicle(jeepVehicle, jeep);
        Ticket truckTicket = vehicleService.parkVehicle(truckVehicle, truck);

        assertThat(carTicket.getAllocatedSlot()).isEqualTo((byte) 1);
        assertThat(carTicket.getVehicleWidht()).isEqualTo((byte) 1);

        assertThat(jeepTicket.getAllocatedSlot()).isEqualTo((byte) 2);
        assertThat(jeepTicket.getVehicleWidht()).isEqualTo((byte) 2);

        assertThat(truckTicket.getAllocatedSlot()).isEqualTo((byte) 4);
        assertThat(truckTicket.getVehicleWidht()).isEqualTo((byte) 4);

        // used to clear cache
        vehicleService.leaveGarage(carTicket);
        vehicleService.leaveGarage(jeepTicket);
        vehicleService.leaveGarage(truckTicket);
    }

    @Test
    void vehicleLeavingParkingTest1() throws GarageFullException {
        Ticket carTicket = vehicleService.parkVehicle(carVehicle, car);
        Ticket jeepTicket = vehicleService.parkVehicle(jeepVehicle, jeep);
        Ticket truckTicket = vehicleService.parkVehicle(truckVehicle, truck);

        vehicleService.leaveGarage(carTicket);
        vehicleService.leaveGarage(jeepTicket);
        truck.setPlate("12-XYZ-12");

        try {
            Ticket truckTicket2 = vehicleService.parkVehicle(truckVehicle, truck);
        } catch (Exception e) {
            assertTrue(e instanceof GarageFullException);
        }

        // used to clear cache
        vehicleService.leaveGarage(truckTicket);
    }

    @Test
    void vehicleLeavingParkingTest2() throws GarageFullException {
        Ticket carTicket = vehicleService.parkVehicle(carVehicle, car);
        Ticket jeepTicket = vehicleService.parkVehicle(jeepVehicle, jeep);
        Ticket truckTicket = vehicleService.parkVehicle(truckVehicle, truck);

        vehicleService.leaveGarage(truckTicket);
        car.setPlate("12-XYZ-123");
        Ticket carTicket2 = vehicleService.parkVehicle(carVehicle, car);
        assertThat(carTicket2.getAllocatedSlot()).isEqualTo((byte) 4);

        // used to clear cache
        vehicleService.leaveGarage(carTicket);
        vehicleService.leaveGarage(jeepTicket);
        vehicleService.leaveGarage(carTicket2);
    }

}
