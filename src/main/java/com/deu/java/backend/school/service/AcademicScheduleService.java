package com.deu.java.backend.school.service;


import com.deu.java.backend.school.DTO.AcademicScheduleDTO;
import com.deu.java.backend.school.DTO.EventDTO;

import java.util.*;

public class AcademicScheduleService {

    public AcademicScheduleDTO get2025Schedule() {
        int year = 2025;
        String lastUpdated = "2025-02-26";

        Map<String, List<EventDTO>> monthlySchedule = new LinkedHashMap<>();
        monthlySchedule.put("3월", Arrays.asList(
                new EventDTO("3월 1일", "학기개시일"),
                new EventDTO("3월 4일", "개강일"),
                new EventDTO("3월 6일 ~ 3월 10일", "수강정정"),
                new EventDTO("3월 27일", "수업일수 1/4선"),
                new EventDTO("3월 30일", "학기개시일 30일째")
        ));
        monthlySchedule.put("4월", Arrays.asList(
                new EventDTO("4월 7일", "수업일수 1/3선"),
                new EventDTO("4월 22일 ~ 4월 28일", "중간시험"),
                new EventDTO("4월 23일", "수업일수 1/2선"),
                new EventDTO("4월 29일", "학기경과일수 60일째")
        ));
        monthlySchedule.put("5월", Arrays.asList(
                new EventDTO("5월 1일", "근로자의 날"),
                new EventDTO("5월 15일", "수업일수 2/3선"),
                new EventDTO("5월 23일", "수업일수 3/4선"),
                new EventDTO("5월 29일", "학기경과일수 90일째")
        ));
        monthlySchedule.put("6월", Arrays.asList(
                new EventDTO("6월 3일", "대통령 선거일"),
                new EventDTO("6월 10일", "지정보강일 (5.6 대체휴일 - 어린이날)"),
                new EventDTO("6월 11일", "지정보강일 (5.5 어린이날)"),
                new EventDTO("6월 12일", "지정보강일 (5.1 근로자의날)"),
                new EventDTO("6월 13일", "지정보강일 (6.6 현충일)"),
                new EventDTO("6월 16일", "지정보강일 (6.3 대통령선거일)"),
                new EventDTO("6월 17일 ~ 6월 23일", "기말시험"),
                new EventDTO("6월 24일", "하계방학 시작"),
                new EventDTO("6월 24일 ~ 7월 14일", "하계계절수업")
        ));
        monthlySchedule.put("7월", Arrays.asList(
                new EventDTO("6월 24일 ~ 7월 14일", "하계계절수업 (연장 일정 안내)")
        ));
        monthlySchedule.put("8월", Arrays.asList(
                new EventDTO("8월 18일 ~ 8월 22일", "수강신청"),
                new EventDTO("8월 21일 ~ 8월 26일", "현금등록"),
                new EventDTO("8월 22일", "2024학년도 후기 학위 수여식")
        ));
        monthlySchedule.put("9월", Arrays.asList(
                new EventDTO("9월 1일", "학기개시일, 개강일"),
                new EventDTO("9월 3일 ~ 9월 5일", "수강정정"),
                new EventDTO("9월 24일", "수업일수 1/4선"),
                new EventDTO("9월 30일", "학기경과일수 30일째")
        ));
        monthlySchedule.put("10월", Arrays.asList(
                new EventDTO("10월 10일", "수업일수 1/3선"),
                new EventDTO("10월 22일", "개교기념일"),
                new EventDTO("10월 27일 ~ 10월 31일", "중간시험"),
                new EventDTO("10월 29일", "수업일수 1/2선"),
                new EventDTO("10월 30일", "학기경과일수 60일째")
        ));
        monthlySchedule.put("11월", Arrays.asList(
                new EventDTO("11월 17일", "수업일수 2/3선"),
                new EventDTO("11월 25일", "수업일수 3/4선"),
                new EventDTO("11월 29일", "학기경과일수 90일째")
        ));
        monthlySchedule.put("12월", Arrays.asList(
                new EventDTO("12월 8일 ~ 12월 9일", "지정보강일 (10.6/10.7 추석)"),
                new EventDTO("12월 10일", "지정보강일 (10.8 대체공휴일 - 추석)"),
                new EventDTO("12월 11일", "지정보강일 (10.9 한글날)"),
                new EventDTO("12월 12일", "지정보강일 (10.3 개천절)"),
                new EventDTO("12월 15일", "지정보강일 (10.22 개교기념일)"),
                new EventDTO("12월 16일 ~ 12월 22일", "기말시험"),
                new EventDTO("12월 23일", "동계방학 시작일"),
                new EventDTO("12월 29일 ~ 1월 19일", "동계계절수업")
        ));

        Map<String, List<EventDTO>> semesterSchedule = new LinkedHashMap<>();
        semesterSchedule.put("1학기", Arrays.asList(
                new EventDTO("3월 1일", "학기개시일"),
                new EventDTO("3월 4일", "개강일"),
                new EventDTO("3월 6일 ~ 3월 10일", "수강정정"),
                new EventDTO("3월 27일", "수업일수 1/4선"),
                new EventDTO("3월 30일", "학기개시일 30일째"),
                new EventDTO("4월 7일", "수업일수 1/3선"),
                new EventDTO("4월 22일 ~ 4월 28일", "중간시험"),
                new EventDTO("4월 23일", "수업일수 1/2선"),
                new EventDTO("4월 29일", "학기경과일수 60일째"),
                new EventDTO("5월 1일", "근로자의 날"),
                new EventDTO("5월 15일", "수업일수 2/3선"),
                new EventDTO("5월 23일", "수업일수 3/4선"),
                new EventDTO("5월 29일", "학기경과일수 90일째"),
                new EventDTO("6월 3일", "대통령 선거일"),
                new EventDTO("6월 10일", "지정보강일 (5.6 대체휴일 - 어린이날)"),
                new EventDTO("6월 11일", "지정보강일 (5.5 어린이날)"),
                new EventDTO("6월 12일", "지정보강일 (5.1 근로자의날)"),
                new EventDTO("6월 13일", "지정보강일 (6.6 현충일)"),
                new EventDTO("6월 16일", "지정보강일 (6.3 대통령선거일)"),
                new EventDTO("6월 17일 ~ 6월 23일", "기말시험"),
                new EventDTO("6월 24일", "하계방학 시작"),
                new EventDTO("6월 24일 ~ 7월 14일", "하계계절수업"),
                new EventDTO("8월 18일 ~ 8월 22일", "수강신청"),
                new EventDTO("8월 21일 ~ 8월 26일", "현금등록"),
                new EventDTO("8월 22일", "2024학년도 후기 학위 수여식")
        ));
        semesterSchedule.put("2학기", Arrays.asList(
                new EventDTO("9월 1일", "학기개시일, 개강일"),
                new EventDTO("9월 3일 ~ 9월 5일", "수강정정"),
                new EventDTO("9월 24일", "수업일수 1/4선"),
                new EventDTO("9월 30일", "학기경과일수 30일째"),
                new EventDTO("10월 10일", "수업일수 1/3선"),
                new EventDTO("10월 22일", "개교기념일"),
                new EventDTO("10월 27일 ~ 10월 31일", "중간시험"),
                new EventDTO("10월 29일", "수업일수 1/2선"),
                new EventDTO("10월 30일", "학기경과일수 60일째"),
                new EventDTO("11월 17일", "수업일수 2/3선"),
                new EventDTO("11월 25일", "수업일수 3/4선"),
                new EventDTO("11월 29일", "학기경과일수 90일째"),
                new EventDTO("12월 8일 ~ 12월 9일", "지정보강일 (10.6/10.7 추석)"),
                new EventDTO("12월 10일", "지정보강일 (10.8 대체공휴일 - 추석)"),
                new EventDTO("12월 11일", "지정보강일 (10.9 한글날)"),
                new EventDTO("12월 12일", "지정보강일 (10.3 개천절)"),
                new EventDTO("12월 15일", "지정보강일 (10.22 개교기념일)"),
                new EventDTO("12월 16일 ~ 12월 22일", "기말시험"),
                new EventDTO("12월 23일", "동계방학 시작일"),
                new EventDTO("12월 29일 ~ 1월 19일", "동계계절수업"),
                new EventDTO("2월 19일 ~ 2월 24일", "현금등록, 수강신청"),
                new EventDTO("2월 20일", "2025학년도 전기 학위 수여식")
        ));

        return new AcademicScheduleDTO(year, monthlySchedule, semesterSchedule, lastUpdated);
    }
}