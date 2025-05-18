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
        String routeId = ctx.queryParam("routeId");
        
        if (routeId == null || routeId.isBlank()) {
            ctx.status(400).json(new Error("Missing RouteId"));
            return;
        }
        
        List<BusArrivalDto> busArrivals = busArrivalService.getBusArrivalsByStopId(routeId);

        ctx.json(busArrivals);
    }
}
