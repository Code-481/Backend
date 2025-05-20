package com.deu.java.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bus")
public class BusEntity {

    @Id //버스 고유 번호
    @Column(name = "bus_id")
    private String busId;

    @ManyToOne // 노선의 식별자
    @JoinColumn(name = "route_id")
    private com.deu.java.backend.entity.RouteEntity route;
    
    @Column(name = "plate_number") // 버스 번호판
    private String plateNumber;
    
    @Column(name = "current_station_id") // 현재 정거장
    private String currentStationId;
    
    @Column(name = "next_station_id") // 다음 정거장
    private String nextStationId;
    
    @Column(name = "status") // 운행중, 미운행
    private String status;
    
    @Column(name = "lat") // 위도
    private double lat;
    
    @Column(name = "lng") // 경도
    private double lng;
    
    
    //생성자
    public BusEntity() {}

    
    //getters. setters
    public String getBusId() {
        return busId;
    }
    public String getPlateNumber() {
        return plateNumber;
    }
    public String getCurrentStationId() {
        return currentStationId;
    }
    public String getNextStationId() {
        return nextStationId;
    }    
    public String getStatus() {
        return status;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
    }
    
    public void setBusId(String busId) {
        this.busId = busId;
    }
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
    public void setCurrentStationId(String currentStationId) {
        this.currentStationId = currentStationId;
    }
    public void setNextStationId(String nextStationId) {
        this.nextStationId = nextStationId;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
}
