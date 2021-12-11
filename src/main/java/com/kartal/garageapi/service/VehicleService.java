package com.kartal.garageapi.service;

import com.kartal.garageapi.dto.TicketDto;
import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.exception.GarageFullException;
import com.kartal.garageapi.model.Vehicle;

public interface VehicleService {

    Object parkVehicle(Vehicle vehicle, VehicleParkingDto vehicleParkingDto) throws GarageFullException;
}
