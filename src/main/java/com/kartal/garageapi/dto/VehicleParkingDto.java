package com.kartal.garageapi.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class VehicleParkingDto {
    @NotBlank
    private String plate;
    @NotBlank
    private String color;
    @NotBlank
    private String type;
}
