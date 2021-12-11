package com.kartal.garageapi.controller;

import com.kartal.garageapi.dto.VehicleLeavingDto;
import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.dto.VehicleStatusDto;
import com.kartal.garageapi.exception.GarageFullException;
import com.kartal.garageapi.exception.VehicleNotFoundException;
import com.kartal.garageapi.model.Ticket;
import com.kartal.garageapi.model.Vehicle;
import com.kartal.garageapi.model.VehicleFactory;
import com.kartal.garageapi.service.VehicleService;
import com.kartal.garageapi.util.PlateFunctions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@Slf4j
@RequestMapping("/api/v1/garage/vehicle")
@Api(value = "Garage Api documentation")
@AllArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/park")
    @ApiOperation(value = "Vehicle park method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully vehicle parked!"),
            @ApiResponse(code = 400, message = "Invalid data about vehicle"),
            @ApiResponse(code = 406, message = "Garage is full"),
            @ApiResponse(code = 500, message = "Occurred a problem, see details for info")
    })
    public ResponseEntity<?> parkVehicle(@RequestBody VehicleParkingDto vehicleParkingDto) {
        try {
            if (!PlateFunctions.isValidPlate(vehicleParkingDto.getPlate())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid plate number");
            }

            if (vehicleService.isParkedVehicle(vehicleParkingDto)) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Vehicle has already parked");
            }

            Vehicle vehicle = VehicleFactory.buildVehicle(vehicleParkingDto.getType());
            Ticket ticketDto = vehicleService.parkVehicle(vehicle, vehicleParkingDto);
            return ResponseEntity.ok(ticketDto);

        } catch (VehicleNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehicle not found error :" + vehicleParkingDto.getType());
        } catch (GarageFullException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Garage is full, sorry.");
        }
    }

    @GetMapping("/status")
    @ApiOperation(value = "Garage status method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully status sended!"),
            @ApiResponse(code = 404, message = "There is no car parking"),
            @ApiResponse(code = 500, message = "Occurred a problem, see details for info")
    })
    public ResponseEntity<?> getGarageStatus() {
        try {
            List<VehicleStatusDto> vehicleStatusDtos = vehicleService.getGarageStatus();
            if (vehicleStatusDtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No parking car");
            }
            return ResponseEntity.ok(vehicleStatusDtos);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage() != null ? ex.getMessage() : "");
        }
    }

    @PostMapping("/leave")
    @ApiOperation(value = "Leave garage method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully vehicle leaved!"),
            @ApiResponse(code = 404, message = "There is no car parking"),
            @ApiResponse(code = 500, message = "Occurred a problem, see details for info")
    })
    public ResponseEntity<?> leave(@RequestBody VehicleLeavingDto vehicleLeavingDto) {
        try {
            Optional<Ticket> ticket = vehicleService.getTicketByTicketNumber(vehicleLeavingDto);
            if (ticket.isEmpty() || ticket.get().getStatus().equals(Ticket.Status.LEAVED)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No parking car");
            }

            byte leavedSlot = vehicleService.leaveVehicle(ticket.get());
            return ResponseEntity.ok("Slot is free :" + leavedSlot);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage() != null ? ex.getMessage() : "");
        }
    }
}

