package com.deu.java.backend;

import io.javalin.Javalin;

import com.deu.java.backend.Bus.controller.BusController;
import com.deu.java.backend.Bus.repository.BusRepository;
import com.deu.java.backend.Bus.service.BusService;
import com.deu.java.backend.Bus.config.JpaUtil;
import com.deu.java.backend.Bus.repository.BusRepositoryImpl;
import com.deu.java.backend.Bus.service.BusServiceImpl;

import jakarta.persistence.EntityManager;

public class Backend {
    public static Javalin createApp() {
        Javalin app = Javalin.create();

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

        app.get("/bus", ctx -> {
            EntityManager em = ctx.attribute("em");
            BusRepository repo = new BusRepositoryImpl(em);
            BusService service = new BusServiceImpl(repo);
            BusController controller = new BusController(service);
            controller.handleGetBusInfo(ctx);
        });

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace(); // 로그 기록
            ctx.status(500).result("알 수 없는 오류가 발생했습니다.");
        });

        return app;
    }
    
    public static void main(String[] args) {
        Javalin app = createApp();
        app.start(7070);
        Runtime.getRuntime().addShutdownHook(new Thread(JpaUtil::close));
    }
}
