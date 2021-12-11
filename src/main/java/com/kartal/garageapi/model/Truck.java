package com.kartal.garageapi.model;

public class Truck implements Vehicle{
    @Override
    public byte getSlotNumber() {
        return 4;
    }
}
