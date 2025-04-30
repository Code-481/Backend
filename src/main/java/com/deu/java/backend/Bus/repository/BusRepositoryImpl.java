package com.deu.java.backend.Bus.repository;

import com.deu.java.backend.Bus.entity.BusEntity;
import jakarta.persistence.EntityManager;
import java.util.List;

public class BusRepositoryImpl implements BusRepository {
    private final EntityManager em;

    public BusRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<BusEntity> findByRouteId(Long routeId) {
        return em.createQuery(
            "SELECT b FROM BusEntity b WHERE b.route.routeId = :routeId", BusEntity.class)
            .setParameter("routeId", routeId)
            .getResultList();
    }
}
