package com.deu.java.backend;

//import com.deu.java.backend.Weather.WeatherController;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;

import com.deu.java.backend.Bus.config.JpaUtil;
import com.deu.java.backend.Bus.controller.BusController;
import com.deu.java.backend.Bus.controller.BusArrivalController;
import com.deu.java.backend.Bus.service.BusArrivalService;
import com.deu.java.backend.Bus.service.BusService;
import com.deu.java.backend.Bus.service.BusServiceImpl;
import com.deu.java.backend.Bus.service.BusArrivalServiceImpl;
import com.deu.java.backend.Bus.repository.BusRepository;
import com.deu.java.backend.Bus.repository.BusRepositoryImpl;
import com.deu.java.backend.Festival.controller.FestivalController;
import com.deu.java.backend.Festival.service.FestivalService;
import com.deu.java.backend.Festival.service.FestivalServiceImpl;
import java.util.Map;

public class Backend {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        //실행 전
        app.before(ctx -> {
            EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
            ctx.attribute("em", em);
        });

        app.after(ctx -> {
            EntityManager em = ctx.attribute("em");
            if (em != null && em.isOpen()) {
                em.close();
            }
        });
        
        //실행 중
        //경로마다의 정보
        app.get("/bus/route/{routeId}", ctx -> {
            EntityManager em = ctx.attribute("em");
            BusRepository repo = new BusRepositoryImpl(em);
            BusService service = new BusServiceImpl(repo);
            BusController controller = new BusController(service);
            controller.handleGetBusInfo(ctx);
        });
        
        //정거장마다의 정보
        app.get("/bus/stop/arrival", ctx -> {
             
            ctx.contentType("application/json; charset=UTF-8");
            String stopId = ctx.queryParam("stopId");
            System.out.println("🛬 stopId: " + stopId); // 여기는 들어오냐?
    
            if (stopId == null || stopId.isBlank()) {
                ctx.status(400).json(Map.of("error", "Missing stopId"));
                System.out.println("🛬 stopId2: " + stopId); // 여기는 들어오냐?

                return;
            }

            BusArrivalService service = new BusArrivalServiceImpl();
            BusArrivalController controller = new BusArrivalController(service);
            controller.handleGetArrivalInfo(ctx);
        });
        
        //행사 정보
        app.get("/festival/info", ctx -> {
            FestivalService service = new FestivalServiceImpl();
            FestivalController controller = new FestivalController(service);
            controller.handleGetFestivalInfo(ctx);
        });
        
        //날씨 정보
//        app.get("/weather", ctx -> {
//            WeatherController controller = new WeatherController();
//            controller::handleWeather}
//        );

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
        Javalin app = createApp();
        app.start(7000);
        Runtime.getRuntime().addShutdownHook(new Thread(JpaUtil::close));
    }
}
