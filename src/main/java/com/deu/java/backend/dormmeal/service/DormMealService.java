package com.deu.java.backend.dormmeal.service;

import com.deu.java.backend.config.JpaUtil;
import com.deu.java.backend.apiClient.CafeteriaClient;
import com.deu.java.backend.apiClient.HappyClient;
import com.deu.java.backend.apiClient.HyominClient;
import com.deu.java.backend.entity.DormMeal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DormMealService {
    private static final Logger log = LoggerFactory.getLogger(DormMealService.class);
    private final HyominClient hyominClient;
    private final HappyClient happyClient;
    private final CafeteriaClient cafeteriaClient;

    public DormMealService() {
        this.hyominClient = new HyominClient();
        this.happyClient = new HappyClient();
        this.cafeteriaClient = new CafeteriaClient();
    }

    public void fetchAndStoreAllMealData() {
        try {
            LocalDate baseDate = LocalDate.now();

            // 데이터 가져오기
            List<Map<String, Object>> hyominData = hyominClient.fetchWeeklyMenu(baseDate);
            List<Map<String, Object>> happyData = happyClient.fetchWeeklyMenu(baseDate);

            // 학교 식당 데이터 가져오기
            List<Map<String, String>> cafeteriaData = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate date = baseDate.plusDays(i);
                String informationMeal = cafeteriaClient.fetchMealData(date, "information");
                String suduckMeal = cafeteriaClient.fetchMealData(date, "suduck");

                if (informationMeal != null || suduckMeal != null) {
                    Map<String, String> dayData = Map.of(
                            "date", date.toString(),
                            "information", informationMeal != null ? informationMeal : "",
                            "suduck", suduckMeal != null ? suduckMeal : ""
                    );
                    cafeteriaData.add(dayData);
                }
            }

            // DB 초기화 및 데이터 저장
            clearAndSaveMealData(hyominData, happyData, cafeteriaData);

        } catch (Exception e) {
            log.error("식단 데이터 수집 및 저장 중 오류 발생", e);
        }
    }

    private void clearAndSaveMealData(List<Map<String, Object>> hyominData, List<Map<String, Object>> happyData, List<Map<String, String>> cafeteriaData) {
        try (EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                // 테이블 초기화
                em.createNativeQuery("TRUNCATE TABLE dorm_meals").executeUpdate();

                // 효민 기숙사 식단 저장
                for (Map<String, Object> dayData : hyominData) {
                    processDormData(em, dayData, "hyomin");
                }

                // 행복 기숙사 식단 저장
                for (Map<String, Object> dayData : happyData) {
                    processDormData(em, dayData, "happy");
                }

                // 학교 식당 식단 저장
                for (Map<String, String> dayData : cafeteriaData) {
                    String dateStr = dayData.get("date");
                    if (dateStr == null) continue;

                    LocalDate mealDate = LocalDate.parse(dateStr);

                    // 정보공학관 식단
                    String information = dayData.get("information");
                    if (information != null && !information.isEmpty()) {
                        saveMeal(em, mealDate, "information", "lunch", information);
                    }

                    // 수덕전 식단
                    String suduck = dayData.get("suduck");
                    if (suduck != null && !suduck.isEmpty()) {
                        saveMeal(em, mealDate, "suduck", "lunch", suduck);
                    }
                }

                tx.commit();
                log.info("식단 데이터 DB 저장 완료: {}", LocalDateTime.now());
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                log.error("식단 데이터 DB 저장 실패", e);
                throw e;
            }
        }
    }

    private void processDormData(EntityManager em, Map<String, Object> dayData, String dormType) {
        String dateStr = (String) dayData.get("Date");
        if (dateStr == null || dateStr.isEmpty() || "No data".equals(dateStr)) {
            return;
        }

        LocalDate mealDate;
        try {
            mealDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            log.error("날짜 파싱 오류: {} - {}", dateStr, e.getMessage());
            return;
        }

        // 아침 식단
        String breakfast = (String) dayData.get("breakfast");
        if (breakfast != null && !breakfast.isEmpty()) {
            saveMeal(em, mealDate, dormType, "breakfast", breakfast);
        }

        // 점심 식단
        String lunch = (String) dayData.get("lunch");
        if (lunch != null && !lunch.isEmpty()) {
            saveMeal(em, mealDate, dormType, "lunch", lunch);
        }

        // 저녁 식단
        String dinner = (String) dayData.get("dinner");
        if (dinner != null && !dinner.isEmpty()) {
            saveMeal(em, mealDate, dormType, "dinner", dinner);
        }

        // 행복 기숙사 특식 처리
        if ("happy".equals(dormType)) {
            // 점심 특식
            String lunchS = (String) dayData.get("lunch_s");
            if (lunchS != null && !lunchS.isEmpty()) {
                saveMeal(em, mealDate, dormType, "lunch_s", lunchS);
            }

            // 저녁 특식
            String dinnerS = (String) dayData.get("dinner_s");
            if (dinnerS != null && !dinnerS.isEmpty()) {
                saveMeal(em, mealDate, dormType, "dinner_s", dinnerS);
            }
        }
    }

    private void saveMeal(EntityManager em, LocalDate mealDate, String dormType, String mealType, String mealContent) {
        try {
            Query query = em.createQuery(
                    "SELECT m FROM DormMeal m WHERE m.mealDate = :mealDate AND m.dormType = :dormType AND m.mealType = :mealType");
            query.setParameter("mealDate", mealDate);
            query.setParameter("dormType", dormType);
            query.setParameter("mealType", mealType);

            List<DormMeal> existingMeals = query.getResultList();

            if (existingMeals.isEmpty()) {
                // 새로운 데이터 저장
                DormMeal newMeal = new DormMeal(mealDate, dormType, mealType, mealContent);
                em.persist(newMeal);
            } else {
                // 기존 데이터 업데이트
                DormMeal existingMeal = existingMeals.get(0);
                existingMeal.setMealContent(mealContent);
                existingMeal.setUpdatedAt(LocalDateTime.now());
                em.merge(existingMeal);
            }
        } catch (Exception e) {
            log.error("식단 저장 중 오류: {}", e.getMessage());
            throw e;
        }
    }
}
