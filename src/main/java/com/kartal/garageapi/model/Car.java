package com.kartal.garageapi.model;

public class Car implements Vehicle{
    @Override
    public byte getSlotNumber() {
        return 1;
    }
}
