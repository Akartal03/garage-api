package com.kartal.garageapi.service;

import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.exception.GarageFullException;
import com.kartal.garageapi.model.Ticket;
import com.kartal.garageapi.model.Vehicle;

public interface VehicleService {

    Ticket parkVehicle(Vehicle vehicle, VehicleParkingDto vehicleParkingDto) throws GarageFullException;

    boolean isParkedVehicle(VehicleParkingDto vehicleParkingDto);
}
