package com.fam.knightfam.calendar_logic.controller;

import com.fam.knightfam.calendar_logic.dto.EventDto;
import com.fam.knightfam.calendar_logic.entity.Event;
import com.fam.knightfam.calendar_logic.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class CalendarRestController {

    private final EventService eventService;  // you’ll build this

    public CalendarRestController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDto> listEvents() {
        return eventService.findAll().stream()
                .map(e -> new EventDto(
                        e.getEventId(),
                        e.getTitle(),
                        e.getEventDate().toString()   // FullCalendar expects an ISO date
                ))
                .collect(Collectors.toList());
    }

}


