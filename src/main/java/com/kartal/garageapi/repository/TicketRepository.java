package com.kartal.garageapi.repository;

import com.kartal.garageapi.model.Ticket;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TicketRepository extends CrudRepository<Ticket, String> {
    Optional<Ticket> findByPlate(String plate);
}
