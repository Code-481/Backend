package com.deu.java.backend.Weather.repository;

import com.deu.java.backend.Weather.entity.WeatherTodayEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface WeatherTodayRepository {
    WeatherTodayEntity findLatestAnnounceTimeToday(LocalDate today);
    void clearTodayWeather();
    void save(WeatherTodayEntity entity);
}