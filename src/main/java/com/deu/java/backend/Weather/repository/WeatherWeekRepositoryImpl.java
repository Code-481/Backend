package com.deu.java.backend.Weather.repository;

import com.deu.java.backend.Weather.entity.WeatherWeekEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;

public class WeatherWeekRepositoryImpl implements WeatherWeekRepository {
    private final EntityManager em;

    public WeatherWeekRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<WeatherWeekEntity> findWeekWeather(LocalDate startDate, LocalDate endDate) {
        return em.createQuery("SELECT w FROM WeatherWeekEntity w WHERE w.date BETWEEN :start AND :end", WeatherWeekEntity.class)
                 .setParameter("start", startDate)
                 .setParameter("end", endDate)
                 .getResultList();
    }

    @Override
    public void clearWeekWeather(LocalDate startDate, LocalDate endDate) {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM WeatherWeekEntity w WHERE w.date BETWEEN :start AND :end")
          .setParameter("start", startDate)
          .setParameter("end", endDate)
          .executeUpdate();
        em.getTransaction().commit();
    }

    @Override
    public void saveAll(List<WeatherWeekEntity> entities) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (WeatherWeekEntity entity : entities) {
                em.persist(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}
