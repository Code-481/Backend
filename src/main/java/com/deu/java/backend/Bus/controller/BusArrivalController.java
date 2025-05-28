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

    // DB에서 조회 (기존)
    public void handleGetArrivalInfo(Context ctx) {
        ctx.contentType("application/json; charset=UTF-8");
        String stopId = ctx.queryParam("stopId");

        try {
            List<BusArrivalDto> busArrivals;
            if (stopId == null || stopId.isBlank()) {
                ctx.status(400).result("stopId 파라미터가 필요합니다.");
                return;
            }

            if ("all".equalsIgnoreCase(stopId)) {
                // 모든 정류장 버스 현황 조회
                busArrivals = busArrivalService.getAllBusArrivalsFromDb();
            } else {
                // 특정 정류장만 조회
                busArrivals = busArrivalService.getBusArrivalsByRouteIdFromDb(stopId);
            }
            ctx.json(busArrivals);
        } catch (Exception e) {
            ctx.status(500).result("서버 오류: " + e.getMessage());
        }
    }
}
