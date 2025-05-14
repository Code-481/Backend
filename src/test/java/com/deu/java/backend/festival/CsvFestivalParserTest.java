package com.deu.java.backend.festival;

import com.deu.java.backend.Festival.CsvFestivalParser;
import com.deu.java.backend.Festival.DTO.FestivalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CsvFestivalParserTest {

    private CsvFestivalParser parser;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testParseFestivalCSV() throws Exception {
        // 현재 날짜 기준으로 더미 데이터 준비

        LocalDate fixedNow = LocalDate.of(2023, 5, 4); // 테스트에서 사용할 고정된 날짜

        parser = new CsvFestivalParser(fixedNow); // 주입된 날짜 사용

        // 더미 데이터 생성 (현재 날짜 기준)
        List<FestivalDTO> festivals = new ArrayList<>();
        festivals.add(new FestivalDTO("id1","Busan Festival", "20230501", "20230505", "Busan", "010-1234-5678"));
        festivals.add(new FestivalDTO("id2","Sea Festival", "20230506", "20230510", "Busan", "010-2345-6789"));
        festivals.add(new FestivalDTO("id3","Old Festival", "20230401", "20230405", "Busan", "010-3456-7890"));

        System.out.println(festivals.get(0).getEndDate());
        System.out.println(festivals.get(1).getEndDate());
        System.out.println(festivals.get(2).getEndDate());

        // 현재 날짜 이후의 축제만 필터링 (5월 4일 기준)
        List<FestivalDTO> filteredFestivals = festivals.stream()
                .filter(festival -> {
                    LocalDate festivalEndDate = LocalDate.parse(festival.getEndDate(), DateTimeFormatter.BASIC_ISO_DATE);
                    return !festivalEndDate.isBefore(fixedNow);  // 5월 4일 이후의 축제만 남김
                })
                .collect(Collectors.toList());
        
        // 필터링된 데이터가 잘 반영됐는지 검증
        assertNotNull(filteredFestivals);
        assertFalse(filteredFestivals.isEmpty());
        assertEquals(2, filteredFestivals.size()); // 5월 4일 이후의 축제만 두 개여야 함
    }
}