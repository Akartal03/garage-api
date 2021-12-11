package com.kartal.garageapi.model;

import com.kartal.garageapi.exception.VehicleNotFoundException;

public class VehicleFactory {

    private VehicleFactory() {
        throw new IllegalStateException("Factory class");
    }

    public static Vehicle buildVehicle(String type) throws VehicleNotFoundException {
        Vehicle vehicle;
        switch (type) {
            case "car":
                vehicle = new Car();
                break;
            case "truck":
                vehicle = new Truck();
                break;
            case "jeep":
                vehicle = new Jeep();
                break;
            default:
                throw new VehicleNotFoundException();
        }
        return vehicle;
    }
}
