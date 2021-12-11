package com.kartal.garageapi.service.impl;

import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.exception.GarageFullException;
import com.kartal.garageapi.model.Ticket;
import com.kartal.garageapi.model.Vehicle;
import com.kartal.garageapi.repository.TicketRepository;
import com.kartal.garageapi.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final TicketRepository ticketRepository;

    @Value("${app.garage.number_of_slots}")
    private byte MAX_SLOT;
    private ArrayList<Byte> availableSlotList;
    private final HashMap<String, Byte> vehiclePlateSlotNumberMap = new HashMap<>();

    public VehicleServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @PostConstruct
    public void init() {
        this.availableSlotList = new ArrayList<>();
        for (byte i = 1; i <= this.MAX_SLOT; i++) {
            availableSlotList.add(i);
        }
    }

    @Override
    public Object parkVehicle(Vehicle vehicle, VehicleParkingDto vehicleParkingDto) throws GarageFullException {
        if (isGarageFull()) {
            throw new GarageFullException();
        }

        assignAvailableSlotNumber(vehicle.getSlotNumber(), vehicleParkingDto.getPlate());
        if (this.vehiclePlateSlotNumberMap.get(vehicleParkingDto.getPlate()) == null) {
            log.info("Garage is full.");
            throw new GarageFullException();
        }

        Ticket ticket = Ticket.builder()
                .ticketNumber(UUID.randomUUID().toString())
                .status(Ticket.Status.PARKED)
                .parketAt(new Date())
                .color(vehicleParkingDto.getColor())
                .plate(vehicleParkingDto.getPlate())
                .slot(this.vehiclePlateSlotNumberMap.get(vehicleParkingDto.getPlate()))
                .build();
        ticketRepository.save(ticket);
        return ticket;
    }

    private boolean isGarageFull() {
        if (this.MAX_SLOT == 0 || this.availableSlotList.isEmpty()) {
            log.info("Garage is full.");
            return true;
        }
        return false;
    }

    private synchronized void assignAvailableSlotNumber(byte slotNumber, String plate) {
        if (this.availableSlotList.size() < slotNumber) {
            log.info("Garage is full.");
        } else if (slotNumber == 1) {
            this.vehiclePlateSlotNumberMap.put(plate, this.availableSlotList.remove(0));
            log.info("Allocated 1 slot");
        } else if (this.availableSlotList.size() == slotNumber) {
            for (byte i = 0; i < slotNumber - 1; i++) {
                if (this.availableSlotList.get(i) + 1 != this.availableSlotList.get(i + 1)) {
                    return;
                }
            }
            this.vehiclePlateSlotNumberMap.put(plate, this.availableSlotList.get(0));
            for (byte i = 0; i < slotNumber; i++) {
                this.availableSlotList.remove(0);
            }
            log.info("Allocated {} slots", slotNumber);

        } else {
            byte sum = 1;
            byte tempSlot = 0;
            for (byte i = 0; i < this.availableSlotList.size() - 1; i++) {
                if (this.availableSlotList.get(i) + 1 == this.availableSlotList.get(i + 1)) {
                    sum++;
                } else {
                    sum = 1;
                    tempSlot = (byte) (i + 1);
                }
                if (sum == slotNumber) {
                    this.vehiclePlateSlotNumberMap.put(plate, this.availableSlotList.get(tempSlot));
                    for (i = 0; i < slotNumber; i++) {
                        this.availableSlotList.remove(tempSlot);
                    }
                    log.info("Allocated {} slots", slotNumber);
                    break;
                }
            }
        }
    }
}





















