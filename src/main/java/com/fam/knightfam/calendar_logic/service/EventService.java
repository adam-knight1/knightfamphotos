package com.fam.knightfam.calendar_logic.service;



import com.fam.knightfam.calendar_logic.entity.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventService {
    List<Event> findAll();
    Optional<Event> findById(UUID eventId);
    Event save(Event event);
    void deleteById(UUID eventId);
}

