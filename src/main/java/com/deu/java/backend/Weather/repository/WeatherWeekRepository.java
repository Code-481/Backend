package com.deu.java.backend.Weather.repository;

import com.deu.java.backend.entity.WeatherWeekEntity;
import java.time.LocalDate;
import java.util.List;

public interface WeatherWeekRepository {
    List<WeatherWeekEntity> findWeekWeather(LocalDate startDate, LocalDate endDate);
    void clearWeekWeather(LocalDate startDate, LocalDate endDate);
    void saveAll(List<WeatherWeekEntity> entities);
}