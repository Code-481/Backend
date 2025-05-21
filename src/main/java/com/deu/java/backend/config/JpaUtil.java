package com.deu.java.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JpaUtil {

    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            // ENV
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            // 환경 변수 로그 출력
            String jdbcUrl = dotenv.get("JDBC_URL");
            String jdbcUser = dotenv.get("JDBC_USER");
            String jdbcPassword = dotenv.get("JDBC_PASSWORD");

           

            if (jdbcUrl == null || jdbcUser == null || jdbcPassword == null) {
                throw new RuntimeException("필수 데이터베이스 환경 변수가 설정되지 않았습니다.");
            }
            Map<String, String> properties = new HashMap<>();
            
            properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
            properties.put("jakarta.persistence.jdbc.user", jdbcUser);
            properties.put("jakarta.persistence.jdbc.password", jdbcPassword);

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("418-unit", properties);

            return emf;
        } catch (Exception e) {
            System.err.println("EntityManagerFactory 초기화 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("EntityManagerFactory 초기화 실패", e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
