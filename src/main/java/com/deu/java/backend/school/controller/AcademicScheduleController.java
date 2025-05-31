package com.deu.java.backend.school.controller;

import com.deu.java.backend.school.DTO.AcademicScheduleDTO;
import com.deu.java.backend.school.service.AcademicScheduleService;
import io.javalin.Javalin;

public class AcademicScheduleController {
    private final AcademicScheduleService scheduleService;

    public AcademicScheduleController(AcademicScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }
    public void registerRoutes(Javalin app) {
        app.get("/api/academic-schedule", ctx -> {
            AcademicScheduleDTO schedule = scheduleService.get2025Schedule();
            ctx.json(schedule);
        });
    }

}