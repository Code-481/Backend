package com.deu.java.backend.dormmeal.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MealDataDto {
    public List<Map<String, Object>> hyomin;
    public List<Map<String, Object>> happy;
    public List<Map<String, Object>> meals;
    public LocalDateTime lastUpdated;

    public MealDataDto() {}
    public MealDataDto(List<Map<String, Object>> hyomin, List<Map<String, Object>> happy,
                       List<Map<String, Object>> meals, LocalDateTime lastUpdated) {
        this.hyomin = hyomin;
        this.happy = happy;
        this.meals = meals;
        this.lastUpdated = lastUpdated;
    }
}
