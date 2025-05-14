package com.deu.java.backend.Festival.DTO;

import com.opencsv.bean.CsvBindByName;

public class FestivalDTO {

    public FestivalDTO(String id, String name, String startDate, String endDate, String address, String description) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.description = description;
    }
    
    @CsvBindByName(column = "ID")
    private String id;

    @CsvBindByName(column = "명칭")
    private String name;

     @CsvBindByName(column = "행사시작일")
    private String startDate;
     
    @CsvBindByName(column = "행사종료일")
    private String endDate;
    
    @CsvBindByName(column = "행사주소")
    private String address;
    
    @CsvBindByName(column = "개요")
    private String description;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
}
