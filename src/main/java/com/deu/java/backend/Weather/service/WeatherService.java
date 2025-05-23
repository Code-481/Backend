package com.deu.java.backend.Weather.service;


import com.deu.java.backend.Weather.DTO.WeatherTodayDTO;
import com.deu.java.backend.Weather.DTO.WeatherWeekDTO;
import java.util.List;


public interface WeatherService {
    WeatherTodayDTO getTodayWeather();
    List<WeatherWeekDTO> getWeekWeather();
}