package com.deu.java.backend;

import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import com.deu.java.backend.config.JpaUtil;
import com.deu.java.backend.Bus.controller.BusController;
import com.deu.java.backend.Bus.controller.BusArrivalController;
import com.deu.java.backend.Bus.service.BusArrivalService;
import com.deu.java.backend.Bus.service.BusServiceImpl;
import com.deu.java.backend.Bus.service.BusArrivalServiceImpl;
import com.deu.java.backend.Bus.repository.BusRepositoryImpl;
import com.deu.java.backend.Bus.service.BusService;
import com.deu.java.backend.Festival.controller.FestivalController;
import com.deu.java.backend.Festival.service.FestivalService;
import com.deu.java.backend.Festival.service.FestivalServiceImpl;
import com.deu.java.backend.Weather.WeatherController;
import com.deu.java.backend.Weather.repository.WeatherTodayRepository;
import com.deu.java.backend.Weather.repository.WeatherTodayRepositoryImpl;
import com.deu.java.backend.Weather.repository.WeatherWeekRepository;
import com.deu.java.backend.Weather.repository.WeatherWeekRepositoryImpl;
import com.deu.java.backend.Weather.service.WeatherService;
import com.deu.java.backend.Weather.service.WeatherServiceImpl;
import com.deu.java.backend.apiClient.BusanBimsApiClient;
import com.deu.java.backend.apiClient.WeatherApiClient;
import jakarta.persistence.EntityManagerFactory;

public class Backend {

    public static Javalin createApp(
            BusArrivalController arrivalController,
            BusController busController,
            FestivalController festivalController,            
            WeatherController weatherController
    ) {
        Javalin app = Javalin.create();

        //실행 전
        app.before(ctx -> {
            EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
            ctx.attribute("em", em);
        });

        // 버스 노선 정보
        app.get("/bus/route/{routeId}", busController::handleGetBusInfo);
        // 정류장별 도착 정보
        app.get("/bus/stop/arrival", arrivalController::handleGetArrivalInfo);
        // 축제 정보
        app.get("/festival/info", festivalController::handleGetFestivalInfo);
        // 오늘 날씨 정보
        app.get("/weather/today", weatherController::handleTodayWeather);
        // 주간 날씨 정보
        app.get("/weather/week", weatherController::handleWeekWeather);

        //실행 후
        app.after(ctx -> {
            EntityManager em = ctx.attribute("em");
            if (em != null && em.isOpen()) {
                em.close();
            }
        });

//        //예외
//        app.exception(Exception.class, (e, ctx) -> {
//            ctx.status(500).result("알 수 없는 오류가 발생했습니다.");
//        });

        return app;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        
        BusService busService = new BusServiceImpl(new BusRepositoryImpl(emf.createEntityManager()));
        BusController busController = new BusController(busService);

        // BIMS 정거장별 정보: API -> service
        BusanBimsApiClient bimsApiClient = new BusanBimsApiClient();
        BusArrivalService arrivalService = new BusArrivalServiceImpl(bimsApiClient);
        BusArrivalController arrivalController = new BusArrivalController(arrivalService);

        // 축제 정보: csv -> service
        FestivalService festService = new FestivalServiceImpl();
        FestivalController festController = new FestivalController(festService);

        // 날씨 정보: API -> DB -> service        
        WeatherTodayRepository todayRepo = new WeatherTodayRepositoryImpl(emf.createEntityManager());
        WeatherWeekRepository weekRepo = new WeatherWeekRepositoryImpl(emf.createEntityManager());
        WeatherApiClient apiClient = new WeatherApiClient() {}; // 너가 만든 API client
        WeatherService weatherService = new WeatherServiceImpl(todayRepo, weekRepo, apiClient);
        WeatherController weatherController = new WeatherController(weatherService);
        Javalin app = createApp(arrivalController, busController, festController, weatherController);
        app.start(7000);

        Runtime.getRuntime().addShutdownHook(new Thread(JpaUtil::close));
    }
}
