package com.deu.java.backend.dormmeal.service;

import com.deu.java.backend.config.JpaUtil;
import com.deu.java.backend.dormmeal.dto.MealDataDto;
import com.deu.java.backend.dormmeal.entity.DormMealCache;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class DormMealService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public DormMealService() {
        startMidnightScheduler();
    }

    private void startMidnightScheduler() {
        long initialDelay = computeInitialDelayToMidnight();
        long period = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("자정 DB 갱신 시작: " + LocalDateTime.now());
                fetchAndStoreData();
            } catch (Exception e) {
                System.err.println("자정 DB 갱신 오류: " + e.getMessage());
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);

        System.out.println("자정 스케줄러 등록됨. 첫 실행까지 " + initialDelay / 1000 + "초 남음.");
    }

    private long computeInitialDelayToMidnight() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime nextMidnight = now.plusDays(1).toLocalDate().atStartOfDay(ZoneId.of("Asia/Seoul"));
        return Duration.between(now, nextMidnight).toMillis();
    }

    public void fetchAndStoreData() {
        try {
            LocalDate baseDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
            DateTimeFormatter dashFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter noDashFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            List<Map<String, String>> weekDates = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate date = baseDate.plusDays(i);
                Map<String, String> dateMap = new HashMap<>();
                dateMap.put("dash", date.format(dashFormatter));
                dateMap.put("nodash", date.format(noDashFormatter));
                weekDates.add(dateMap);
            }

            String koreaDate = baseDate.format(dashFormatter);
            Map<String, Object> result = new HashMap<>();
            result.put("hyomin", fetchHyominData(koreaDate));
            result.put("happy", fetchHappyData(koreaDate));
            result.put("meals", fetchMealData(weekDates));

            String jsonData = mapper.writeValueAsString(result);
            LocalDateTime updatedAt = LocalDateTime.now();

            saveToCache(jsonData, updatedAt);

            System.out.println("데이터 수집 및 저장 완료: " + updatedAt);
        } catch (Exception e) {
            System.err.println("데이터 수집 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Map<String, Object>> fetchHyominData(String koreaDate) {
        String url = "http://dorm.deu.ac.kr/hyomin/food/getWeeklyMenu.kmc?locgbn=DE&sch_date=" + koreaDate;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
            String body = response.body().string();
            JsonNode root = mapper.readTree(body).path("root").get(0).path("WEEKLYMENU").get(0);
            List<Map<String, Object>> hyominList = new ArrayList<>();
            for (int idx = 0; idx < 8; idx++) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("Date", root.path("fo_date" + idx).asText());
                dayData.put("breakfast", root.path("fo_menu_mor" + idx).asText());
                dayData.put("lunch", root.path("fo_menu_lun" + idx).asText());
                dayData.put("dinner", root.path("fo_menu_eve" + idx).asText());
                hyominList.add(dayData);
            }
            return hyominList;
        } catch (Exception e) {
            List<Map<String, Object>> errorList = new ArrayList<>();
            errorList.add(Collections.singletonMap("Date", "No data"));
            return errorList;
        }
    }

    private List<Map<String, Object>> fetchHappyData(String koreaDate) {
        String url = "http://dorm.deu.ac.kr/deu/food/getWeeklyMenu.kmc?locgbn=DE&sch_date=" + koreaDate;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
            String body = response.body().string();
            JsonNode root = mapper.readTree(body).path("root").get(0).path("WEEKLYMENU").get(0);
            List<Map<String, Object>> happyList = new ArrayList<>();
            for (int idx = 1; idx < 8; idx++) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("Date", root.path("fo_date" + idx).asText());
                dayData.put("breakfast", root.path("fo_menu_mor" + idx).asText());
                dayData.put("lunch", root.path("fo_menu_lun" + idx).asText());
                dayData.put("lunch_s", root.path("fo_sub_lun" + idx).asText());
                dayData.put("dinner", root.path("fo_menu_eve" + idx).asText());
                dayData.put("dinner_s", root.path("fo_sub_eve" + idx).asText());
                happyList.add(dayData);
            }
            return happyList;
        } catch (Exception e) {
            List<Map<String, Object>> errorList = new ArrayList<>();
            errorList.add(Collections.singletonMap("Date", "No data"));
            return errorList;
        }
    }

    private List<Map<String, Object>> fetchMealData(List<Map<String, String>> weekDates) {
        List<Map<String, Object>> mealsList = new ArrayList<>();
        for (Map<String, String> date : weekDates) {
            Map<String, Object> mealData = new HashMap<>();
            mealData.put("date", date.get("dash"));
            try {
                String url = String.format("https://smart.deu.ac.kr/m/sel_dfood?date=%s&gubun2=2&gubun1=1", date.get("nodash"));
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        mealData.put("information", body.contains("정보공학관") ? body : null);
                    } else {
                        mealData.put("information", null);
                    }
                }
            } catch (Exception e) {
                mealData.put("information", null);
            }
            try {
                String url = String.format("https://smart.deu.ac.kr/m/sel_dfood?date=%s&gubun2=1&gubun1=1", date.get("nodash"));
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        mealData.put("suduck", body.contains("수덕전") ? body : null);
                    } else {
                        mealData.put("suduck", null);
                    }
                }
            } catch (Exception e) {
                mealData.put("suduck", null);
            }
            mealsList.add(mealData);
        }
        return mealsList;
    }

    private void saveToCache(String jsonData, LocalDateTime updatedAt) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            DormMealCache cache = em.find(DormMealCache.class, 1L);
            if (cache == null) {
                cache = new DormMealCache(jsonData, updatedAt);
                cache.setId(1L);
                em.persist(cache);
            } else {
                cache.setJsonData(jsonData);
                cache.setUpdatedAt(updatedAt);
                em.merge(cache);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("JPA 저장 오류: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public MealDataDto getAllMealData() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            DormMealCache cache = em.find(DormMealCache.class, 1L);
            if (cache == null) throw new RuntimeException("데이터를 찾을 수 없습니다.");
            Map<String, Object> dataMap = mapper.readValue(cache.getJsonData(), Map.class);
            return new MealDataDto(
                    (List<Map<String, Object>>) dataMap.get("hyomin"),
                    (List<Map<String, Object>>) dataMap.get("happy"),
                    (List<Map<String, Object>>) dataMap.get("meals"),
                    cache.getUpdatedAt()
            );
        } catch (Exception e) {
            throw new RuntimeException("데이터 조회 중 오류 발생: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
