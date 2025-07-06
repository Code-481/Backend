package com.deu.java.backend.Bus.service;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import com.deu.java.backend.apiClient.BusanBimsApiClient;
import com.deu.java.backend.config.JpaUtil;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class BusArrivalServiceImpl implements BusArrivalService {

    private final BusanBimsApiClient apiClient;
    private final ExecutorService executor;
    private final String pythonApiUrl;

    public BusArrivalServiceImpl(BusanBimsApiClient apiClient, ExecutorService executor) {
        this.apiClient = apiClient;
        this.executor = executor; // 3. 전달받은 인스턴스를 멤버 변수에 할당.

        Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
        this.pythonApiUrl = dotenv.get("PYTHON_AI_SERVER_URL");
        if (this.pythonApiUrl == null) {
            throw new IllegalStateException("PYTHON_AI_SERVER_URL is not set in .env file.");
        }
    }

    public List<BusArrivalDto> getBusArrivalInfo(String bstopid) {
        // apiClient를 통해 버스 도착 정보를 가져옴
        List<BusArrivalDto> arrivalInfo = apiClient.fetchArrivalInfo(bstopid);

        // API 조회가 성공적으로 끝나면 (예외가 발생하지 않으면),
        // 백그라운드에서 파이썬 서버에 학습 데이터 수집을 요청합니다.
        if (arrivalInfo != null) {
            triggerPythonLearning(bstopid);
        }

        return arrivalInfo;
    }

    public void triggerPythonLearning(String stationId) {
        // 4. 초기화된 멤버 변수 'executor'를 사용하여 비동기 작업 제출
        executor.submit(() -> {
            System.out.println("AI 서버에 학습 데이터 수집 요청 시작 (정류소 ID: " + stationId + ")");
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> requestBody = Collections.singletonMap("station_id", stationId);
                HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

                String url = pythonApiUrl + "/collect_and_learn";
                ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("AI 서버에 요청 성공: " + response.getBody());
                } else {
                    System.err.println("AI 서버에 요청 실패: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.err.println("AI 서버 호출 중 예외 발생: " + e.getMessage());
            }
        });
    }
    @Override
    public void saveArrivals(String bstopid, List<BusArrivalDto> arrivals) {
        System.out.println("정류소 " + bstopid + " arrivals size: " + arrivals.size());
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            for (BusArrivalDto arrival : arrivals) {
                try {
                    // 예...뭐 SQL 인젝션 방지할려고 이따기로 김니다 ㅎㅎ...
                    // JSON 데이터 준비
                    Map<String, Object> allData = arrival.getAllData();
                    String allDataJson = new JSONObject(allData).toString();

                    // 개별 필드 추출
                    String arsno = getStringValue(allData, "arsno");
                    Integer bstopidx = getIntegerValue(allData, "bstopidx");
                    String bustype = getStringValue(allData, "bustype");
                    String carno1 = getStringValue(allData, "carno1");
                    String carno2 = getStringValue(allData, "carno2");
                    String gpsx = getStringValue(allData, "gpsx");
                    String gpsy = getStringValue(allData, "gpsy");
                    Boolean isReverse = getBooleanValue(allData, "isReverse");
                    String lineid = getStringValue(allData, "lineid");
                    String lineno = getStringValue(allData, "lineno");
                    Integer lowplate1 = getIntegerValue(allData, "lowplate1");
                    String min1 = getStringValue(allData, "min1");
                    String min2 = getStringValue(allData, "min2");
                    String nodenm = getStringValue(allData, "nodenm");
                    Integer seat1 = getIntegerValue(allData, "seat1");
                    Integer station1 = getIntegerValue(allData, "station1");
                    Integer station2 = getIntegerValue(allData, "station2");

                    // SQL 쿼리 생성 - 모든 필드를 포함
                    Query q = em.createNativeQuery(
                            "INSERT INTO busArraival (bstopid, bus_no, arrival_time, all_data, updated_at, " +
                                    "arrivalTime, arsno, bstopidx, bustype, carno1, carno2, gpsx, gpsy, isReverse, " +
                                    "lineid, lineno, lowplate1, min1, min2, nodenm, seat1, station1, station2) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE " +
                                    "arrival_time=VALUES(arrival_time), all_data=VALUES(all_data), updated_at=VALUES(updated_at), " +
                                    "arrivalTime=VALUES(arrivalTime), arsno=VALUES(arsno), bstopidx=VALUES(bstopidx), " +
                                    "bustype=VALUES(bustype), carno1=VALUES(carno1), carno2=VALUES(carno2), " +
                                    "gpsx=VALUES(gpsx), gpsy=VALUES(gpsy), isReverse=VALUES(isReverse), " +
                                    "lineid=VALUES(lineid), lineno=VALUES(lineno), lowplate1=VALUES(lowplate1), " +
                                    "min1=VALUES(min1), min2=VALUES(min2), nodenm=VALUES(nodenm), " +
                                    "seat1=VALUES(seat1), station1=VALUES(station1), station2=VALUES(station2)"
                    );

                    // 파라미터 설정
                    q.setParameter(1, bstopid);
                    q.setParameter(2, arrival.getBusNo());
                    q.setParameter(3, arrival.getArrivalTime());
                    q.setParameter(4, allDataJson);
                    q.setParameter(5, LocalDateTime.now());
                    q.setParameter(6, arrival.getArrivalTime()); // arrivalTime은 arrival_time과 동일하게 설정
                    q.setParameter(7, arsno);
                    q.setParameter(8, bstopidx);
                    q.setParameter(9, bustype);
                    q.setParameter(10, carno1);
                    q.setParameter(11, carno2);
                    q.setParameter(12, gpsx);
                    q.setParameter(13, gpsy);
                    q.setParameter(14, isReverse != null ? (isReverse ? 1 : 0) : null);
                    q.setParameter(15, lineid);
                    q.setParameter(16, lineno);
                    q.setParameter(17, lowplate1);
                    q.setParameter(18, min1);
                    q.setParameter(19, min2);
                    q.setParameter(20, nodenm);
                    q.setParameter(21, seat1);
                    q.setParameter(22, station1);
                    q.setParameter(23, station2);

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

    // 헬퍼 메서드들 - allData에서 값을 안전하게 추출
    private String getStringValue(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getIntegerValue(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        if (value == null) return null;
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String strValue = value.toString().toLowerCase();
        return "true".equals(strValue) || "1".equals(strValue);
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

    public List<BusArrivalDto> getAllBusArrivalsFromDb() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        List<BusArrivalDto> result = new ArrayList<>();
        try {
            Query q = em.createNativeQuery("SELECT bstopid, bus_no, arrival_time, all_data FROM busArraival");
            List<?> rows = q.getResultList();
            for (Object rowObj : rows) {
                Object[] row = (Object[]) rowObj;
                String stopId = (String) row[0];
                String busNo = (String) row[1];
                long arrivalTime = ((Number) row[2]).longValue();
                String allDataStr = (String) row[3];
                Map<String, Object> allData = new org.json.JSONObject(allDataStr).toMap();
                result.add(new BusArrivalDto(busNo, arrivalTime, allData));
            }
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
