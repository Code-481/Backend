package com.deu.java.backend.Bus.service;

import com.deu.java.backend.bus.client.BusanBimsApiClient;
import com.deu.java.backend.Bus.dto.BusArrivalDto;

import java.util.ArrayList;
import java.util.List;

public class BusArrivalServiceImpl implements BusArrivalService {

    private final BusanBimsApiClient bimsApiClient;

    public BusArrivalServiceImpl() {
        this.bimsApiClient = new BusanBimsApiClient();
    }

    @Override
    public List<BusArrivalDto> getBusArrivalsByStopId(String stopId) {
        
        List<BusArrivalDto> busArrivals = new ArrayList<>();
        
        try {
            BusArrivalDto arrivalDto = bimsApiClient.fetchArrivalInfo(stopId);
            busArrivals.add(arrivalDto);
        } catch (RuntimeException e) {
           
            System.err.println("버스 도착 정보 호출 실패: " + e.getMessage()); 
            busArrivals.clear();  // 실패 시 빈 리스트를 반환
        }
        return busArrivals;
    }
}