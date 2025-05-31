package com.deu.java.backend.school.DTO;


import java.util.List;
import java.util.Map;

public class AcademicScheduleDTO {
    private int year;
    private Map<String, List<EventDTO>> monthlySchedule;
    private Map<String, List<EventDTO>> semesterSchedule;
    private String lastUpdated;

    public AcademicScheduleDTO(int year, Map<String, List<EventDTO>> monthlySchedule,
                               Map<String, List<EventDTO>> semesterSchedule, String lastUpdated) {
        this.year = year;
        this.monthlySchedule = monthlySchedule;
        this.semesterSchedule = semesterSchedule;
        this.lastUpdated = lastUpdated;
    }

    // Getters and setters
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public Map<String, List<EventDTO>> getMonthlySchedule() { return monthlySchedule; }
    public void setMonthlySchedule(Map<String, List<EventDTO>> monthlySchedule) { this.monthlySchedule = monthlySchedule; }

    public Map<String, List<EventDTO>> getSemesterSchedule() { return semesterSchedule; }
    public void setSemesterSchedule(Map<String, List<EventDTO>> semesterSchedule) { this.semesterSchedule = semesterSchedule; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}