package com.kartal.garageapi.service;

import com.kartal.garageapi.dto.VehicleLeavingDto;
import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.dto.VehicleStatusDto;
import com.kartal.garageapi.exception.GarageFullException;
import com.kartal.garageapi.model.Ticket;
import com.kartal.garageapi.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    Ticket parkVehicle(Vehicle vehicle, VehicleParkingDto vehicleParkingDto) throws GarageFullException;

    boolean isParkedVehicle(VehicleParkingDto vehicleParkingDto);

    List<VehicleStatusDto> getGarageStatus();

    Optional<Ticket> getTicketByTicketNumber(VehicleLeavingDto vehicleLeavingDto);

    byte leaveGarage(Ticket ticket);
}
