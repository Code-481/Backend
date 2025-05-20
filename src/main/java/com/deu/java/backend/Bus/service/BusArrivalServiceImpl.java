package com.deu.java.backend.Bus.service;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import com.deu.java.backend.apiClient.BusanBimsApiClient;
import com.deu.java.backend.config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BusArrivalServiceImpl implements BusArrivalService {

    private final BusanBimsApiClient apiClient;

    public BusArrivalServiceImpl(BusanBimsApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void saveArrivals(String bstopid, List<BusArrivalDto> arrivals) {
        System.out.println("정류소 " + bstopid + " arrivals size: " + arrivals.size());
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            for (BusArrivalDto arrival : arrivals) {
                try {
                    Query q = em.createNativeQuery(
                            "INSERT INTO busArraival (\n" +
                                    "  bstopid, bus_no, arrivaltime, all_data, updated_at\n" +
                                    ") VALUES (?, ?, ?, ?, ?)\n" +
                                    "ON DUPLICATE KEY UPDATE\n" +
                                    "  arrival_time = VALUES(arrival_time),\n" +
                                    "  all_data = VALUES(all_data),\n" +
                                    "  updated_at = VALUES(updated_at);"
                    );
                    q.setParameter(1, bstopid);
                    q.setParameter(2, arrival.getBusNo());
                    q.setParameter(3, arrival.getArrivalTime());
                    q.setParameter(4, new JSONObject(arrival.getAllData()).toString());
                    q.setParameter(5, LocalDateTime.now());
                    q.executeUpdate();
                    System.out.printf("DB 저장 성공 - 정류소: %s, 버스번호: %s, 도착시간: %d%n",
                            bstopid, arrival.getBusNo(), arrival.getArrivalTime());
                } catch (Exception e) {
                    System.err.printf("DB 저장 실패 - 정류소: %s, 버스번호: %s, 오류: %s%n",
                            bstopid, arrival.getBusNo(), e.getMessage());
                }
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("JPA 저장 전체 트랜잭션 오류: " + e.getMessage());
        } finally {
            em.close();
        }
    }



    @Override
    public List<BusArrivalDto> getBusArrivalsByRouteIdFromDb(String bstopid) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        List<BusArrivalDto> result = new ArrayList<>();
        try {
            Query q = em.createNativeQuery("SELECT bus_no, arrival_time, all_data FROM busArraival WHERE bstopid = ?");
            q.setParameter(1, bstopid);
            List<?> rows = q.getResultList();
            for (Object rowObj : rows) {
                Object[] row = (Object[]) rowObj;
                String busNo = (String) row[0];
                long arrivalTime = ((Number) row[1]).longValue();
                String allDataStr = (String) row[2];
                Map<String, Object> allData = new JSONObject(allDataStr).toMap();
                result.add(new BusArrivalDto(busNo, arrivalTime, allData));
            }
        } catch (Exception e) {
            System.err.println("JPA 조회 오류: " + e.getMessage());
        } finally {
            em.close();
        }
        return result;
    }


    // 실제 API 호출 구현
    @Override
    public List<BusArrivalDto> getBusArrivalsByStopId(String stopId) {
        // BusanBimsApiClient를 통해 API에서 받아온다
        return apiClient.fetchArrivalInfo(stopId);
    }
}
