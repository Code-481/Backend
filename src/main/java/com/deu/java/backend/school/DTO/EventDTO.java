package com.deu.java.backend.school.DTO;

public class EventDTO {
    private String date;
    private String event;

    public EventDTO(String date, String event) {
        this.date = date;
        this.event = event;
    }

    // Getters and setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
}