package com.deu.java.backend.Festival.controller;

import com.deu.java.backend.Festival.DTO.FestivalDto;
import com.deu.java.backend.Festival.service.FestivalService;

import javax.naming.Context;
import java.util.List;

public class FestivalController {
    private final FestivalService festivalService = new FestivalService();

    // 반드시 io.javalin.http.Context 사용!
    public void getFestivalsAsJson(io.javalin.http.Context ctx) {
        try {
            List<FestivalDto> festivals = festivalService.getFestivals();
            ctx.json(festivals);
        } catch (Exception e) {
            ctx.status(500).result("Error: " + e.getMessage());
        }
    }
}