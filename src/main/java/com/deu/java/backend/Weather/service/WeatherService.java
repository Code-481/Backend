package com.deu.java.backend.Weather.service;

import com.deu.java.backend.Weather.dto.WeatherTodayDTO;
import com.deu.java.backend.Weather.dto.WeatherWeekDTO;
import java.util.List;


public interface WeatherService {
    WeatherTodayDTO getTodayWeather();
    List<WeatherWeekDTO> getWeekWeather();
    List<WeatherTodayDTO> getTodayWeatherMorningAfternoon();
}