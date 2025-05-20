package com.deu.java.backend.Bus.controller;

import com.deu.java.backend.Bus.service.BusArrivalService;
import com.deu.java.backend.Bus.dto.BusArrivalDto;
import com.deu.java.backend.apiClient.BusanBimsApiClient;
import io.javalin.http.Context;
import java.util.List;

public class BusArrivalController {
    private final BusArrivalService busArrivalService;
    private final BusanBimsApiClient apiClient = new BusanBimsApiClient();

    public BusArrivalController(BusArrivalService arrivalService) {
        this.busArrivalService = arrivalService;
    }

    // 실시간으로 API에서 받아서 DB에 저장하고, 그 결과를 반환
    public void handleUpdateAndGetArrivalInfo(Context ctx) {
        ctx.contentType("application/json; charset=UTF-8");
        String stopId = ctx.queryParam("stopId");
        if (stopId == null || stopId.isBlank()) {
            ctx.status(400).json(new Error("Missing stopId parameter"));
            return;
        }
    }

    // DB에서 조회 (기존)
    public void handleGetArrivalInfo(Context ctx) {
        ctx.contentType("application/json; charset=UTF-8");
        String stopId = ctx.queryParam("stopId");
        if (stopId == null || stopId.isBlank()) {
            ctx.status(400).json(new Error("Missing stopId parameter"));
            return;
        }
        try {
            List<BusArrivalDto> busArrivals = busArrivalService.getBusArrivalsByRouteIdFromDb(stopId);
            ctx.json(busArrivals);
        } catch (Exception e) {
            ctx.status(500).json(new Error("Error fetching bus arrival data: " + e.getMessage()));
        }
    }

    private static class Error {
        private final String message;
        public Error(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}
