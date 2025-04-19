package com.fam.knightfam.calendar_logic.controller;

import com.fam.knightfam.calendar_logic.dto.EventDto;
import com.fam.knightfam.calendar_logic.entity.Event;
import com.fam.knightfam.calendar_logic.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

@Controller
public class CalendarController {

    private final EventService eventService;

    // constructor injection; Spring will wire in your EventServiceImpl
    public CalendarController(EventService eventService) {
        this.eventService = eventService;
    }

    // 1. Show the calendar view
    @GetMapping("/calendar")
    public String viewCalendar() {
        return "calendar";
    }

    // 2. Show the “create event” form
    @GetMapping("/calendar/create")
    public String showCreateForm(Model model) {
        model.addAttribute("eventDto", new EventDto());
        return "create-event";
    }

    // 3. Handle the form submit
    @PostMapping("/calendar/create")
    public String createEvent(@ModelAttribute EventDto eventDto) {
        // map DTO → Entity yourself or via a mapper
        Event e = new Event();
        e.setTitle(eventDto.getTitle());
        e.setEventDate(LocalDate.parse(eventDto.getStart()));
        eventService.save(e);

        return "redirect:/calendar";
    }
}

