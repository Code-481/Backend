package com.deu.java.backend.Weather.service;

import com.deu.java.backend.Weather.DTO.WeatherTodayDTO;
import com.deu.java.backend.Weather.DTO.WeatherWeekDTO;
import com.deu.java.backend.Weather.entity.WeatherWeekEntity;
import com.deu.java.backend.apiClient.WeatherApiClient;
import com.deu.java.backend.Weather.repository.WeatherTodayRepository;
import com.deu.java.backend.Weather.repository.WeatherWeekRepository;

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
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public WeatherServiceImpl(WeatherTodayRepository todayRepo, WeatherWeekRepository weekRepo, WeatherApiClient apiClient) {
        this.todayRepo = todayRepo;
        this.weekRepo = weekRepo;
        this.apiClient = apiClient;
        startTodayWeatherUpdate();
        startWeekWeatherUpdate();
    }

    // 1시간마다 오늘 날씨 갱신
    private void startTodayWeatherUpdate() {
        Runnable updateTask = () -> {
            try {
                System.out.println("1시간마다 오늘 날씨 정보 갱신 실행: " + LocalDateTime.now());
                WeatherWeekEntity.WeatherTodayEntity apiData = apiClient.fetchTodayWeather();
                todayRepo.clearTodayWeather();
                todayRepo.save(apiData);
                System.out.println("오늘 날씨 정보 갱신 완료: " + LocalDateTime.now());
            } catch (Exception e) {
                System.err.println("오늘 날씨 정보 갱신 실패: " + e.getMessage());
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(updateTask, 0, 1, TimeUnit.HOURS);
    }

    // 3시간마다 주간 날씨 갱신
    private void startWeekWeatherUpdate() {
        Runnable updateTask = () -> {
            try {
                System.out.println("3시간마다 주간 날씨 정보 갱신 실행: " + LocalDateTime.now());
                LocalDate today = LocalDate.now();
                LocalDate endDate = today.plusDays(6);
                weekRepo.clearWeekWeather(today, endDate);
                List<WeatherWeekDTO> apiDTOs = apiClient.fetchWeekWeather();
                List<com.deu.java.backend.Weather.entity.WeatherTodayEntity.WeatherWeekEntity> apiData = convertDtoToEntity(apiDTOs);
                weekRepo.saveAll(apiData);
                System.out.println("주간 날씨 정보 갱신 완료: " + LocalDateTime.now());
            } catch (Exception e) {
                System.err.println("주간 날씨 정보 갱신 실패: " + e.getMessage());
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(updateTask, 0, 3, TimeUnit.HOURS);
    }

    private List<WeatherWeekDTO> convertToWeekDTO(List<com.deu.java.backend.Weather.entity.WeatherTodayEntity.WeatherWeekEntity> entities) {
        return entities.stream()
                .map(e -> new WeatherWeekDTO(e.getDate(), e.getMinTemperature(), e.getMaxTemperature(), e.getSky(), e.getCloud()))
                .toList();
    }

    private List<com.deu.java.backend.Weather.entity.WeatherTodayEntity.WeatherWeekEntity> convertDtoToEntity(List<WeatherWeekDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> new com.deu.java.backend.Weather.entity.WeatherTodayEntity.WeatherWeekEntity(
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
        WeatherWeekEntity.WeatherTodayEntity cached = todayRepo.findLatestAnnounceTimeToday(today);
        if (cached != null) {
            return new WeatherTodayDTO(cached.getDate(), cached.getTemperature(), cached.getSky(), cached.getCloud());
        }
        WeatherWeekEntity.WeatherTodayEntity apiData = apiClient.fetchTodayWeather();
        todayRepo.clearTodayWeather();
        todayRepo.save(apiData);
        return new WeatherTodayDTO(apiData.getDate(), apiData.getTemperature(), apiData.getSky(), apiData.getCloud());
    }

    @Override
    public List<WeatherWeekDTO> getWeekWeather() {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(6);
        List<com.deu.java.backend.Weather.entity.WeatherTodayEntity.WeatherWeekEntity> cached = weekRepo.findWeekWeather(today, endDate);
        if (!cached.isEmpty()) {
            return convertToWeekDTO(cached);
        }
        List<WeatherWeekDTO> apiDTOs = apiClient.fetchWeekWeather();
        List<com.deu.java.backend.Weather.entity.WeatherTodayEntity.WeatherWeekEntity> entities = convertDtoToEntity(apiDTOs);
        weekRepo.clearWeekWeather(today, endDate);
        weekRepo.saveAll(entities);
        return apiDTOs;
    }
}
