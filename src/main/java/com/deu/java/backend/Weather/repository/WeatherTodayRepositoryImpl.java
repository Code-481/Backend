package com.deu.java.backend.Weather.repository;


import com.deu.java.backend.entity.WeatherTodayEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class WeatherTodayRepositoryImpl implements WeatherTodayRepository {
    private final EntityManager em;

    public WeatherTodayRepositoryImpl(EntityManager em) {
        this.em = em;
    }


    @Override
    public WeatherTodayEntity findLatestAnnounceTimeToday(LocalDate today) {
        LocalDateTime start = today.atStartOfDay();           // 오늘 00시
        LocalDateTime end = start.plusDays(1);                // 내일 00시
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String startFormatted = start.format(formatter); // start가 LocalDateTime이라면
        String endFormatted = end.format(formatter); // start가 LocalDateTime이라면

        List<WeatherTodayEntity> list = em.createQuery(
                        "SELECT w FROM WeatherTodayEntity w " +
                                "WHERE w.date >= :start AND w.date < :end " +
                                "ORDER BY w.date DESC", WeatherTodayEntity.class)
                .setParameter("start", startFormatted)
                .setParameter("end", endFormatted)
                .setMaxResults(1)
                .getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void clearTodayWeather() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM WeatherTodayEntity").executeUpdate();
        em.getTransaction().commit();
    }

    @Override
    public void save(WeatherTodayEntity entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

}