package com.deu.java.backend.Bus.scheduler;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import com.deu.java.backend.Bus.service.BusArrivalService;
import com.deu.java.backend.Bus.service.BusArrivalServiceImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BusArrivalScheduler {

    private final BusArrivalService busArrivalService;
    private final ScheduledExecutorService scheduler;

    // 정류소 ID 목록 (10개) 만약을 위해서 동의대역도 추가 함
    private final List<String> stopIds = Arrays.asList(
            "163980104", "172520304", "172440302", "511700000", "163980103",
            "172520303", "163980102", "172520302", "163980101", "172520301");

    // 운행 시간 설정 (한국 시간 기준)
    private final LocalTime startTime = LocalTime.of(7, 30);
    private final LocalTime endTime = LocalTime.of(22, 0);

    public BusArrivalScheduler(BusArrivalService busArrivalService) {
        this.busArrivalService = busArrivalService;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startScheduling() {
        // 2분마다 실행되는 작업 스케줄링
        scheduler.scheduleAtFixedRate(this::fetchAndSaveBusArrivals, 0, 2, TimeUnit.MINUTES);
    }

    private void fetchAndSaveBusArrivals() {
        // 현재 한국 시간 확인
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalTime currentTime = now.toLocalTime();

        // 운행 시간(오전 5시 ~ 오후 11시) 외에는 실행하지 않음
        if (currentTime.isBefore(startTime) || currentTime.isAfter(endTime)) {
            System.out.println("운행 시간이 아닙니다. 현재 시간: " + currentTime);
            return;
        }

        System.out.println("정류소 정보 업데이트 시작: " + now);

        for (String stopId : stopIds) {
            try {
                List<BusArrivalDto> arrivals = busArrivalService.getBusArrivalsByStopId(stopId);

                // 여기서 arrivals 리스트의 각 버스 정보를 로그로 출력
                System.out.println("정류소 ID " + stopId + "에서 받아온 버스 도착 정보:");
                for (BusArrivalDto dto : arrivals) {
                    System.out.printf(
                            "  - 버스번호: %s, 도착시간: %d,",
                            dto.getBusNo(),
                            dto.getArrivalTime()
                    );
                }

                // DB 저장하는 함수로 값을 전달하여 저장하게 함.
                // 이 부분때문에 많이 고엿성
                busArrivalService.saveArrivals(stopId, arrivals);

                System.out.println("정류소 ID " + stopId + "의 버스 " + arrivals.size() + "개 정보 업데이트/생성 완료");
                Thread.sleep(5000);
            } catch (Exception e) {
                System.err.println("정류소 ID " + stopId + " 정보 업데이트/생성 실패: " + e.getMessage());
            }
        }


        System.out.println("정류소 정보 업데이트 완료: " + LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }

    public void stopScheduling() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}