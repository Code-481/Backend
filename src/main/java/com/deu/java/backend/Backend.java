package com.deu.java.backend;

//import com.deu.java.backend.Weather.WeatherController;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;

import com.deu.java.backend.Bus.config.JpaUtil;
import com.deu.java.backend.Bus.controller.BusController;
import com.deu.java.backend.Bus.controller.BusArrivalController;
import com.deu.java.backend.Bus.service.BusArrivalService;
import com.deu.java.backend.Bus.service.BusServiceImpl;
import com.deu.java.backend.Bus.service.BusArrivalServiceImpl;
import com.deu.java.backend.Bus.repository.BusRepositoryImpl;
import com.deu.java.backend.Bus.service.BusServiceFactory;
import com.deu.java.backend.Festival.controller.FestivalController;
import com.deu.java.backend.Festival.service.FestivalService;
import com.deu.java.backend.Festival.service.FestivalServiceImpl;
import com.deu.java.backend.apiClient.BusanBimsApiClient;

public class Backend {

    public static Javalin createApp(
            BusArrivalController arrivalController,
            BusController busController,
            FestivalController festivalController
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
        //날씨 정보
        //app.get("/weather", controller::handleWeather);
        
        //실행 후
        app.after(ctx -> {
            EntityManager em = ctx.attribute("em");
            if (em != null && em.isOpen()) {
                em.close();
            }
        });

        //예외
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).result("알 수 없는 오류가 발생했습니다.");
        });

        return app;
    }

    public static void main(String[] args) {
        // 버스 노선별 정보 : DB -> service 
        BusServiceFactory busServiceFactory = em -> new BusServiceImpl(new BusRepositoryImpl(em));
        BusController busController = new BusController(busServiceFactory);

        // BIMS 정거장별 정보: API -> service
        BusanBimsApiClient bimsApiClient = new BusanBimsApiClient();
        BusArrivalService arrivalService = new BusArrivalServiceImpl(bimsApiClient);
        BusArrivalController arrivalController = new BusArrivalController(arrivalService);

        // 축제 정보: csv -> service
        FestivalService festService = new FestivalServiceImpl();
        FestivalController festController = new FestivalController(festService);

        Javalin app = createApp(arrivalController, busController, festController);
        app.start(7000);

        Runtime.getRuntime().addShutdownHook(new Thread(JpaUtil::close));
    } 
}
