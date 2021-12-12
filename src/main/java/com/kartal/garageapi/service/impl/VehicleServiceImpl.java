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
    private byte MAX_SLOT;
    private byte[] availableSlots;
    private final HashMap<String, Byte> vehiclePlateSlotNumberMap = new HashMap<>();

    public VehicleServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /*
     ** When program runs, available status of slots will init from in-memory db
     */
    @PostConstruct
    public void init() {
        this.availableSlots = new byte[MAX_SLOT + 1];
        for (byte i = 1; i <= this.MAX_SLOT; i++) {
            this.availableSlots[i] = i;
        }

        /*
         ** if in availableSlots an index value is -1, this index is allocated
         */
        List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAll();
        ticketList.forEach(ticket -> {
            if (ticket.getStatus().equals(Ticket.Status.PARKED)) {
                for (byte i = 0; i < ticket.getNumberOfSlots(); i++) {
                    this.availableSlots[ticket.getSlot() + i] = -1;
                }
            }
        });
        writeToLogAvailableSlots();
    }

    private void writeToLogAvailableSlots() {
        List<Byte> availableSlotList = new ArrayList<>();
        for (int i = 1; i < this.availableSlots.length; i++) {
            if(this.availableSlots[i] != -1){
                availableSlotList.add(this.availableSlots[i]);
            }
        }
        log.info("Available slots : {}", availableSlotList);
    }

    @Override
    public Ticket parkVehicle(Vehicle vehicle, VehicleParkingDto vehicleParkingDto) throws GarageFullException {
        if (isGarageFull(vehicle.getSlotNumber())) {
            log.info("Garage is full.");
            throw new GarageFullException();
        }

        assignAvailableSlotNumber(vehicle.getSlotNumber(), vehicleParkingDto.getPlate());
        if (this.vehiclePlateSlotNumberMap.get(vehicleParkingDto.getPlate()) == null) {
            throw new GarageFullException();
        }

        Ticket ticket = Ticket.builder()
                .ticketNumber(UUID.randomUUID().toString())
                .status(Ticket.Status.PARKED)
                .parketAt(new Date())
                .color(vehicleParkingDto.getColor())
                .plate(vehicleParkingDto.getPlate())
                .slot(this.vehiclePlateSlotNumberMap.get(vehicleParkingDto.getPlate()))
                .numberOfSlots(vehicle.getSlotNumber())
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
                        .type(getVehicleType(ticket.getNumberOfSlots()))
                        .slots(getSlots(ticket))
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
    public byte leaveVehicle(Ticket ticket) {
        leaveVehicleSync(ticket);
        ticket.setLeavedAt(new Date());
        ticket.setStatus(Ticket.Status.LEAVED);
        ticketRepository.save(ticket);
        log.info("Leaved: {} slot {}", ticket.getPlate(), ticket.getSlot());
        writeToLogAvailableSlots();
        return ticket.getSlot();
    }

    private synchronized void leaveVehicleSync(Ticket ticket) {
        for (byte i = 0; i < ticket.getNumberOfSlots() ; i++) {
            this.availableSlots[ticket.getSlot() + i] = (byte) (ticket.getSlot() + i);
        }
    }

    private String getVehicleType(byte numberOfSlots) {
        if(numberOfSlots == 1){
            return "Car";
        }else if(numberOfSlots == 2){
            return "Jeep";
        }
        return "Truck";
    }

    private int[] getSlots(Ticket ticket) {
        int[] slots = new int[ticket.getNumberOfSlots()];
        for (byte i = 0; i < ticket.getNumberOfSlots(); i++) {
            slots[i] = ticket.getSlot() + i;
        }
        return slots;
    }

    /*
     ** check is garage full for vehicle, it also depends on vehicle widht
     */
    private boolean isGarageFull(byte slotNumber) {
        return this.MAX_SLOT == 0 || getAvailableSlotsSize() == 0 || getAvailableSlotsSize() < slotNumber;
    }

    /*
     ** return how many slots is empty
     */
    private byte getAvailableSlotsSize() {
        byte sum = 0;
        for (byte i = 1; i < this.MAX_SLOT + 1; i++) {
            if (this.availableSlots[i] != -1) {
                sum = (byte) (sum + 1);
            }
        }
        return sum;
    }

    /*
     ** The nearest available index
     */
    private byte getAvailableSlotIndex() {
        for (byte i = 1; i < this.MAX_SLOT + 1; i++) {
            if (this.availableSlots[i] != -1) {
                return i;
            }
        }
        return -1;
    }

    /*
     ** At same time, only one thread can allocate slots because of concurrency problem
     */
    private synchronized void assignAvailableSlotNumber(byte slotNumber, String plate) throws GarageFullException {
        allocateSlotToVehicle(slotNumber, plate, getAvailableSlotIndex());
    }

    private void allocateSlotToVehicle(byte slotNumber, String plate, byte availableSlotIndex) throws GarageFullException {
        if (slotNumber == 1) {
            byte tempValue = this.availableSlots[availableSlotIndex];
            this.availableSlots[availableSlotIndex] = -1;
            this.vehiclePlateSlotNumberMap.put(plate, tempValue);
            log.info("Allocated 1 slot");

        } else {
            byte index = getAvailableSlotIndexForMoreThanOneSlotLenght(slotNumber, availableSlotIndex);
            this.vehiclePlateSlotNumberMap.put(plate, index);
            byte tempValue = this.availableSlots[index];
            for (byte i = 0; i < slotNumber; i++) {
                this.availableSlots[index] = -1;
                index = (byte) (index + 1);
            }
            this.vehiclePlateSlotNumberMap.put(plate, tempValue);
            log.info("Allocated {} slots", slotNumber);
        }

    }

    private byte getAvailableSlotIndexForMoreThanOneSlotLenght(byte slotNumber, byte availableSlotIndex) throws GarageFullException {
        byte sum = 1;
        byte tempIndex = availableSlotIndex;
        for (byte i = availableSlotIndex; i < MAX_SLOT + 1; i++) {
            if (this.availableSlots[i] + 1 == this.availableSlots[i + 1]) {
                sum = (byte) (sum + 1);
                if (sum == slotNumber) {
                    return tempIndex;
                }
            } else {
                sum = 1;
                tempIndex = (byte) (i + 1);
            }
        }
        log.info("Garage is full.");
        throw new GarageFullException();
    }
}















