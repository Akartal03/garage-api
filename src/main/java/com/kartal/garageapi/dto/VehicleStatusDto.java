package com.kartal.garageapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class VehicleStatusDto {
    private String plate;
    private String color;
    private int[] slots;
    private String type;
}
