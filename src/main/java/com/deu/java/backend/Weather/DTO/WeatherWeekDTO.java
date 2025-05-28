package com.deu.java.backend.Weather.DTO;

import java.time.LocalDate;

public class WeatherWeekDTO {

    private final String date;
    private final int minTemperature;
    private final int maxTemperature;
    private final String sky;
    private final String cloud;

    public WeatherWeekDTO(LocalDate date, int minTemperature, int maxTemperature, String sky, String cloud) {
        this.date = date.toString();
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.sky = sky;
        this.cloud = cloud;
    }

    public String getDate() {
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