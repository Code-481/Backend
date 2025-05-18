package com.deu.java.backend.Bus.dto;

import java.util.Map;

public class BusArrivalDto {
    private String busNo;  // lineno
    private long arrivalTime;
    private Map<String, Object> allData;
    private boolean isOperating;
    private boolean isReverse;  // 회차 여부

    public BusArrivalDto(String busNo, long arrivalTime) {
        this.busNo = busNo;
        this.arrivalTime = arrivalTime;
        this.isOperating = false;
        this.isReverse = false;
    }

    public BusArrivalDto(String busNo, long arrivalTime, Map<String, Object> allData) {
        this.busNo = busNo;
        this.arrivalTime = arrivalTime;
        this.allData = allData;
        
        // 운행 중 여부 설정
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
                // min1 값이 숫자인 경우 운행 중으로 간주
                Object min1Obj = allData.get("min1");
                if (min1Obj instanceof Integer || min1Obj instanceof Long) {
                    this.isOperating = (Integer.parseInt(min1Obj.toString()) > 0);
                } else if (min1Obj instanceof String) {
                    try {
                        int min = Integer.parseInt((String) min1Obj);
                        this.isOperating = (min > 0);
                    } catch (NumberFormatException e) {
                        this.isOperating = false;
                    }
                }
            }
            
            // 회차 여부 설정 (bstopidx 값을 기준으로 판단)
            // 예: bstopidx가 특정 값보다 크면 회차로 간주 (실제 로직은 비즈니스 요구사항에 맞게 조정 필요)
            if (allData.containsKey("bstopidx")) {
                Object bstopidxObj = allData.get("bstopidx");
                try {
                    int bstopidx = Integer.parseInt(bstopidxObj.toString());
                    // 예시: bstopidx가 50보다 크면 회차로 간주 (실제 로직은 비즈니스 요구사항에 맞게 조정 필요)
                    this.isReverse = (bstopidx > 50);
                } catch (NumberFormatException e) {
                    this.isReverse = false;
                }
            }
        }
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

    public boolean isOperating() {
        return isOperating;
    }

    public void setOperating(boolean operating) {
        this.isOperating = operating;
    }
    
    public boolean isReverse() {
        return isReverse;
    }
    
    public void setReverse(boolean reverse) {
        this.isReverse = reverse;
    }

    // 특정 데이터 가져오기
    public Object getData(String key) {
        return allData != null ? allData.get(key) : null;
    }
    
    // 편의 메서드
    public Long getBstopId() {
        if (allData != null && allData.containsKey("bstopid")) {
            try {
                return Long.valueOf(allData.get("bstopid").toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public String getLineId() {
        if (allData != null && allData.containsKey("lineid")) {
            return allData.get("lineid").toString();
        }
        return null;
    }
    
    public String getNodeName() {
        if (allData != null && allData.containsKey("nodenm")) {
            return allData.get("nodenm").toString();
        }
        return null;
    }
    
    public String getCarNo1() {
        if (allData != null && allData.containsKey("carno1")) {
            return allData.get("carno1").toString();
        }
        return null;
    }
    
    public String getMin1() {
        if (allData != null && allData.containsKey("min1")) {
            return allData.get("min1").toString();
        }
        return null;
    }
    
    public Integer getStation1() {
        if (allData != null && allData.containsKey("station1")) {
            try {
                return Integer.valueOf(allData.get("station1").toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public String getCarNo2() {
        if (allData != null && allData.containsKey("carno2")) {
            return allData.get("carno2").toString();
        }
        return null;
    }
    
    public String getMin2() {
        if (allData != null && allData.containsKey("min2")) {
            return allData.get("min2").toString();
        }
        return null;
    }
    
    public Integer getStation2() {
        if (allData != null && allData.containsKey("station2")) {
            try {
                return Integer.valueOf(allData.get("station2").toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "BusArrivalDto{" +
                "busNo='" + busNo + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", isOperating=" + isOperating +
                ", isReverse=" + isReverse +
                ", nodeName=" + getNodeName() +
                ", min1=" + getMin1() +
                ", station1=" + getStation1() +
                ", min2=" + getMin2() +
                ", station2=" + getStation2() +
                '}';
    }
}
