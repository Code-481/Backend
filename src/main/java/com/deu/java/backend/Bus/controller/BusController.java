package com.deu.java.backend.Bus.controller;

import com.deu.java.backend.Bus.dto.BusDTO;
import com.deu.java.backend.Bus.service.BusService;
import io.javalin.http.Context;

public class BusController {
    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    public void handleGetBusInfo(Context ctx) {
        String routeId = ctx.pathParam("routeId");
        if (routeId == null || routeId.isEmpty()) {
            ctx.status(400).json(new Error("Missing routeId"));
            return;
        }

        BusDTO dto = busService.getRealTimeBusInfo(Long.parseLong(routeId));
        ctx.json(dto);
    }

    public static class Error {
        public String error;
        public Error(String message) {
            this.error = message;
        }
    }
}