package com.deu.java.backend.Festival.controller;

import com.deu.java.backend.Festival.service.FestivalService;
import com.deu.java.backend.Festival.DTO.FestivalDTO;
import io.javalin.http.Context;
import java.util.List;

public class FestivalController {
    private final FestivalService festivalService;

    public FestivalController(FestivalService festivalService) {
        this.festivalService = festivalService;
    }

    public void handleGetFestivalInfo(Context ctx) {
        List<FestivalDTO> festivalList = festivalService.getFestivalInfo();    
        ctx.json(festivalList);
    }
}
