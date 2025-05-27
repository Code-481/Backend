package com.deu.java.backend.apiClient.compoment;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KmaBaseTimeUtil {
    public static String[] getLatestBaseDateTime(LocalDateTime now) {
        int minuteThreshold = 10;
        int hour = now.getHour();
        int minute = now.getMinute();

        if (hour == 0 && minute < minuteThreshold) {
            LocalDate yesterday = now.toLocalDate().minusDays(1);
            return new String[]{
                    yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    "2300"
            };
        }

        int baseHour = hour - 1;
        if (minute < minuteThreshold) {
            baseHour = (hour == 0) ? 23 : hour - 1;
            if (hour == 0) {
                LocalDate yesterday = now.toLocalDate().minusDays(1);
                return new String[]{
                        yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        String.format("%02d00", baseHour)
                };
            }
        }
        return new String[]{
                now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                String.format("%02d00", baseHour)
        };
    }
}
