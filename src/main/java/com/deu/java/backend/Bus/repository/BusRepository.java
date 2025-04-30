package com.deu.java.backend.Bus.repository;

import com.deu.java.backend.Bus.entity.BusEntity;
import java.util.List;

public interface BusRepository {
    List<BusEntity> findByRouteId(Long routeId);
}