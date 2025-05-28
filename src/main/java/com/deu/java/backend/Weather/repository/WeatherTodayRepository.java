package com.deu.java.backend.Weather.repository;


import com.deu.java.backend.Weather.entity.WeatherWeekEntity;

import java.time.LocalDate;

public interface WeatherTodayRepository {
    WeatherWeekEntity.WeatherTodayEntity findLatestAnnounceTimeToday(LocalDate today);
    void clearTodayWeather();
    void save(WeatherWeekEntity.WeatherTodayEntity entity);
}