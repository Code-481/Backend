package com.deu.java.backend.Festival.service;

import com.deu.java.backend.Festival.CsvFestivalParser;
import com.deu.java.backend.Festival.DTO.FestivalDTO;
import java.util.List;


public class FestivalServiceImpl implements FestivalService {
private final CsvFestivalParser parser;
    public FestivalServiceImpl() {
        this.parser = new CsvFestivalParser();
    }

@Override
    public List<FestivalDTO> getFestivalInfo() {
        try {
            return parser.parseFestivalCSV();
        } catch (Exception e) {
            throw new RuntimeException("CSV 파싱 중 오류 발생", e);
        }
    }
    
}
