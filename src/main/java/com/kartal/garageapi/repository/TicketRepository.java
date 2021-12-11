package com.kartal.garageapi.repository;

import com.kartal.garageapi.model.Ticket;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, String> {
}
