package com.deu.java.backend.apiClient;

import com.deu.java.backend.Weather.DTO.WeatherWeekDTO;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherApiClient {

    static Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();
    private static final String todayWeatherKey = dotenv.get("TODAY_WEATHER_API_KEY");
    private static final String weekWeatherKey = dotenv.get("WEEK_WEATHER_API_KEY");
    private static final String TODAY_WEATHER_URL = "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstMsgService/getLandFcst?pageNo=1&numOfRows=10&dataType=XML&regId=11B10101&authKey=" + todayWeatherKey;
    private static final String WEEKLY_WEATHER_C_URL = "https://apihub.kma.go.kr/api/typ01/url/fct_afs_wc.php?reg=11H20201&tmfc=0&disp=1&help=0&authKey=" + weekWeatherKey;
    private static final String WEEKLY_WEATHER_L_URL = "https://apihub.kma.go.kr/api/typ01/url/fct_afs_wl.php?reg=11H20000&tmfc=0&disp=1&help=0&authKey=" + weekWeatherKey;

    private String sendGetRequest(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
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
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
    private String extractDataBetweenMarkers(String text) {
        int startIdx = text.indexOf("#START7777");
        int endIdx = text.indexOf("#7777END");

        if (startIdx < 0 || endIdx < 0 || endIdx <= startIdx) {
            return "";
        }
        // startMarker 끝 다음부터 endMarker 시작 직전까지
        return text.substring(startIdx + "#START7777".length(), endIdx).trim();
    }
    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public WeatherTodayEntity fetchTodayWeather() {
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

                Map<String, String> parsed = Map.of(
                        "announceTime", getElementTextByTagName(recordElement, "announceTime"),
                        "wfCd", getElementTextByTagName(recordElement, "wfCd"),
                        "ta", getElementTextByTagName(recordElement, "ta"),
                        "rnYn", getElementTextByTagName(recordElement, "rnYn"),
                        "numEf", getElementTextByTagName(recordElement, "numEf")
                );

                int numEf = parseIntSafe(parsed.get("numEf"), -1);

                if (numEf == 0) {
                    int ta = parseIntSafe(parsed.get("ta"), -999);

                    WeatherTodayEntity candidate = new WeatherTodayEntity(
                            parsed.get("announceTime"),
                            ta,
                            parsed.get("wfCd"),
                            parsed.get("rnYn")
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

    public List<WeatherWeekDTO> fetchWeekWeather() {
        try {
            String responseC = sendGetRequest(WEEKLY_WEATHER_C_URL);
            String responseL = sendGetRequest(WEEKLY_WEATHER_L_URL);

            Map<LocalDate, Map<String, Object>> weatherMap = new HashMap<>();
            LocalDate baseDate = LocalDate.now();

            // 1. 기온 데이터 파싱
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

            // 2. 하늘/강수 데이터 파싱
            String dataL = extractDataBetweenMarkers(responseL);
            String[] recordsL = dataL.split("=");

            dayIndex = 0;
            for (String record : recordsL) {
                record = record.trim();
                if (record.isEmpty() || record.startsWith("#") || record.startsWith("REG_ID")) continue;

                String[] parts = record.split(",");
                if (parts.length < 8) continue;

                LocalDate date = baseDate.plusDays(dayIndex);

                String sky = parts[6];  // 예: WB01
                String rain = parts[7]; // 예: WB00

                Map<String, Object> map = weatherMap.computeIfAbsent(date, k -> new HashMap<>());
                map.put("sky", sky);
                map.put("rain", rain);

                dayIndex++;
            }

            // 3. DTO 생성
            return weatherMap.entrySet().stream()
                    .map(e -> {
                        LocalDate date = e.getKey();
                        Map<String, Object> v = e.getValue();
                        int min = (int) v.getOrDefault("min", -999);
                        int max = (int) v.getOrDefault("max", -999);
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