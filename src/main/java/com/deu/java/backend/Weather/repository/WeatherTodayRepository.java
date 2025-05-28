package com.deu.java.backend.Weather.repository;


import com.deu.java.backend.Weather.entity.WeatherTodayEntity;

import java.time.LocalDate;

public interface WeatherTodayRepository {
    WeatherTodayEntity findLatestAnnounceTimeToday(LocalDate today);
    void clearTodayWeather();
    void save(WeatherTodayEntity entity);
}