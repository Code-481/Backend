package com.deu.java.backend.Weather.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_today")
public class WeatherTodayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String date;  // 오늘 날짜

    @Column(nullable = false)
    private int temperature;

    @Column(nullable = false)
    private String sky;

    @Column(nullable = false)
    private String cloud;

    public WeatherTodayEntity(String date,int temperature, String sky, String cloud) {
        this.date = date;
        this.temperature = temperature;
        this.sky = sky;
        this.cloud = cloud;
    }

    public WeatherTodayEntity() {

    }

    public Long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getSky() {
        return sky;
    }

    public String getCloud() {
        return cloud;
    }
}