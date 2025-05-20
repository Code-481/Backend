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
            Map<String, String> properties = new HashMap<>();

            // ENV
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            // 환경 변수 로그 출력
            String jdbcUrl = dotenv.get("JDBC_URL");
            String jdbcUser = dotenv.get("JDBC_USER");
            String jdbcPassword = dotenv.get("JDBC_PASSWORD");

            System.out.println("JDBC URL: " + jdbcUrl);
            System.out.println("JDBC USER: " + jdbcUser);

            if (jdbcUrl == null || jdbcUser == null || jdbcPassword == null) {
                throw new RuntimeException("필수 데이터베이스 환경 변수가 설정되지 않았습니다.");
            }

            // MYSQL
            properties.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
            properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
            properties.put("jakarta.persistence.jdbc.user", jdbcUser);
            properties.put("jakarta.persistence.jdbc.password", jdbcPassword);

            // SQL
            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.show_sql", "false");
            properties.put("hibernate.format_sql", "false");

            //JTA
            properties.put("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory"); // JDBC 트랜잭션 예시
            properties.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.JBossTransactionManagerLookup");

            return Persistence.createEntityManagerFactory("bus-unit", properties);
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
