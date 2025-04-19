package com.fam.knightfam.calendar_logic.dto;

import java.util.UUID;

public class EventDto {

    private UUID eventId;
    private String title;
    private String start;  // ISO‑8601 date string, e.g. "2025-05-01"

    public EventDto() {}

    public EventDto(UUID eventId, String title, String start) {
        this.eventId = eventId;
        this.title   = title;
        this.start   = start;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
}

