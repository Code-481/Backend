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
        String stopId = ctx.queryParam("stopId");
        List<BusArrivalDto> busArrivals = busArrivalService.getBusArrivalsByStopId(stopId);

        ctx.json(busArrivals);
    }

    public static class Error {

        private String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
