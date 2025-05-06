package com.deu.java.backend.Bus.dto;

import java.util.List;

public class BusDTO {
    Long routeId; // 6,6-1,9
    private List<BusInfo> buses; // ex: 6번 버스에 해당하는 버스 객체

    public static class BusInfo {
        private String busId;
        private String plateNumber;
        private String currentStationId;
        private String nextStationId;
        private String status;
        private double lat;
        private double lng;
        
        public BusInfo() {}
        public BusInfo(String busId, String plateNumber, String currentStationId, String nextStationId, String status) {
            this.busId = busId;
            this.plateNumber = plateNumber;
            this.currentStationId = currentStationId;
            this.nextStationId = nextStationId;
            this.status = status;
        }
        
        public String getBusId() { return busId; }
        public String getPlateNumber() { return plateNumber; }
        public String getCurrentStationId() { return currentStationId; }
        public String getNextStationId() { return nextStationId; }
        public String getStatus() { return status; }
        public double getLat() { return lat; }
        public double getLng() { return lng; }
    }

    public BusDTO() { }
    public BusDTO(Long routeId, List<BusInfo> buses) {
        this.routeId = routeId;
        this.buses = buses;
    }

    public Long getRouteId() {
        return routeId;
    }
    public List<BusInfo> getBuses() {
        return buses;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    public void setBuses(List<BusInfo> buses) {
        this.buses = buses;
    }
}
