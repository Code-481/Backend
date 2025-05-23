package com.deu.java.backend.Weather.service;

import com.deu.java.backend.Weather.DTO.WeatherTodayDTO;
import com.deu.java.backend.Weather.DTO.WeatherWeekDTO;
import com.deu.java.backend.apiClient.WeatherApiClient;
import com.deu.java.backend.Weather.repository.WeatherTodayRepository;
import com.deu.java.backend.Weather.repository.WeatherWeekRepository;
import com.deu.java.backend.entity.WeatherTodayEntity;
import com.deu.java.backend.entity.WeatherWeekEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherServiceImpl implements WeatherService {

    private final WeatherTodayRepository todayRepo;
    private final WeatherWeekRepository weekRepo;
    private final WeatherApiClient apiClient;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public WeatherServiceImpl(WeatherTodayRepository todayRepo, WeatherWeekRepository weekRepo, WeatherApiClient apiClient) {
        this.todayRepo = todayRepo;
        this.weekRepo = weekRepo;
        this.apiClient = apiClient;
        startScheduledUpdate();  // 생성자에서 스케줄러 시작

    }

    private void startScheduledUpdate() {
        Runnable updateTask = () -> {
            try {
                System.out.println("3시간마다 날씨 정보 갱신 실행: " + LocalDateTime.now());

                LocalDate today = LocalDate.now();
                LocalDate endDate = today.plusDays(6);

                // 기존 DB 데이터 삭제
                weekRepo.clearWeekWeather(today, endDate);

                // API에서 DTO 리스트 가져오기
                List<WeatherWeekDTO> apiDTOs = apiClient.fetchWeekWeather();

                // DTO -> Entity 변환
                List<WeatherWeekEntity> apiData = convertDtoToEntity(apiDTOs);

                // DB 저장
                weekRepo.saveAll(apiData);

                System.out.println("날씨 정보 갱신 완료: " + LocalDateTime.now());
            } catch (Exception e) {
                System.err.println("날씨 정보 갱신 실패: " + e.getMessage());
                e.printStackTrace();
            }
        };

        // 0초 후 즉시 시작, 이후 3시간마다 실행
        scheduler.scheduleAtFixedRate(updateTask, 0, 3, TimeUnit.HOURS);
    }

    private List<WeatherWeekDTO> convertToWeekDTO(List<WeatherWeekEntity> entities) {
        return entities.stream()
                .map(e -> new WeatherWeekDTO(e.getDate(), e.getMinTemperature(), e.getMaxTemperature(), e.getSky(), e.getCloud()))
                .toList();
    }

    private List<WeatherWeekEntity> convertDtoToEntity(List<WeatherWeekDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> new WeatherWeekEntity(
                        LocalDate.parse(dto.getDate()),
                        dto.getMinTemperature(),
                        dto.getMaxTemperature(),
                        dto.getSky(),
                        dto.getCloud()
                ))
                .toList();
    }

    @Override
    public WeatherTodayDTO getTodayWeather() {
        LocalDate today = LocalDate.now();

        // DB에 데이터 있으면 반환
        WeatherTodayEntity cached= todayRepo.findLatestAnnounceTimeToday(today);
        if (cached!=null) {
            return new WeatherTodayDTO(cached.getDate(), cached.getTemperature(), cached.getSky(), cached.getCloud());
        }

        // 없으면 외부 API 를 호출
        WeatherTodayEntity apiData = apiClient.fetchTodayWeather();
        WeatherTodayEntity entity = new WeatherTodayEntity(
                apiData.getDate(), apiData.getTemperature(), apiData.getSky(), apiData.getCloud()
        );

        //기존 데이터 삭제 및 새 데이터 저장
        todayRepo.clearTodayWeather();
        todayRepo.save(entity);

        return new WeatherTodayDTO(entity.getDate(), entity.getTemperature(), entity.getSky(), entity.getCloud());

    }

    @Override
    public List<WeatherWeekDTO> getWeekWeather() {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(6);

        List<WeatherWeekEntity> cached = weekRepo.findWeekWeather(today, endDate);
        System.out.println("cached.size(): " + cached.size());
        if (!cached.isEmpty()) {
            return convertToWeekDTO(cached);
        }

        List<WeatherWeekDTO> apiDTOs = apiClient.fetchWeekWeather();
        List<WeatherWeekEntity> entities = convertDtoToEntity(apiDTOs);
        System.out.println("converted DTOs: " + apiDTOs.size());
        weekRepo.clearWeekWeather(today, endDate);
        weekRepo.saveAll(entities);
        return apiDTOs;
    }
}