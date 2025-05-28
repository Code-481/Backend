package com.deu.java.backend.Weather.repository;

import com.deu.java.backend.Weather.entity.WeatherTodayEntity;

import java.time.LocalDate;
import java.util.List;

public interface WeatherWeekRepository {
    List<WeatherTodayEntity.WeatherWeekEntity> findWeekWeather(LocalDate startDate, LocalDate endDate);
    void clearWeekWeather(LocalDate startDate, LocalDate endDate);
    void saveAll(List<WeatherTodayEntity.WeatherWeekEntity> entities);
}