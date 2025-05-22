package com.deu.java.backend.Weather;

import com.deu.java.backend.Weather.dto.WeatherTodayDTO;
import com.deu.java.backend.Weather.dto.WeatherWeekDTO;
import com.deu.java.backend.Weather.service.WeatherService;
import io.javalin.http.Context;
import java.util.List;

public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public void handleTodayWeather(Context ctx) {
        try {
            WeatherTodayDTO todayWeather = weatherService.getTodayWeather();
            ctx.contentType("application/json; charset=UTF-8");

            ctx.json(todayWeather);
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 에러 로그 출력
            ctx.status(500).result("날씨 데이터를 가져오는 중 오류가 발생했습니다.");
        }
    }

    public void handleWeekWeather(Context ctx) {
        List<WeatherWeekDTO> weekWeather = weatherService.getWeekWeather();
        ctx.json(weekWeather);
    }
}
