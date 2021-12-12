package com.kartal.garageapi.service.impl;

import com.kartal.garageapi.dto.VehicleLeavingDto;
import com.kartal.garageapi.dto.VehicleParkingDto;
import com.kartal.garageapi.dto.VehicleStatusDto;
import com.kartal.garageapi.exception.GarageFullException;
import com.kartal.garageapi.model.Ticket;
import com.kartal.garageapi.model.Vehicle;
import com.kartal.garageapi.repository.TicketRepository;
import com.kartal.garageapi.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final TicketRepository ticketRepository;
    @Value("${app.garage.number_of_slots}")
    private byte maxSlot;
    private final byte[] availableSlots = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private final HashMap<String, Byte> vehiclePlateSlotNumberMap = new HashMap<>();

    public VehicleServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /*
     ** When program runs, available status of slots will init from in-memory db
     */
    @PostConstruct
    public void init() {
        /*
         ** if in availableSlots an index value is -1, this index is allocated
         */
        List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAll();
        ticketList.forEach(ticket -> {
            if (ticket.getStatus().equals(Ticket.Status.PARKED)) {
                for (byte i = 0; i < ticket.getVehicleWidht(); i++) {
                    availableSlots[ticket.getAllocatedSlot() + i] = -1;
                }
            }
        });
        writeToLogAvailableSlots();
    }

    @Override
    public Ticket parkVehicle(Vehicle vehicle, VehicleParkingDto vehicleParkingDto) throws GarageFullException {
        // thread-safe
        synchronized (availableSlots) {
            if (isGarageFull(vehicle.getVehicleWidht())) {
                log.info("Garage is full.");
                throw new GarageFullException();
            }
            allocateSlotToVehicle(vehicle.getVehicleWidht(), vehicleParkingDto.getPlate(), getAvailableSlotIndex());
        }

        if (vehiclePlateSlotNumberMap.get(vehicleParkingDto.getPlate()) == null) {
            throw new GarageFullException();
        }

        Ticket ticket = Ticket.builder()
                .ticketNumber(UUID.randomUUID().toString())
                .status(Ticket.Status.PARKED)
                .parketAt(new Date())
                .color(vehicleParkingDto.getColor())
                .plate(vehicleParkingDto.getPlate())
                .allocatedSlot(vehiclePlateSlotNumberMap.remove(vehicleParkingDto.getPlate()))
                .vehicleWidht(vehicle.getVehicleWidht())
                .build();
        ticketRepository.save(ticket);
        return ticket;
    }

    @Override
    public boolean isParkedVehicle(VehicleParkingDto vehicleParkingDto) {
        Optional<Ticket> ticket = ticketRepository.findByPlate(vehicleParkingDto.getPlate());
        return ticket.map(value -> value.getStatus().equals(Ticket.Status.PARKED)).orElse(false);
    }

    @Override
    public List<VehicleStatusDto> getGarageStatus() {
        List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAll();
        List<VehicleStatusDto> vehicleStatusDtos = new ArrayList<>(Collections.emptyList());
        ticketList.forEach(ticket -> {
            if (ticket.getStatus().equals(Ticket.Status.PARKED)) {
                vehicleStatusDtos.add(VehicleStatusDto
                        .builder()
                        .color(ticket.getColor())
                        .plate(ticket.getPlate())
                        .type(getVehicleType(ticket.getVehicleWidht()))
                        .allocatedSlots(getAllocatedSlots(ticket))
                        .build());
            }
        });
        return vehicleStatusDtos;
    }

    @Override
    public Optional<Ticket> getTicketByTicketNumber(VehicleLeavingDto vehicleLeavingDto) {
        return ticketRepository.findByTicketNumber(vehicleLeavingDto.getTicketNumber());
    }

    @Override
    public byte leaveGarage(Ticket ticket) {
        synchronized (availableSlots) {
            leaveVehicleSync(ticket);
        }
        ticket.setLeavedAt(new Date());
        ticket.setStatus(Ticket.Status.LEAVED);
        ticketRepository.save(ticket);
        log.info("Leaved: {} slot {}", ticket.getPlate(), ticket.getAllocatedSlot());
        writeToLogAvailableSlots();
        return ticket.getAllocatedSlot();
    }

    private void leaveVehicleSync(Ticket ticket) {
        for (byte i = 0; i < ticket.getVehicleWidht(); i++) {
            availableSlots[ticket.getAllocatedSlot() + i] = (byte) (ticket.getAllocatedSlot() + i);
        }
    }

    private String getVehicleType(byte numberOfSlots) {
        if (numberOfSlots == 1) {
            return "Car";
        } else if (numberOfSlots == 2) {
            return "Jeep";
        }
        return "Truck";
    }

    private int[] getAllocatedSlots(Ticket ticket) {
        int[] slots = new int[ticket.getVehicleWidht()];
        for (byte i = 0; i < ticket.getVehicleWidht(); i++) {
            slots[i] = ticket.getAllocatedSlot() + i;
        }
        return slots;
    }

    /*
     ** check is garage full for vehicle, it also depends on vehicle widht
     */
    private boolean isGarageFull(byte slotNumber) {
        return maxSlot == 0 || getAvailableSlotSize() == 0 || getAvailableSlotSize() < slotNumber;
    }

    /*
     ** return how many slots is empty
     */
    private byte getAvailableSlotSize() {
        byte sum = 0;
        for (byte i = 1; i < maxSlot + 1; i++) {
            if (availableSlots[i] != -1) {
                sum = (byte) (sum + 1);
            }
        }
        return sum;
    }

    /*
     ** The nearest available index
     */
    private byte getAvailableSlotIndex() {
        for (byte i = 1; i < maxSlot + 1; i++) {
            if (availableSlots[i] != -1) {
                return i;
            }
        }
        return -1;
    }

    private void allocateSlotToVehicle(byte slotNumber, String plate, byte availableSlotIndex) throws GarageFullException {
        if (slotNumber == 1) {
            byte tempValue = availableSlots[availableSlotIndex];
            availableSlots[availableSlotIndex] = -1;
            vehiclePlateSlotNumberMap.put(plate, tempValue);
            log.info("Allocated 1 slot");

        } else {
            byte index = getAvailableSlotIndexForMoreThanOneSlotLenght(slotNumber, availableSlotIndex);
            vehiclePlateSlotNumberMap.put(plate, index);
            for (byte i = 0; i < slotNumber; i++) {
                availableSlots[index] = -1;
                index = (byte) (index + 1);
            }
            log.info("Allocated {} slots", slotNumber);
            writeToLogAvailableSlots();
        }

    }

    private byte getAvailableSlotIndexForMoreThanOneSlotLenght(byte slotNumber, byte availableSlotIndex) throws GarageFullException {
        byte sum = 1;
        byte tempIndex = availableSlotIndex;
        try {
            for (byte i = availableSlotIndex; i < maxSlot + 1; i++) {
                if (availableSlots[i] + 1 == availableSlots[i + 1]) {
                    sum = (byte) (sum + 1);
                    if (sum == slotNumber) {
                        return tempIndex;
                    }
                } else {
                    sum = 1;
                    tempIndex = (byte) (i + 1);
                }
            }
        }catch (ArrayIndexOutOfBoundsException ex){
            log.info("Garage is full.");
            throw new GarageFullException();
        }
        return -1;
    }

    private void writeToLogAvailableSlots() {
        List<Byte> availableSlotList = new ArrayList<>();
        for (int i = 1; i < availableSlots.length; i++) {
            if (availableSlots[i] != -1) {
                availableSlotList.add(availableSlots[i]);
            }
        }
        log.info("Available slots : {}", availableSlotList);
    }
}















