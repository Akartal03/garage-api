package com.kartal.garageapi.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class VehicleLeavingDto {
    @NotBlank
    private String ticketNumber;
}
