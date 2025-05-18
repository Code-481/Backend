package com.deu.java.backend.Bus.dto;

import java.util.Map;

public class BusArrivalDto {
    private String busNo;
    private long arrivalTime;
    private Map<String, Object> allData; // 모든 데이터를 저장할 맵

    public BusArrivalDto(String busNo, long arrivalTime) {
        this.busNo = busNo;
        this.arrivalTime = arrivalTime;
    }

    public BusArrivalDto(String busNo, long arrivalTime, Map<String, Object> allData) {
        this.busNo = busNo;
        this.arrivalTime = arrivalTime;
        this.allData = allData;
    }

    // getter와 setter 메소드
    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Map<String, Object> getAllData() {
        return allData;
    }

    public void setAllData(Map<String, Object> allData) {
        this.allData = allData;
    }

    // 특정 데이터 가져오기
    public Object getData(String key) {
        return allData != null ? allData.get(key) : null;
    }
}
