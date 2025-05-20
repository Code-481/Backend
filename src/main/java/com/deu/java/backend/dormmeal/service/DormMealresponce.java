package com.deu.java.backend.dormmeal.service;

import com.deu.java.backend.config.JpaUtil;
import com.deu.java.backend.entity.DormMeal;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DormMealresponce {
    private EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();


    public DormMealresponce(String dormMealId) {

    };

    // 특정 기숙사 타입의 식단 정보를 가져오는 메서드
    public List<Map<String, Object>> get_dormmealresponce(String dormMealId) {
        // 파라미터 바인딩을 사용하여 SQL 인젝션 방지
        Query q = em.createNativeQuery("SELECT * FROM dorm_meals WHERE dorm_type = ?", DormMeal.class);
        q.setParameter(1, dormMealId);
        List<DormMeal> dormMeals = q.getResultList();

        // 결과를 원하는 형태로 변환
        List<Map<String, Object>> formattedResults = new ArrayList<>();

        for (DormMeal meal : dormMeals) {
            try {
                if("happy".equals(meal.getDormType()) || "suduck".equals(meal.getDormType())) {
                    Map<String, Object> originalMeal = new HashMap<>();
                    String date = meal.getMealDate().toString();
                    originalMeal.put("date", date.replaceAll("[\\[\\]]", "").replace(", ", "-"));                    originalMeal.put("dormType", meal.getDormType());
                    originalMeal.put("food_menu", meal.getMealContent());
                    originalMeal.put("getMealType", meal.getMealType());
                    formattedResults.add(originalMeal);
                } else {
                    // JSON 형식인지 확인
                    String mealContent = meal.getMealContent();
                    if (isValidJson(mealContent)) {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                        // mealContent JSON 문자열을 파싱
                        Map<String, List<Map<String, Object>>> mealContentMap =
                                mapper.readValue(mealContent,
                                        new TypeReference<Map<String, List<Map<String, Object>>>>() {});

                        // 각 키오스크별로 분리된 객체 생성
                        List<Map<String, List<Map<String, Object>>>> kioskList = new ArrayList<>();
                        for (Map.Entry<String, List<Map<String, Object>>> entry : mealContentMap.entrySet()) {
                            Map<String, List<Map<String, Object>>> kioskMap = new HashMap<>();
                            kioskMap.put(entry.getKey(), entry.getValue());
                            kioskList.add(kioskMap);
                        }

                        // 최종 결과에 추가
                        Map<String, Object> formattedMeal = new HashMap<>();
                        formattedMeal.put("dormType", meal.getDormType());
                        formattedMeal.put("kiosks", kioskList);
                        formattedResults.add(formattedMeal);
                    } else {
                        // JSON이 아닌 경우 원본 데이터 추가
                        Map<String, Object> originalMeal = new HashMap<>();
                        String date = meal.getMealDate().toString();
                        originalMeal.put("date", date.replaceAll("[\\[\\]]", "").replace(", ", "-"));                        originalMeal.put("dormType", meal.getDormType());
                        originalMeal.put("food_menu", mealContent);
                        originalMeal.put("getMealType", meal.getMealType());
                        formattedResults.add(originalMeal);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 예외 발생 시 기본 정보만 포함
                Map<String, Object> errorMeal = new HashMap<>();
                errorMeal.put("dormType", meal.getDormType());
                errorMeal.put("error", "데이터 처리 중 오류 발생");
                formattedResults.add(errorMeal);
            }
        }

        return formattedResults;
    }

    // 모든 기숙사 타입의 식단 정보를 가져오는 메서드
    public Map<String, List<Map<String, Object>>> get_dormmealresponceall() {
        // 각 기숙사별 쿼리 실행 (Entity 매핑 사용)
        Query happyq = em.createNativeQuery("SELECT * FROM dorm_meals WHERE dorm_type = 'happy'", DormMeal.class);
        Query hyominq = em.createNativeQuery("SELECT * FROM dorm_meals WHERE dorm_type = 'hyomin'", DormMeal.class);
        Query informationq = em.createNativeQuery("SELECT * FROM dorm_meals WHERE dorm_type = 'information'", DormMeal.class);
        Query suduckq = em.createNativeQuery("SELECT * FROM dorm_meals WHERE dorm_type = 'suduck'", DormMeal.class);

        // 각 쿼리 결과를 변환된 형태로 저장할 Map 생성
        Map<String, List<Map<String, Object>>> formattedDormMeals = new HashMap<>();

        // 각 기숙사 타입에 대해 처리
        processAndFormatResults(happyq.getResultList(), "happy", formattedDormMeals);
        processAndFormatResults(hyominq.getResultList(), "hyomin", formattedDormMeals);
        processAndFormatResults(informationq.getResultList(), "information", formattedDormMeals);
        processAndFormatResults(suduckq.getResultList(), "suduck", formattedDormMeals);

        return formattedDormMeals;
    }

    // 결과 처리 및 포맷팅을 위한 헬퍼 메서드
    private void processAndFormatResults(List<DormMeal> meals, String dormType,
                                         Map<String, List<Map<String, Object>>> formattedResults) {
        List<Map<String, Object>> formattedMeals = new ArrayList<>();

        for (DormMeal meal : meals) {
            try {
                // 문자열 비교는 equals() 메서드 사용
                if("happy".equals(meal.getDormType()) || "suduck".equals(meal.getDormType())) {
                    // 파싱 실패 시 원본 데이터 추가
                    Map<String, Object> originalMeal = new HashMap<>();
                    String date = meal.getMealDate().toString();
                    originalMeal.put("date", date.replaceAll("[\\[\\]]", "").replace(", ", "-"));
                    originalMeal.put("dormType", meal.getDormType());
                    originalMeal.put("getMealType", meal.getMealType());
                    originalMeal.put("food_menu", meal.getMealContent());
                    formattedMeals.add(originalMeal);
                } else {
                    String mealContent = meal.getMealContent();
                    // JSON 형식인지 확인
                    if (isValidJson(mealContent)) {
                        // ObjectMapper 설정
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                        // mealContent JSON 문자열을 파싱
                        Map<String, List<Map<String, Object>>> mealContentMap =
                                mapper.readValue(mealContent,
                                        new TypeReference<Map<String, List<Map<String, Object>>>>() {});

                        // 각 키오스크별로 분리된 객체 생성
                        List<Map<String, List<Map<String, Object>>>> kioskList = new ArrayList<>();

                        for (Map.Entry<String, List<Map<String, Object>>> entry : mealContentMap.entrySet()) {
                            Map<String, List<Map<String, Object>>> kioskMap = new HashMap<>();
                            kioskMap.put(entry.getKey(), entry.getValue());
                            kioskList.add(kioskMap);
                        }

                        // 최종 결과에 추가
                        Map<String, Object> formattedMeal = new HashMap<>();
                        formattedMeal.put("id", meal.getId());
                        formattedMeal.put("dormType", meal.getDormType());
                        formattedMeal.put("updatedAt", meal.getUpdatedAt());
                        formattedMeal.put("kiosks", kioskList);

                        formattedMeals.add(formattedMeal);
                    } else {
                        // JSON이 아닌 경우 원본 데이터 추가
                        Map<String, Object> originalMeal = new HashMap<>();
                        String date = meal.getMealDate().toString();
                        originalMeal.put("date", date.replaceAll("[\\[\\]]", "").replace(", ", "-"));
                        originalMeal.put("dormType", meal.getDormType());
                        originalMeal.put("getMealType", meal.getMealType());
                        originalMeal.put("food_menu", mealContent);
                        formattedMeals.add(originalMeal);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 예외 발생 시 기본 정보만 포함
                Map<String, Object> errorMeal = new HashMap<>();
                errorMeal.put("dormType", meal.getDormType());
                errorMeal.put("error", "데이터 처리 중 오류 발생");
                formattedMeals.add(errorMeal);
            }
        }

        formattedResults.put(dormType, formattedMeals);
    }

    // JSON 유효성 검사 헬퍼 메서드
    private boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        try {
            // JSON 시작 문자 확인
            if (json.trim().startsWith("{") || json.trim().startsWith("[")) {
                new ObjectMapper().readTree(json);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
