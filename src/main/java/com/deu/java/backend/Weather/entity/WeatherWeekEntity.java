package com.deu.java.backend.Weather.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "weather_week")
public class WeatherWeekEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;  // 예보 날짜 (각각의 날)

    @Column(nullable = false)
    private int minTemperature;

    @Column(nullable = false)
    private int maxTemperature;

    @Column(nullable = false)
    private String sky;

    @Column(nullable = false)
    private String cloud;

    public WeatherWeekEntity(LocalDate date, int minTemperature, int maxTemperature, String sky, String cloud) {
        this.date = date;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.sky = sky;
        this.cloud = cloud;
    }

    public WeatherWeekEntity() {

    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public String getSky() {
        return sky;
    }

    public String getCloud() {
        return cloud;
    }
}