package com.deu.java.backend.Bus.dto;

public class BusArrivalDto {
    private String busNo;
    private long arrivalTime;

    public BusArrivalDto(String busNo, long arrivalTime) {
        this.busNo = busNo;
        this.arrivalTime = arrivalTime;
    }

    public BusArrivalDto() { }

    public String getBusNo() {
        return busNo;
    }
    public long getArrivalTime() {
        return arrivalTime;
    }
    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }
    public void setArrivalTime(Long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
