package com.deu.java.backend.dormmeal.controller;


import com.deu.java.backend.dormmeal.service.DormMealService;
import com.deu.java.backend.dormmeal.service.DormMealresponce;
import io.javalin.http.Context;


public class DormMealController {
    public final DormMealService dormMealService;

    public DormMealController(DormMealService dormMealService) {
        this.dormMealService = dormMealService;
    }

    // DB에서 조회 (기존)
    public void handleGetDormMealInfo(Context ctx) {
        ctx.contentType("application/json; charset=UTF-8");
        String paramdata = ctx.queryParam("place");
        try {
            //파라미터가 없으면
            if (paramdata !=  null || !paramdata.equals("")) {
                DormMealresponce  dormMealresponce = new DormMealresponce(paramdata);
                ctx.json(dormMealresponce.get_dormmealresponce(paramdata));
            }else{
                DormMealresponce  dormMealresponce = new DormMealresponce("");
                ctx.json(dormMealresponce.get_dormmealresponceall());
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }


}
