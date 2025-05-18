package com.deu.java.backend.Bus.service;

import com.deu.java.backend.apiClient.BusanBimsApiClient;
import com.deu.java.backend.Bus.dto.BusArrivalDto;

import java.util.ArrayList;
import java.util.List;

public class BusArrivalServiceImpl implements BusArrivalService {

    private final BusanBimsApiClient bimsApiClient;

    public BusArrivalServiceImpl(BusanBimsApiClient bimsApiClient) {
        this.bimsApiClient = bimsApiClient;
    }

    @Override
    public List<BusArrivalDto> getBusArrivalsByStopId(String stopId) {
        
        List<BusArrivalDto> busArrivals = new ArrayList<>();
        
        try {
            List<BusArrivalDto> arrivalDto = bimsApiClient.fetchArrivalInfo(stopId);
            busArrivals.addAll(arrivalDto);
            
        } catch (RuntimeException e) {
           
            System.err.println("버스 도착 정보 호출 실패: " + e.getMessage()); 
            busArrivals.clear();  // 실패 시 빈 리스트를 반환
        }
        return busArrivals;
    }
}