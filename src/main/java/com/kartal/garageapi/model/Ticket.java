package com.kartal.garageapi.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;

@RedisHash("Ticket")
@Builder
@Setter
@Getter
public class Ticket implements Serializable {
    @Id
    @Indexed
    private String ticketNumber;
    @Indexed
    private String plate;
    private String color;
    private Status status;
    private byte allocatedSlot;
    private Date parketAt;
    private Date leavedAt;
    private byte vehicleWidht;

    public enum Status {
        PARKED, LEAVED
    }
}
