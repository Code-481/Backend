package com.deu.java.backend.Bus.controller;

import com.deu.java.backend.Bus.service.BusArrivalService;
import com.deu.java.backend.Bus.dto.BusArrivalDto;
import io.javalin.http.Context;
import java.util.List;

public class BusArrivalController {

    private final BusArrivalService busArrivalService;

    public BusArrivalController(BusArrivalService arrivalService) {
        this.busArrivalService = arrivalService;
    }

    public void handleGetArrivalInfo(Context ctx) {
        ctx.contentType("application/json; charset=UTF-8");
        String stopId = ctx.queryParam("stopId");
        
        if (stopId == null || stopId.isBlank()) {
            ctx.status(400).json(new Error("Missing stopId"));
            return;
        }
        
        List<BusArrivalDto> busArrivals = busArrivalService.getBusArrivalsByStopId(stopId);

        ctx.json(busArrivals);
    }
}
