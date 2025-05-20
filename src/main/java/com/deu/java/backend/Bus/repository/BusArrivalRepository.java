package com.deu.java.backend.Bus.repository;

import com.deu.java.backend.entity.BusArrival;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class BusArrivalRepository {

    private final EntityManager entityManager;

    public BusArrivalRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(BusArrival busArrival) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            
            // 기존 데이터가 있는지 확인
            BusArrival existingBusArrival = entityManager.find(BusArrival.class, busArrival.getId());
            
            if (existingBusArrival != null) {
                // 기존 데이터가 있으면 업데이트
                existingBusArrival.setArrivalTime(busArrival.getArrivalTime());
                
                // 운행 중인 경우에만 다른 필드도 업데이트
                if (busArrival.getArrivalTime() > 0) {
                    existingBusArrival.setLineid(busArrival.getLineid());
                    existingBusArrival.setLineno(busArrival.getLineno());
                    existingBusArrival.setNodenm(busArrival.getNodenm());
                    existingBusArrival.setGpsx(busArrival.getGpsx());
                    existingBusArrival.setGpsy(busArrival.getGpsy());
                    existingBusArrival.setCarno1(busArrival.getCarno1());
                    existingBusArrival.setMin1(busArrival.getMin1());
                    existingBusArrival.setStation1(busArrival.getStation1());
                    existingBusArrival.setLowplate1(busArrival.getLowplate1());
                    existingBusArrival.setSeat1(busArrival.getSeat1());
                    existingBusArrival.setCarno2(busArrival.getCarno2());
                    existingBusArrival.setMin2(busArrival.getMin2());
                    existingBusArrival.setStation2(busArrival.getStation2());
                    existingBusArrival.setBustype(busArrival.getBustype());
                    existingBusArrival.setBstopidx(busArrival.getBstopidx());
                    existingBusArrival.setArsno(busArrival.getArsno());
                    existingBusArrival.setIsReverse(busArrival.getIsReverse());
                }
                
                entityManager.merge(existingBusArrival);
            } else {
                // 새 데이터 저장
                entityManager.persist(busArrival);
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Optional<BusArrival> findById(Long id) {
        BusArrival busArrival = entityManager.find(BusArrival.class, id);
        return Optional.ofNullable(busArrival);
    }
    
    // 노선 ID로 버스 도착 정보 조회
    public List<BusArrival> findByRouteId(Integer routeId) {
        TypedQuery<BusArrival> query = entityManager.createQuery(
                "SELECT b FROM BusArrival b WHERE b.lineid = :routeId", 
                BusArrival.class);
        query.setParameter("routeId", routeId.toString());
        return query.getResultList();
    }
    
    // 버스 번호로 버스 도착 정보 조회
    public List<BusArrival> findByRouteNo(String routeNo) {
        TypedQuery<BusArrival> query = entityManager.createQuery(
                "SELECT b FROM BusArrival b WHERE b.lineno = :routeNo", 
                BusArrival.class);
        query.setParameter("routeNo", routeNo);
        return query.getResultList();
    }
    
    // 정류소 ID로 버스 도착 정보 조회
    public List<BusArrival> findByStopId(Long stopId) {
        TypedQuery<BusArrival> query = entityManager.createQuery(
                "SELECT b FROM BusArrival b WHERE b.id = :stopId", 
                BusArrival.class);
        query.setParameter("stopId", stopId);
        return query.getResultList();
    }
}
