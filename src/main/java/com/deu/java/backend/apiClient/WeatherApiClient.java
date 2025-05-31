package com.deu.java.backend.apiClient;

import com.deu.java.backend.Weather.DTO.WeatherWeekDTO;
import com.deu.java.backend.Weather.entity.WeatherTodayEntity;
import com.deu.java.backend.apiClient.compoment.KmaBaseTimeUtil;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

    static Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
    private static final String todayWeatherKey = dotenv.get("TODAY_WEATHER_API_KEY");
    private static final String TODAY_WEATHER_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
    private static final String weekWeatherKey = dotenv.get("WEEK_WEATHER_API_KEY");
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



    public int parseTaWithFallback(Element item, Integer previousTa) {
        String taRaw = getElementTextByTagName(item, "ta");

        if (taRaw == null || taRaw.isBlank()) {
            if (previousTa == null) {
                throw new RuntimeException("ta 값이 비어 있고 대체할 이전 값도 없습니다.");
            }
            System.out.println("[대체값 적용] announceTime=" + getElementTextByTagName(item, "announceTime") + " 의 ta 값이 없어 이전 값 " + previousTa + " 사용함.");
            return previousTa;
        }

        try {
            return Integer.parseInt(taRaw);
        } catch (NumberFormatException e) {
            throw new RuntimeException("ta 값 파싱 실패. raw: " + taRaw);
        }
}

    private String getNearestFcstTime() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(30); // 30분 이내 예보 반영
        int hour = now.getHour();
        int minute = now.getMinute();
        String fcstTime = String.format("%02d00", hour);
        if (minute >= 30) {
            fcstTime = String.format("%02d30", hour);
        }
        return fcstTime;
    }

    private String getTodayStr() {
        return java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public WeatherTodayEntity fetchTodayWeather() {
        try {
            String[] baseDateTime = KmaBaseTimeUtil.getLatestBaseDateTime(LocalDateTime.now());
            String baseDate = baseDateTime[0];
            String baseTime = baseDateTime[1];
            String nx = "97";
            String ny = "75";
            StringBuilder urlBuilder = new StringBuilder(TODAY_WEATHER_URL);
            urlBuilder.append("?ServiceKey=").append(todayWeatherKey);
            urlBuilder.append("&base_date=").append(baseDate);
            urlBuilder.append("&base_time=").append(baseTime);
            urlBuilder.append("&nx=").append(nx);
            urlBuilder.append("&ny=").append(ny);
            urlBuilder.append("&numOfRows=100");
            urlBuilder.append("&dataType=JSON");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();

            String responseStr = sb.toString().trim();
            System.out.println(sb.toString());
            // JSON 응답이 아니면 로그 출력 및 예외 발생
            if (!responseStr.startsWith("{")) {
                System.err.println("API 응답이 JSON이 아님: " + responseStr);
                throw new RuntimeException("기상청 API 응답이 JSON이 아님");
            }

            JSONObject json = new JSONObject(sb.toString());
            JSONArray items = json.getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("item");

            // category별 첫번째 데이터만 저장
            Map<String, JSONObject> firstItemByCategory = new HashMap<>();

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (!item.has("category")) continue;
                String category = item.getString("category");
                // category별로 첫 번째 값만 저장
                if (!firstItemByCategory.containsKey(category)) {
                    firstItemByCategory.put(category, item);
                }
            }

            // 필요한 category만 추출 (예: T1H, SKY, REH)
            String temperatureStr = null, sky = null, cloud = null;
            if (firstItemByCategory.containsKey("T1H")) {
                temperatureStr = firstItemByCategory.get("T1H").getString("fcstValue");
            }
            if (firstItemByCategory.containsKey("SKY")) {
                sky = firstItemByCategory.get("SKY").getString("fcstValue");
            }
            if (firstItemByCategory.containsKey("REH")) {
                cloud = firstItemByCategory.get("REH").getString("fcstValue");
            }

            int temperature = (temperatureStr != null && !temperatureStr.isEmpty()) ? (int) Double.parseDouble(temperatureStr) : -999;
            sky = (sky != null) ? sky : "";
            cloud = (cloud != null) ? cloud : "";

            // 첫 번째 데이터의 fcstDate 사용 (없으면 빈 문자열)
            String targetFcstDate = "";
            if (!firstItemByCategory.isEmpty()) {
                targetFcstDate = firstItemByCategory.values().iterator().next().getString("fcstDate");
            }

            return new WeatherTodayEntity(targetFcstDate, temperature, sky, cloud);

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

                } catch (NumberFormatException ignored) { }

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
            return weatherMap.entrySet().stream().map(e -> {
                LocalDate date = e.getKey();
                Map<String, Object> v = e.getValue();
                int min = (int) v.getOrDefault("min", -999);
                int max = (int) v.getOrDefault("max", -999);
                String sky = (String) v.getOrDefault("sky", "");
                String rain = (String) v.getOrDefault("rain", "");

                return new WeatherWeekDTO(date, min, max, sky, rain);
            }).sorted(Comparator.comparing(WeatherWeekDTO::getDate)).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("주간 날씨 불러오기 실패", e);
        }
    }
}