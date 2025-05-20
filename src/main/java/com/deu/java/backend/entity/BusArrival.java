package com.deu.java.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "busArraival")
public class BusArrival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 자동 생성되는 기본 키

    @Column(name = "bstopid", nullable = false)
    private String bstopid; // 정류소 ID

    @Column(name = "bus_no", nullable = false)
    private String busNo; // 버스 번호

    @Column(name = "arrival_time")
    private Long arrivalTime; // 도착 시간

    @Column(name = "all_data", columnDefinition = "TEXT")
    private String allData; // 모든 데이터(JSON)

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt; // 업데이트 시간

    @Column(name = "lineid")
    private String lineid; // 노선ID

    @Column(name = "lineno")
    private String lineno; // 버스번호

    @Column(name = "nodenm")
    private String nodenm; // 정류소명

    @Column(name = "gpsx")
    private String gpsx; // GPS X좌표

    @Column(name = "gpsy")
    private String gpsy; // GPS Y좌표

    @Column(name = "carno1")
    private String carno1; // 앞차 차량번호

    @Column(name = "min1")
    private String min1; // 앞차 남은도착시간

    @Column(name = "station1")
    private Integer station1; // 앞차 남은정류소 수

    @Column(name = "lowplate1")
    private Integer lowplate1; // 앞차 저상버스 여부 (0: 일반, 1: 저상)

    @Column(name = "seat1")
    private Integer seat1; // 앞차 빈 좌석 수

    @Column(name = "carno2")
    private String carno2; // 뒷차 차량번호

    @Column(name = "min2")
    private String min2; // 뒷차 남은도착시간

    @Column(name = "station2")
    private Integer station2; // 뒷차 남은정류소 수

    @Column(name = "bustype")
    private String bustype; // 버스 종류

    @Column(name = "bstopidx")
    private Integer bstopidx; // 노선 정류소 순번

    @Column(name = "arsno")
    private String arsno; // 정류소번호

    @Column(name = "isReverse")
    private Boolean isReverse; // 회차 여부 (true: 회차, false: 정방향)

    // 기본 생성자
    public BusArrival() {
    }

    // 필수 필드를 포함한 생성자
    public BusArrival(String bstopid, String busNo, Long arrivalTime, String allData, java.time.LocalDateTime updatedAt) {
        this.bstopid = bstopid;
        this.busNo = busNo;
        this.arrivalTime = arrivalTime;
        this.allData = allData;
        this.updatedAt = updatedAt;
    }

    // 모든 필드를 포함한 생성자
    public BusArrival(Long id, String bstopid, String busNo, Long arrivalTime, String allData,
                      java.time.LocalDateTime updatedAt, String lineid, String lineno, String nodenm,
                      String gpsx, String gpsy, String carno1, String min1, Integer station1,
                      Integer lowplate1, Integer seat1, String carno2, String min2,
                      Integer station2, String bustype, Integer bstopidx, String arsno, Boolean isReverse) {
        this.id = id;
        this.bstopid = bstopid;
        this.busNo = busNo;
        this.arrivalTime = arrivalTime;
        this.allData = allData;
        this.updatedAt = updatedAt;
        this.lineid = lineid;
        this.lineno = lineno;
        this.nodenm = nodenm;
        this.gpsx = gpsx;
        this.gpsy = gpsy;
        this.carno1 = carno1;
        this.min1 = min1;
        this.station1 = station1;
        this.lowplate1 = lowplate1;
        this.seat1 = seat1;
        this.carno2 = carno2;
        this.min2 = min2;
        this.station2 = station2;
        this.bustype = bustype;
        this.bstopidx = bstopidx;
        this.arsno = arsno;
        this.isReverse = isReverse;
    }

    // Getter와 Setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBstopid() {
        return bstopid;
    }

    public void setBstopid(String bstopid) {
        this.bstopid = bstopid;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public Long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getAllData() {
        return allData;
    }

    public void setAllData(String allData) {
        this.allData = allData;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    public String getLineno() {
        return lineno;
    }

    public void setLineno(String lineno) {
        this.lineno = lineno;
    }

    public String getNodenm() {
        return nodenm;
    }

    public void setNodenm(String nodenm) {
        this.nodenm = nodenm;
    }

    public String getGpsx() {
        return gpsx;
    }

    public void setGpsx(String gpsx) {
        this.gpsx = gpsx;
    }

    public String getGpsy() {
        return gpsy;
    }

    public void setGpsy(String gpsy) {
        this.gpsy = gpsy;
    }

    public String getCarno1() {
        return carno1;
    }

    public void setCarno1(String carno1) {
        this.carno1 = carno1;
    }

    public String getMin1() {
        return min1;
    }

    public void setMin1(String min1) {
        this.min1 = min1;
    }

    public Integer getStation1() {
        return station1;
    }

    public void setStation1(Integer station1) {
        this.station1 = station1;
    }

    public Integer getLowplate1() {
        return lowplate1;
    }

    public void setLowplate1(Integer lowplate1) {
        this.lowplate1 = lowplate1;
    }

    public Integer getSeat1() {
        return seat1;
    }

    public void setSeat1(Integer seat1) {
        this.seat1 = seat1;
    }

    public String getCarno2() {
        return carno2;
    }

    public void setCarno2(String carno2) {
        this.carno2 = carno2;
    }

    public String getMin2() {
        return min2;
    }

    public void setMin2(String min2) {
        this.min2 = min2;
    }

    public Integer getStation2() {
        return station2;
    }

    public void setStation2(Integer station2) {
        this.station2 = station2;
    }

    public String getBustype() {
        return bustype;
    }

    public void setBustype(String bustype) {
        this.bustype = bustype;
    }

    public Integer getBstopidx() {
        return bstopidx;
    }

    public void setBstopidx(Integer bstopidx) {
        this.bstopidx = bstopidx;
    }

    public String getArsno() {
        return arsno;
    }

    public void setArsno(String arsno) {
        this.arsno = arsno;
    }

    public Boolean getIsReverse() {
        return isReverse;
    }

    public void setIsReverse(Boolean isReverse) {
        this.isReverse = isReverse;
    }
}
