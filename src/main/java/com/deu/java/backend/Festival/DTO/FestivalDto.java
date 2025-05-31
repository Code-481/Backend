package com.deu.java.backend.Festival.DTO;

import com.opencsv.bean.CsvBindByName;

import java.util.Map;

public class FestivalDto {
    private Map<String, String> fields;

    public FestivalDto(Map<String, String> fields) {
        this.fields = fields;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}