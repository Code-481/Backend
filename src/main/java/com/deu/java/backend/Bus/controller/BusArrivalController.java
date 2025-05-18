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
            ctx.status(400).json(new Error("Missing stopId parameter"));
            return;
        }
        
        try {
            // DB에서 정보 조회 (스케줄러가 이미 DB에 저장해둔 정보)
            List<BusArrivalDto> busArrivals = busArrivalService.getBusArrivalsByRouteIdFromDb(stopId);
            ctx.json(busArrivals);
        } catch (Exception e) {
            ctx.status(500).json(new Error("Error fetching bus arrival data: " + e.getMessage()));
        }
    }
    
    // 에러 응답을 위한 내부 클래스
    private static class Error {
        private final String message;
        
        public Error(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
