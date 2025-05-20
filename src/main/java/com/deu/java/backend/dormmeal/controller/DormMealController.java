package com.deu.java.backend.dormmeal.controller;

import com.deu.java.backend.dormmeal.dto.MealDataDto;
import com.deu.java.backend.dormmeal.service.DormMealService;
import io.javalin.http.Context;

public class DormMealController {
    private final DormMealService service;

    public DormMealController(DormMealService service) {
        this.service = service;
    }

    public void getAllMealData(Context ctx) {
        try {
            MealDataDto data = service.getAllMealData();
            ctx.json(data);
        } catch (Exception e) {
            ctx.status(500).result("서버 오류: " + e.getMessage());
        }
    }
}