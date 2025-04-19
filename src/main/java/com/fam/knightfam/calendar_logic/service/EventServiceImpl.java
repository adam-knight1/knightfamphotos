package com.fam.knightfam.calendar_logic.service;

import com.fam.knightfam.calendar_logic.entity.Event;
import com.fam.knightfam.calendar_logic.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    public EventServiceImpl(EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Event> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Event> findById(UUID eventId) {
        return repository.findById(eventId);
    }

    @Override
    public Event save(Event event) {
        return repository.save(event);
    }

    @Override
    public void deleteById(UUID eventId) {
        repository.deleteById(eventId);
    }
}

