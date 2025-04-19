package com.fam.knightfam.calendar_logic.repository;


import com.fam.knightfam.calendar_logic.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    // You can add custom queries here if you need filtering by date, title, etc.
}

