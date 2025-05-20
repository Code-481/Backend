package com.deu.java.backend.apiClient;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class CafeteriaClient {
    private static final Logger log = LoggerFactory.getLogger(CafeteriaClient.class);
    private final OkHttpClient client;
    
    public CafeteriaClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.MINUTES))
                .build();
    }
    
    public String fetchMealData(LocalDate date, String cafeteriaType) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String gubun2 = "information".equals(cafeteriaType) ? "2" : "1";
        String url = String.format("https://smart.deu.ac.kr/m/sel_dfood?date=%s&gubun2=%s&gubun1=1", formattedDate, gubun2);
        
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;
            
            String body = response.body().string();
            String checkString = "information".equals(cafeteriaType) ? "정보공학관" : "수덕전";
            return body.contains(checkString) ? body : null;
        } catch (Exception e) {
            log.error("식당 식단 데이터 가져오기 실패: " + cafeteriaType, e);
            return null;
        }
    }
}