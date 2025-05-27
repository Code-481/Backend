package com.deu.java.backend;

import com.deu.java.backend.Weather.WeatherController;
import com.deu.java.backend.Weather.repository.WeatherTodayRepository;
import com.deu.java.backend.Weather.repository.WeatherTodayRepositoryImpl;
import com.deu.java.backend.Weather.repository.WeatherWeekRepository;
import com.deu.java.backend.Weather.repository.WeatherWeekRepositoryImpl;
import com.deu.java.backend.Weather.service.WeatherService;
import com.deu.java.backend.Weather.service.WeatherServiceImpl;
import com.deu.java.backend.apiClient.WeatherApiClient;
import com.deu.java.backend.dormmeal.controller.DormMealController;
import com.deu.java.backend.dormmeal.scheduler.MealDataScheduler;
import com.deu.java.backend.dormmeal.service.DormMealService;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;

import com.deu.java.backend.config.JpaUtil;
import com.deu.java.backend.Bus.controller.BusController;
import com.deu.java.backend.Bus.controller.BusArrivalController;
import com.deu.java.backend.Bus.service.BusServiceImpl;
import com.deu.java.backend.Bus.service.BusArrivalServiceImpl;
import com.deu.java.backend.Bus.repository.BusRepositoryImpl;
import com.deu.java.backend.Bus.scheduler.BusArrivalScheduler;
import com.deu.java.backend.Bus.service.BusServiceFactory;
import com.deu.java.backend.Festival.controller.FestivalController;
import com.deu.java.backend.Festival.service.FestivalService;
import com.deu.java.backend.Festival.service.FestivalServiceImpl;
import com.deu.java.backend.apiClient.BusanBimsApiClient;
import jakarta.persistence.EntityManagerFactory;

public class Backend {

    public static Javalin createApp(
            BusArrivalController arrivalController,
            BusController busController,
            FestivalController festivalController,
            DormMealController dormMealController,
            WeatherController weatherController
    ) {
        Javalin app = Javalin.create();

        // 실행 전
        app.before(ctx -> {
            EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
            ctx.attribute("em", em);
        });

        // 버스 노선 정보
        app.get("/api/v1/bus/route/{routeId}", busController::handleGetBusInfo);
        // 정류장별 도착 정보 (DB에서 조회)
        app.get("/api/v1/bus/stop/arrival", arrivalController::handleGetArrivalInfo);
        // 축제 정보
        app.get("/api/v1/festival/info", festivalController::handleGetFestivalInfo);
        //학식 정보 파라미터 place
        app.get("/api/v1/univ/foods", dormMealController::handleGetDormMealInfo);
        // 오늘 날씨 정보
        app.get("/api/v1/weather/today", weatherController::handleTodayWeather);
        // 주간 날씨 정보
        app.get("/api/v1/weather/week", weatherController::handleWeekWeather);

        // 실행 후
        app.after(ctx -> {
            EntityManager em = ctx.attribute("em");
            if (em != null && em.isOpen()) {
                em.close();
            }
        });
        return app;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();


        // 버스 실시간 서비스
        BusanBimsApiClient apiClient = new BusanBimsApiClient();
        BusArrivalServiceImpl busArrivalService = new BusArrivalServiceImpl(apiClient);
        BusArrivalController busArrivalController = new BusArrivalController(busArrivalService);

        BusServiceFactory busServiceFactory = em -> new BusServiceImpl(new BusRepositoryImpl(em));
        BusController busController = new BusController(busServiceFactory);

        // 행사 실시간 부분
        FestivalService festService = new FestivalServiceImpl();
        FestivalController festController = new FestivalController(festService);

        // 우리가 좋아하는 학식 기숙사 파싱하는 부분
        DormMealService dormMealService = new DormMealService();
        DormMealController dormMealController = new DormMealController(dormMealService);

        // 날씨 정보: API -> DB -> service
        WeatherTodayRepository todayRepo = new WeatherTodayRepositoryImpl(emf.createEntityManager());
        WeatherWeekRepository weekRepo = new WeatherWeekRepositoryImpl(emf.createEntityManager());
        WeatherApiClient Weather_apiClient = new WeatherApiClient() {}; // 너가 만든 API client
        WeatherService weatherService = new WeatherServiceImpl(todayRepo, weekRepo, Weather_apiClient);
        WeatherController weatherController = new WeatherController(weatherService);


        // Javalin 서버 시작
        Javalin app = createApp(busArrivalController, busController, festController, dormMealController, weatherController);
        app.start(7000);

        // 예최 처리
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace(); // 콘솔에 예외 로그
            ctx.status(500).json(new Error("알 수 없는 오류가 발생했습니다."));
        });


        // 스케줄러 시작
        BusArrivalScheduler busArrivalScheduler = new BusArrivalScheduler(busArrivalService);
        busArrivalScheduler.startScheduling();
        MealDataScheduler mealDataScheduler = new MealDataScheduler(dormMealService);
        //mealDataScheduler.startMidnightScheduler();

        // 종료 시 리소스 정리
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("서버 종료: 스케줄러 및 Javalin 리소스 정리 중...");
            busArrivalScheduler.stopScheduling();
            JpaUtil.close();
            System.out.println("리소스 정리 완료. 서버 종료.");
            System.exit(0);
        }));
    }
}
