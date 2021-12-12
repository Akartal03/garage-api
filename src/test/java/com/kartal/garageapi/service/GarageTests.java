package com.kartal.garageapi.service;

import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.exception.GarageFullException;
import com.kartal.garageapi.exception.VehicleNotFoundException;
import com.kartal.garageapi.model.Ticket;
import com.kartal.garageapi.model.Vehicle;
import com.kartal.garageapi.model.VehicleFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GarageTests {

    VehicleParkingDto car = VehicleParkingDto
            .builder()
            .plate("16-JGR-42")
            .color("black")
            .type("car")
            .build();

    VehicleParkingDto truck = VehicleParkingDto
            .builder()
            .plate("16-JGR-42")
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

    GarageTests() throws VehicleNotFoundException {
    }

    @Test
    void vehicleParkingTest() throws GarageFullException {
        Ticket carTicket = vehicleService.parkVehicle(carVehicle, car);
        Ticket jeepTicket =vehicleService.parkVehicle(jeepVehicle, jeep);
        Ticket truckTicket =vehicleService.parkVehicle(truckVehicle, truck);

        assertThat(carTicket.getSlot()).isEqualTo((byte)1);
        assertThat(carTicket.getNumberOfSlots()).isEqualTo((byte)1);

        assertThat(jeepTicket.getSlot()).isEqualTo((byte)2);
        assertThat(jeepTicket.getNumberOfSlots()).isEqualTo((byte)2);

        assertThat(truckTicket.getSlot()).isEqualTo((byte)4);
        assertThat(truckTicket.getNumberOfSlots()).isEqualTo((byte)4);

        vehicleService.leaveVehicle(carTicket);
        vehicleService.leaveVehicle(jeepTicket);
        vehicleService.leaveVehicle(truckTicket);
    }

}
