package com.deu.java.backend.dormmeal.scheduler;

import com.deu.java.backend.dormmeal.service.DormMealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MealDataScheduler {
    private static final Logger log = LoggerFactory.getLogger(MealDataScheduler.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final DormMealService mealService;

    public MealDataScheduler(DormMealService mealService) {
        this.mealService = mealService;

        // 서버 시작 시 데이터 로드
        log.info("서버 시작 시 식단 데이터 불러오기 시작: {}", LocalDateTime.now());
        try {
            mealService.fetchAndStoreAllMealData();
            log.info("서버 시작 시 식단 데이터 불러오기 완료: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("서버 시작 시 식단 데이터 불러오기 실패: {}", e.getMessage(), e);
        }
    }

    public void startMidnightScheduler() {
        long initialDelay = computeInitialDelayToMidnight();
        long period = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                log.info("자정 DB 갱신 시작: {}", LocalDateTime.now());
                mealService.fetchAndStoreAllMealData();
            } catch (Exception e) {
                log.error("자정 DB 갱신 오류: {}", e.getMessage(), e);
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
        log.info("자정 스케줄러 등록됨. 첫 실행까지 {} 초 남음.", initialDelay / 1000);
    }

    private long computeInitialDelayToMidnight() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime nextMidnight = now.plusDays(1).toLocalDate().atStartOfDay(ZoneId.of("Asia/Seoul"));
        return Duration.between(now, nextMidnight).toMillis();
    }



}
