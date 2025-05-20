package com.deu.java.backend.Bus.dto;

import java.util.Map;

public class BusArrivalDto {

    private String busNo; // lineno
    private long arrivalTime;
    private Map allData;
    private boolean isOperating;
    private boolean isReverse;

    public BusArrivalDto(String busNo, long arrivalTime, Map allData) {
        this.busNo = busNo;
        this.arrivalTime = arrivalTime;
        this.allData = allData;
        this.isOperating = false;
        if (allData != null) {
            if (allData.containsKey("arrivalStatus")) {
                Object statusObj = allData.get("arrivalStatus");
                if (statusObj instanceof Boolean) {
                    this.isOperating = (Boolean) statusObj;
                } else if (statusObj instanceof String) {
                    this.isOperating = "true".equalsIgnoreCase((String) statusObj) ||
                            "운행중".equals((String) statusObj);
                }
            } else if (allData.containsKey("min1")) {
                Object min1Obj = allData.get("min1");
                try {
                    int min = Integer.parseInt(min1Obj.toString());
                    this.isOperating = (min > 0);
                } catch (NumberFormatException e) {
                    this.isOperating = false;
                }
            }
            if (allData.containsKey("bstopidx")) {
                Object bstopidxObj = allData.get("bstopidx");
                try {
                    int bstopidx = Integer.parseInt(bstopidxObj.toString());
                    this.isReverse = (bstopidx > 50);
                } catch (NumberFormatException e) {
                    this.isReverse = false;
                }
            }
        }
    }

    public String getBusNo() { return busNo; }
    public long getArrivalTime() { return arrivalTime; }
    public Map getAllData() { return allData; }

}
