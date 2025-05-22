package com.deu.java.backend.apiClient;

import com.deu.java.backend.Weather.dto.WeatherWeekDTO;
import com.deu.java.backend.entity.WeatherTodayEntity;
import io.github.cdimascio.dotenv.Dotenv;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class WeatherApiClient {

    static Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    private static final String todayWeatherKey = dotenv.get("TODAY_WEATHER_API_KEY");
    private static final String weekWeatherKey = dotenv.get("WEEK_WEATHER_API_KEY");

    // 오늘 오전/오후 날씨를 위한 동네예보 통보문 API
    private static final String TODAY_WEATHER_URL = "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstMsgService/getLandFcst?pageNo=1&numOfRows=10&dataType=XML&regId=11B10101&authKey=" + todayWeatherKey;

    // 주간 날씨를 위한 API URLs
    private static final String WEEKLY_WEATHER_C_URL = "https://apihub.kma.go.kr/api/typ01/url/fct_afs_wc.php?reg=11H20201&tmfc=0&disp=1&help=0&authKey=" + weekWeatherKey;
    private static final String WEEKLY_WEATHER_L_URL = "https://apihub.kma.go.kr/api/typ01/url/fct_afs_wl.php?reg=11H20000&tmfc=0&disp=1&help=0&authKey=" + weekWeatherKey;

    private String sendGetRequest(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // 타임아웃 설정 추가 (Socket Timeout Exception 방지)
        conn.setConnectTimeout(10000); // 10초
        conn.setReadTimeout(15000);    // 15초

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } finally {
            conn.disconnect();
        }
    }

    private String getElementTextByTagName(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            String text = nodeList.item(0).getTextContent();
            return text != null ? text.trim() : "";
        }
        return "";
    }

    // 개선된 온도 파싱 메서드
    private Integer parseTemperatureSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // 온도 문자열에서 숫자만 추출
            String cleanValue = value.replaceAll("[^-0-9]", "");
            if (cleanValue.isEmpty()) {
                return null;
            }

            int temp = Integer.parseInt(cleanValue);
            // 한국 기온 범위 검증 (-40도 ~ 50도)
            if (temp >= -40 && temp <= 50) {
                return temp;
            }
            return null;
        } catch (NumberFormatException e) {
            System.err.println("온도 파싱 실패: " + value);
            return null;
        }
    }

    private int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String extractDataBetweenMarkers(String text) {
        int startIdx = text.indexOf("#START7777");
        int endIdx = text.indexOf("#7777END");
        if (startIdx < 0 || endIdx < 0 || endIdx <= startIdx) {
            return "";
        }
        return text.substring(startIdx + "#START7777".length(), endIdx).trim();
    }

    // 오늘 오전/오후 날씨 조회 (개선된 버전)
    public List<WeatherTodayEntity> fetchTodayWeatherMorningAfternoon() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            List<WeatherTodayEntity> todayWeatherList = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(TODAY_WEATHER_URL);

            Element root = document.getDocumentElement();
            NodeList recordList = root.getElementsByTagName("item");

            LocalDate today = LocalDate.now();

            for (int i = 0; i < recordList.getLength(); i++) {
                Node recordNode = recordList.item(i);
                if (recordNode.getNodeType() != Node.ELEMENT_NODE) continue;

                Element recordElement = (Element) recordNode;

                String announceTime = getElementTextByTagName(recordElement, "announceTime");
                String wfCd = getElementTextByTagName(recordElement, "wfCd");
                String taStr = getElementTextByTagName(recordElement, "ta");
                String rnYn = getElementTextByTagName(recordElement, "rnYn");
                String numEfStr = getElementTextByTagName(recordElement, "numEf");

                // numEf가 0 또는 1인 경우만 처리 (오늘과 내일)
                Integer numEf = parseTemperatureSafe(numEfStr);
                if (numEf == null || numEf > 1) continue;

                Integer temperature = parseTemperatureSafe(taStr);
                if (temperature == null) {
                    System.err.println("온도 데이터 누락: " + announceTime);
                    continue;
                }

                // 오전/오후 구분을 위한 시간 정보 추가
                String timeInfo = determineTimeOfDay(announceTime);

                WeatherTodayEntity weatherEntity = new WeatherTodayEntity(
                        announceTime + "_" + timeInfo, // 시간 정보 포함
                        temperature,
                        wfCd,
                        rnYn
                );

                todayWeatherList.add(weatherEntity);
            }

            if (todayWeatherList.isEmpty()) {
                throw new RuntimeException("오늘 날씨 데이터를 찾을 수 없습니다.");
            }

            return todayWeatherList;

        } catch (Exception e) {
            throw new RuntimeException("오늘 날씨 데이터를 불러오는 데 실패했습니다.", e);
        }
    }

    // 시간대별 구분 메서드
    private String determineTimeOfDay(String announceTime) {
        if (announceTime.length() >= 10) {
            String timeStr = announceTime.substring(8, 10);
            int hour = Integer.parseInt(timeStr);

            if (hour >= 6 && hour < 12) {
                return "MORNING";
            } else if (hour >= 12 && hour < 18) {
                return "AFTERNOON";
            } else if (hour >= 18 && hour < 24) {
                return "EVENING";
            } else {
                return "NIGHT";
            }
        }
        return "UNKNOWN";
    }

    // 기존 fetchTodayWeather 메서드 (호환성 유지)
    public WeatherTodayEntity fetchTodayWeather() {
        // 기존 구현 유지
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            WeatherTodayEntity latestWeather = null;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(TODAY_WEATHER_URL);

            Element root = document.getDocumentElement();
            NodeList recordList = root.getElementsByTagName("item");

            for (int i = 0; i < recordList.getLength(); i++) {
                Node recordNode = recordList.item(i);
                if (recordNode.getNodeType() != Node.ELEMENT_NODE) continue;

                Element recordElement = (Element) recordNode;

                String announceTime = getElementTextByTagName(recordElement, "announceTime");
                String wfCd = getElementTextByTagName(recordElement, "wfCd");
                String ta = getElementTextByTagName(recordElement, "ta");
                String rnYn = getElementTextByTagName(recordElement, "rnYn");
                String numEf = getElementTextByTagName(recordElement, "numEf");

                int numEfInt = parseIntSafe(numEf, -1);
                if (numEfInt == 0) {
                    int temperature = parseIntSafe(ta, -999);
                    WeatherTodayEntity candidate = new WeatherTodayEntity(
                            announceTime, temperature, wfCd, rnYn
                    );

                    if (latestWeather == null ||
                            LocalDateTime.parse(candidate.getDate(), formatter)
                                    .isAfter(LocalDateTime.parse(latestWeather.getDate(), formatter))) {
                        latestWeather = candidate;
                    }
                }
            }

            if (latestWeather == null) {
                throw new RuntimeException("numEf=0인 오늘 날씨 데이터가 없습니다.");
            }

            return latestWeather;
        } catch (Exception e) {
            throw new RuntimeException("오늘 날씨 데이터를 불러오는 데 실패했습니다.", e);
        }
    }

    // 개선된 주간 날씨 조회 (1주일 단위)
    public List<WeatherWeekDTO> fetchWeekWeather() {
        // 기존 구현 유지하되 타입 안전성 개선
        try {
            String responseC = sendGetRequest(WEEKLY_WEATHER_C_URL);
            String responseL = sendGetRequest(WEEKLY_WEATHER_L_URL);

            Map<LocalDate, Map<String, Object>> weatherMap = new HashMap<>();
            LocalDate baseDate = LocalDate.now();

            // 기온 데이터 파싱
            String dataC = extractDataBetweenMarkers(responseC);
            String[] recordsC = dataC.split("=");

            int dayIndex = 0;
            for (String record : recordsC) {
                record = record.trim();
                if (record.isEmpty() || record.startsWith("#") || record.startsWith("REG_ID")) continue;

                String[] parts = record.split(",");
                if (parts.length < 12) continue;

                LocalDate date = baseDate.plusDays(dayIndex);
                try {
                    int min = Integer.parseInt(parts[6]);
                    int max = Integer.parseInt(parts[7]);
                    Map<String, Object> map = weatherMap.computeIfAbsent(date, k -> new HashMap<>());
                    map.put("min", min);
                    map.put("max", max);
                } catch (NumberFormatException ignored) {}
                dayIndex++;
            }

            // 하늘/강수 데이터 파싱
            String dataL = extractDataBetweenMarkers(responseL);
            String[] recordsL = dataL.split("=");

            dayIndex = 0;
            for (String record : recordsL) {
                record = record.trim();
                if (record.isEmpty() || record.startsWith("#") || record.startsWith("REG_ID")) continue;

                String[] parts = record.split(",");
                if (parts.length < 8) continue;

                LocalDate date = baseDate.plusDays(dayIndex);
                String sky = parts[6];
                String rain = parts[7];

                Map<String, Object> map = weatherMap.computeIfAbsent(date, k -> new HashMap<>());
                map.put("sky", sky);
                map.put("rain", rain);
                dayIndex++;
            }

            // DTO 생성
            return weatherMap.entrySet().stream()
                    .map(e -> {
                        LocalDate date = e.getKey();
                        Map<String, Object> v = e.getValue();
                        int min = (Integer) v.getOrDefault("min", -999);
                        int max = (Integer) v.getOrDefault("max", -999);
                        String sky = (String) v.getOrDefault("sky", "");
                        String rain = (String) v.getOrDefault("rain", "");
                        return new WeatherWeekDTO(date, min, max, sky, rain);
                    })
                    .sorted(Comparator.comparing(WeatherWeekDTO::getDate))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("주간 날씨 불러오기 실패", e);
        }
    }
}
