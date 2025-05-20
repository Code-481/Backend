package com.deu.java.backend;

import com.deu.java.backend.dormmeal.controller.DormMealController;
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

public class Backend {

    public static Javalin createApp(
            BusArrivalController arrivalController,
            BusController busController,
            FestivalController festivalController,
            DormMealController dormMealController
    ) {
        Javalin app = Javalin.create();

        // 실행 전
        app.before(ctx -> {
            EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
            ctx.attribute("em", em);
        });

        // 버스 노선 정보
        app.get("/bus/route/{routeId}", busController::handleGetBusInfo);
        // 정류장별 도착 정보 (DB에서 조회)
        app.get("/bus/stop/arrival", arrivalController::handleGetArrivalInfo);
        // 실시간 API 호출 후 DB 저장 (업데이트)
        app.get("/api/bus/update", arrivalController::handleUpdateAndGetArrivalInfo);
        // 축제 정보
        app.get("/festival/info", festivalController::handleGetFestivalInfo);
        // 기숙사 식단 정보
        app.get("/dormmeal", dormMealController::getAllMealData);

        // 실행 후
        app.after(ctx -> {
            EntityManager em = ctx.attribute("em");
            if (em != null && em.isOpen()) {
                em.close();
            }
        });

        // 예외
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).result("알 수 없는 오류가 발생했습니다.");
        });

        return app;
    }

    public static void main(String[] args) {


        // 서비스/컨트롤러 생성
        BusanBimsApiClient apiClient = new BusanBimsApiClient();
        BusArrivalServiceImpl busArrivalService = new BusArrivalServiceImpl(apiClient);
        BusArrivalController busArrivalController = new BusArrivalController(busArrivalService);

        BusServiceFactory busServiceFactory = em -> new BusServiceImpl(new BusRepositoryImpl(em));
        BusController busController = new BusController(busServiceFactory);

        FestivalService festService = new FestivalServiceImpl();
        FestivalController festController = new FestivalController(festService);

        DormMealService dormMealService = new DormMealService();
        DormMealController dormMealController = new DormMealController(dormMealService);

        // Javalin 서버 시작
        Javalin app = createApp(busArrivalController, busController, festController, dormMealController);
        app.start(7000);
        System.out.println("Javalin 서버 시작: http://localhost:7000/");

        // 스케줄러 시작
        BusArrivalScheduler busArrivalScheduler = new BusArrivalScheduler(busArrivalService);
        busArrivalScheduler.startScheduling();

        // 종료 시 리소스 정리
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("서버 종료: 스케줄러 및 JPA 리소스 정리 중...");
            busArrivalScheduler.stopScheduling();
            JpaUtil.close();
            System.out.println("리소스 정리 완료. 서버 종료.");
        }));

        // main이 종료되지 않도록 대기
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
