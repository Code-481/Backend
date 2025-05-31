package com.deu.java.backend.Festival.service;
import com.deu.java.backend.Festival.DTO.FestivalDto;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FestivalService {

    public List<FestivalDto> getFestivals() throws Exception {
        List<FestivalDto> result = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("BusanFestivals.csv");
        if (inputStream == null) throw new IllegalArgumentException("CSV 파일을 찾을 수 없습니다.");

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext();
            if (headers == null) return result;
            String[] row;
            while ((row = reader.readNext()) != null) {
                Map<String, String> map = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    map.put(headers[i].trim(), i < row.length ? row[i].trim() : "");
                }
                result.add(new FestivalDto(map));
            }
        }
        return result;
    }
}