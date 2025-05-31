package com.deu.java.backend.Festival;

import com.deu.java.backend.Festival.DTO.FestivalDTO;
import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvFestivalParser {

    private static final List<String> TARGET_HEADERS = List.of(
            "콘텐츠ID", "제목", "이용요일 및 시간", "행사종료일", "주소", "개요", "썸네일이미지URL", "운영기간"
    );

    public CsvFestivalParser() {}

    public List<FestivalDTO> parseFestivalCSV() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("BusanFestivals.csv");
        List<FestivalDTO> festivals = new ArrayList<>();
        String[] row;

        if (inputStream == null) {
            throw new FileNotFoundException("Cannot find 'BusanFestivals.csv' in resources folder.");
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext();
            if (headers == null) {
                throw new IllegalStateException("CSV 파일이 비어 있습니다.");
            }

            Map<String, Integer> headerMap = mapHeaders(headers);

            while ((row = reader.readNext()) != null) {
                try {
                    // 날짜 관련 필드 추출
                    String endDateStr = getColumn(row, headerMap, "행사종료일");
                    String startDateStr = getColumn(row, headerMap, "이용요일 및 시간");
                    String periodStr = getColumn(row, headerMap, "운영기간");

                    // 2025년 포함 여부 체크
                    boolean is2025 = (endDateStr != null && endDateStr.contains("2025"))
                            || (startDateStr != null && startDateStr.contains("2025"))
                            || (periodStr != null && periodStr.contains("2025"));
                    if (!is2025) continue;

                    String description = getColumn(row, headerMap, "개요");
                    if (description != null && description.length() > 70) {
                        description = description.substring(0, 70);
                    }

                    FestivalDTO dto = new FestivalDTO(
                            getColumn(row, headerMap, "콘텐츠ID"),
                            getColumn(row, headerMap, "제목"),
                            startDateStr,
                            endDateStr,
                            getColumn(row, headerMap, "주소"),
                            description,
                            getColumn(row, headerMap, "썸네일이미지URL")
                    );
                    festivals.add(dto);
                } catch (Exception e) {
                    System.out.println("오류 발생 (해당 줄 무시): " + Arrays.toString(row));
                }
            }
        }
        return festivals;
    }

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

    private String getColumn(String[] row, Map<String, Integer> headerMap, String columnName) {
        Integer index = headerMap.get(columnName);
        return (index != null && index < row.length) ? row[index].trim() : null;
    }
}
