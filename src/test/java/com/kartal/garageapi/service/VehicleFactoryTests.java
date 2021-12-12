package com.kartal.garageapi.service;

import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.exception.VehicleNotFoundException;
import com.kartal.garageapi.model.Vehicle;
import com.kartal.garageapi.model.VehicleFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class VehicleFactoryTests {

    @Test
    void vehicleFactoryTest() throws VehicleNotFoundException {
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

        Vehicle truckVehicle = VehicleFactory.buildVehicle(truck.getType());
        Vehicle jeepVehicle = VehicleFactory.buildVehicle(jeep.getType());
        Vehicle carVehicle = VehicleFactory.buildVehicle(car.getType());

        assertThat(carVehicle.getVehicleWidht()).isEqualTo((byte) 1);
        assertThat(truckVehicle.getVehicleWidht()).isEqualTo((byte) 4);
        assertThat(jeepVehicle.getVehicleWidht()).isEqualTo((byte) 2);
    }

    @Test
    void vehicleUnSupportedTypeTest() {
        VehicleParkingDto vehicleParkingDto = VehicleParkingDto
                .builder()
                .plate("16-JGR-42")
                .color("black")
                .type("suv")
                .build();

        try {
            Vehicle vehicle = VehicleFactory.buildVehicle(vehicleParkingDto.getType());
        } catch (Exception e) {
            assertTrue(e instanceof VehicleNotFoundException);
        }
    }

}
