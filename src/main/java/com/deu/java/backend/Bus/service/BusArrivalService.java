package com.deu.java.backend.Bus.service;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import java.util.List;

public interface BusArrivalService {
    // API에서 버스 도착 정보 가져오기
    List<BusArrivalDto> getBusArrivalsByStopId(String routeId);

    // DB에서 정류장 ID로 버스 도착 정보 가져오기
    List<BusArrivalDto> getBusArrivalsByRouteIdFromDb(String routeId);

    // 여러 버스 도착정보를 DB에 저장
    void saveArrivals(String bstopid, List<BusArrivalDto> arrivals);
}
