package com.kartal.garageapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class VehicleStatusDto {
    private String plate;
    private String color;
    private int[] slots;
    private String type;
}
