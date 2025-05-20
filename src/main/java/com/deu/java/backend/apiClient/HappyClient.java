package com.deu.java.backend.apiClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HappyClient {
    private static final Logger log = LoggerFactory.getLogger(HappyClient.class);
    private static final String BASE_URL = "http://dorm.deu.ac.kr/deu/food/getWeeklyMenu.kmc?locgbn=DE&sch_date=";
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public HappyClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.MINUTES))
                .build();
        this.mapper = new ObjectMapper();
    }

    public List<Map<String, Object>> fetchWeeklyMenu(LocalDate baseDate) {
        String formattedDate = baseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String url = BASE_URL + formattedDate;

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String body = response.body().string();
            JsonNode root = mapper.readTree(body).path("root").get(0).path("WEEKLYMENU").get(0);

            List<Map<String, Object>> mealList = new ArrayList<>();
            for (int idx = 1; idx < 8; idx++) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("Date", root.path("fo_date" + idx).asText());
                dayData.put("breakfast", root.path("fo_menu_mor" + idx).asText());
                dayData.put("lunch", root.path("fo_menu_lun" + idx).asText());
                dayData.put("lunch_s", root.path("fo_sub_lun" + idx).asText());
                dayData.put("dinner", root.path("fo_menu_eve" + idx).asText());
                dayData.put("dinner_s", root.path("fo_sub_eve" + idx).asText());
                mealList.add(dayData);
            }
            return mealList;
        } catch (Exception e) {
            log.error("행복 기숙사 식단 데이터 가져오기 실패", e);
            return new ArrayList<>();
        }
    }
}