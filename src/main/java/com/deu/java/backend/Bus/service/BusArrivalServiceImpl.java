package com.deu.java.backend.Bus.service;

import com.deu.java.backend.Bus.config.JpaUtil;
import com.deu.java.backend.Bus.dto.BusArrivalDto;
import com.deu.java.backend.Bus.entity.BusArrival;
import com.deu.java.backend.Bus.repository.BusArrivalRepository;
import com.deu.java.backend.apiClient.BusanBimsApiClient;

import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusArrivalServiceImpl implements BusArrivalService {

    private final BusanBimsApiClient bimsApiClient;
    private final BusArrivalRepository busArrivalRepository;
    private final EntityManager entityManager;

    public BusArrivalServiceImpl(BusanBimsApiClient bimsApiClient) {
        this.bimsApiClient = bimsApiClient;
        this.entityManager = JpaUtil.getEntityManagerFactory().createEntityManager();
        this.busArrivalRepository = new BusArrivalRepository(entityManager);
    }

    @Override
    public List<BusArrivalDto> getBusArrivalsByStopId(String bstopid) {
        List<BusArrivalDto> busArrivals = new ArrayList<>();
        
        try {
            List<BusArrivalDto> arrivalDto = bimsApiClient.fetchArrivalInfo(bstopid);
            busArrivals.addAll(arrivalDto);
            
            // DB에 저장 또는 업데이트
            saveOrUpdateBusArrivals(busArrivals);

        } catch (RuntimeException e) {
            System.err.println("버스 도착 정보 호출 실패: " + e.getMessage()); 
            busArrivals.clear();  // 실패 시 빈 리스트를 반환
        }
        return busArrivals;
    }
    
    private void saveOrUpdateBusArrivals(List<BusArrivalDto> busArrivals) {
        for (BusArrivalDto dto : busArrivals) {
            Map<String, Object> allData = dto.getAllData();
            
            // 필요한 데이터 추출
            Long id = null;
            if (allData != null && allData.containsKey("bstopid")) {
                try {
                    id = Long.valueOf(allData.get("bstopid").toString());
                } catch (NumberFormatException e) {
                    System.err.println("bstopid 변환 오류: " + e.getMessage());
                    continue;
                }
            }
            
            if (id == null) {
                System.err.println("버스 도착 정보 저장 실패: ID가 없습니다.");
                continue;
            }
            
            // 운행 상태 확인
            boolean isOperating = dto.isOperating();
            
            // 데이터 준비
            Long arrivalTime = isOperating ? 1L : 0L;
            
            // 필요한 데이터 추출
            String lineid = (allData != null && allData.containsKey("lineid")) ? allData.get("lineid").toString() : null;
            String lineno = dto.getBusNo();
            String nodenm = (allData != null && allData.containsKey("nodenm")) ? allData.get("nodenm").toString() : null;
            String gpsx = (allData != null && allData.containsKey("gpsx")) ? allData.get("gpsx").toString() : null;
            String gpsy = (allData != null && allData.containsKey("gpsy")) ? allData.get("gpsy").toString() : null;
            String carno1 = (allData != null && allData.containsKey("carno1")) ? allData.get("carno1").toString() : null;
            String min1 = (allData != null && allData.containsKey("min1")) ? allData.get("min1").toString() : null;
            
            Integer station1 = null;
            if (allData != null && allData.containsKey("station1")) {
                try {
                    station1 = Integer.valueOf(allData.get("station1").toString());
                } catch (NumberFormatException e) {
                    System.err.println("station1 변환 오류: " + e.getMessage());
                }
            }
            
            Integer lowplate1 = null;
            if (allData != null && allData.containsKey("lowplate1")) {
                try {
                    lowplate1 = Integer.valueOf(allData.get("lowplate1").toString());
                } catch (NumberFormatException e) {
                    System.err.println("lowplate1 변환 오류: " + e.getMessage());
                }
            }
            
            Integer seat1 = null;
            if (allData != null && allData.containsKey("seat1")) {
                try {
                    seat1 = Integer.valueOf(allData.get("seat1").toString());
                } catch (NumberFormatException e) {
                    System.err.println("seat1 변환 오류: " + e.getMessage());
                }
            }
            
            String carno2 = (allData != null && allData.containsKey("carno2")) ? allData.get("carno2").toString() : null;
            String min2 = (allData != null && allData.containsKey("min2")) ? allData.get("min2").toString() : null;
            
            Integer station2 = null;
            if (allData != null && allData.containsKey("station2")) {
                try {
                    station2 = Integer.valueOf(allData.get("station2").toString());
                } catch (NumberFormatException e) {
                    System.err.println("station2 변환 오류: " + e.getMessage());
                }
            }
            
            String bustype = (allData != null && allData.containsKey("bustype")) ? allData.get("bustype").toString() : null;
            
            Integer bstopidx = null;
            if (allData != null && allData.containsKey("bstopidx")) {
                try {
                    bstopidx = Integer.valueOf(allData.get("bstopidx").toString());
                } catch (NumberFormatException e) {
                    System.err.println("bstopidx 변환 오류: " + e.getMessage());
                }
            }
            
            String arsno = (allData != null && allData.containsKey("arsno")) ? allData.get("arsno").toString() : null;
            
            // 회차 여부 확인
            Boolean isReverse = dto.isReverse();
            
            try {
                // BusArrival 엔티티 생성
                BusArrival busArrival = new BusArrival(
                    id, arrivalTime, lineid, lineno, nodenm, gpsx, gpsy, carno1, min1, station1, 
                    lowplate1, seat1, carno2, min2, station2, bustype, bstopidx, arsno, isReverse
                );
                
                // 저장 또는 업데이트
                busArrivalRepository.save(busArrival);
                
                if (isOperating) {
                    System.out.println("버스 도착 정보가 DB에 저장/업데이트 되었습니다. ID: " + id + ", 운행 중");
                } else {
                    System.out.println("버스 도착 정보의 arrivalTime이 0으로 설정되었습니다. ID: " + id + ", 운행 중이 아님");
                }
            } catch (Exception e) {
                System.err.println("DB 저장 또는 업데이트 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public List<BusArrivalDto> getBusArrivalsByRouteIdFromDb(String routeId) {
        List<BusArrival> busArrivals = new ArrayList<>();
        
        try {
            // routeId가 숫자인지 확인
            Integer routeIdInt = Integer.parseInt(routeId);
            busArrivals = busArrivalRepository.findByRouteId(routeIdInt);
        } catch (NumberFormatException e) {
            // routeId가 숫자가 아닌 경우, 문자열로 처리
            busArrivals = busArrivalRepository.findByRouteNo(routeId);
        }
        
        // 엔티티를 DTO로 변환
        return convertToDtoList(busArrivals);
    }
    
    private List<BusArrivalDto> convertToDtoList(List<BusArrival> entities) {
        List<BusArrivalDto> dtoList = new ArrayList<>();
        
        for (BusArrival entity : entities) {
            Map<String, Object> allData = new HashMap<>();
            allData.put("bstopid", entity.getId());
            allData.put("lineid", entity.getLineid());
            allData.put("nodenm", entity.getNodenm());
            allData.put("gpsx", entity.getGpsx());
            allData.put("gpsy", entity.getGpsy());
            allData.put("carno1", entity.getCarno1());
            allData.put("min1", entity.getMin1());
            allData.put("station1", entity.getStation1());
            allData.put("lowplate1", entity.getLowplate1());
            allData.put("seat1", entity.getSeat1());
            allData.put("carno2", entity.getCarno2());
            allData.put("min2", entity.getMin2());
            allData.put("station2", entity.getStation2());
            allData.put("bustype", entity.getBustype());
            allData.put("bstopidx", entity.getBstopidx());
            allData.put("arsno", entity.getArsno());
            allData.put("isReverse", entity.getIsReverse());
            
            // 운행 중 여부 설정 (arrivalTime이 0보다 크면 운행 중)
            boolean isOperating = entity.getArrivalTime() > 0;
            allData.put("arrivalStatus", isOperating ? true : false);
            
            BusArrivalDto dto = new BusArrivalDto(
                entity.getLineno(), 
                entity.getArrivalTime(),
                allData
            );
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }
}
