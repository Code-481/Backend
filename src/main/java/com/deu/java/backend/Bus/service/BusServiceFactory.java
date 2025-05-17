package com.deu.java.backend.Bus.service;

import jakarta.persistence.EntityManager;

public interface BusServiceFactory {
    BusService create(EntityManager em);
}
