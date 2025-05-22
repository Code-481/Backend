package com.deu.java.backend.Weather.DTO;

import java.time.LocalDateTime;

public class WeatherTodayDTO {

    private final String date;    // AM or PM
    private final int temperature;  //
    private final String sky;       //
    private final String cloud;     //

    public WeatherTodayDTO(String date, int temperature, String sky, String cloud) {
        this.date= date.toString();
        this.temperature = temperature;
        this.sky = sky;
        this.cloud = cloud;
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