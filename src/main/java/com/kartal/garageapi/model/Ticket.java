package com.kartal.garageapi.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@RedisHash("Ticket")
@Builder
@Setter
@Getter
public class Ticket implements Serializable {
    @Id
    private String ticketNumber;
    private String color;
    private String plate;
    private Status status;
    private byte slot;
    private Date parketAt;
    private Date leavedAt;

    public enum Status {
        PARKED, LEAVED
    }
}
