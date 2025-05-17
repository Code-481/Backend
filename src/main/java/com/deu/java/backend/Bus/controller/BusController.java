package com.deu.java.backend.Bus.controller;

import com.deu.java.backend.Bus.dto.BusDTO;
import com.deu.java.backend.Bus.service.BusService;
import com.deu.java.backend.Bus.service.BusServiceFactory;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;

public class BusController {
    private final BusServiceFactory busFactory;

    public BusController(BusServiceFactory busFactory) {
        this.busFactory = busFactory;
    }

    public void handleGetBusInfo(Context ctx) {
        EntityManager em =ctx.attribute("em");
        String routeId = ctx.pathParam("routeId");
        BusService busService = busFactory.create(em);
        
        if (routeId == null || routeId.isEmpty()) {
            ctx.status(400).json(new Error("Missing routeId"));
            return;
        }

        BusDTO dto = busService.getRealTimeBusInfo(Long.parseLong(routeId));
        ctx.json(dto);
    }
}