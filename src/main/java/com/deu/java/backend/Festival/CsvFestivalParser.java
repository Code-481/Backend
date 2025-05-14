package com.deu.java.backend.Festival;

import com.deu.java.backend.Festival.DTO.FestivalDTO;
import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CsvFestivalParser {

    private LocalDate fixedNow;
    private static final List<String> TARGET_HEADERS = List.of("ID", "명칭", "행사시작일", "행사종료일", "주소", "개요");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    //생성자
    public CsvFestivalParser(LocalDate fixedNow) {
        this.fixedNow = fixedNow != null ? fixedNow : LocalDate.now();
    }

    public CsvFestivalParser() {
        this(LocalDate.now());
    }

    public List<FestivalDTO> parseFestivalCSV() throws Exception {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("BusanFestivals.csv");
        List<FestivalDTO> festivals = new ArrayList<>();
        String[] row;

        //인식되는 파일이 없을 때
        if (inputStream == null) {
            throw new FileNotFoundException("Cannot find 'BusanFestivals.csv' in resources folder.");
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String[] headers = reader.readNext();

            //인식되는 값이 없을 때
            if (headers == null) {
                throw new IllegalStateException("CSV 파일이 비어 있습니다.");
            }

            // 헤더 인덱스 매핑
            Map<String, Integer> headerMap = mapHeaders(headers);

            while ((row = reader.readNext()) != null) {
                try {
                    //행사 종료일이 빈 값
                    String endDateStr = getColumn(row, headerMap, "행사종료일");
                    if (endDateStr == null || endDateStr.isBlank()) {
                        continue;
                    }
                    
                    // 개요 70자 이내
                    String description = getColumn(row, headerMap, "개요");
                    if (description == null ||description.length()>70) { 
                        description = description.substring(0, 70);
                    }

                    //행사가 끝남
                    LocalDate endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
                    if (endDate.isBefore(fixedNow)) {
                        continue;
                    }
                    
                    //dto 추가
                    FestivalDTO dto = new FestivalDTO(
                            getColumn(row, headerMap, "ID"),
                            getColumn(row, headerMap, "명칭"),
                            getColumn(row, headerMap, "행사시작일"),
                            endDateStr,
                            getColumn(row, headerMap, "주소"),
                            description
                    );
                    festivals.add(dto);
                    
                } catch (Exception e) {
                    System.out.println("오류 발생 (해당 줄 무시): " + Arrays.toString(row));
                }
            }
            return festivals;
        }
    }

    // 헤더 인덱스 매핑 함수
    private Map<String, Integer> mapHeaders(String[] headers) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String name = headers[i].trim();
            if (TARGET_HEADERS.contains(name)) {
                headerMap.put(name, i);
            }
        }
        return headerMap;
    }

    //파일 검증 함수
    private String getColumn(String[] row, Map<String, Integer> headerMap, String columnName) {
        Integer index = headerMap.get(columnName);
        return (index != null && index < row.length) ? row[index].trim() : null;
    }
}
