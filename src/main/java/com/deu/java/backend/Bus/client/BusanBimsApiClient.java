package com.deu.java.backend.Bus.client;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import java.util.Map;

public class BusanBimsApiClient {

    // ENV
    Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    // API KEY
    private final String API_KEY = dotenv.get("BUSID_BUSID_API_KEY");
    private final OkHttpClient client = new OkHttpClient();

    // 도착 정보
    public List<BusArrivalDto> fetchArrivalInfo(String bstopid) {

        String url = "http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid?serviceKey=" + API_KEY + "&bstopid="
                + bstopid;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String xmlResponse = response.body().string();
                return parseArrivalInfoFromXml(xmlResponse, bstopid);
            } else {
                throw new RuntimeException("Failed to fetch arrival info: " + response.message());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching arrival info: " + e.getMessage(), e);
        }
    }

    // XML 파싱하여 정보 추출
    private List<BusArrivalDto> parseArrivalInfoFromXml(String xmlResponse, String bstopid) throws Exception {
        List<BusArrivalDto> arrivals = new ArrayList<>();

        try {
            // XML을 JSON으로 변환
            JSONObject jsonResponse = XML.toJSONObject(xmlResponse);

            // JSON 구조 탐색
            JSONObject response = jsonResponse.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");

            // items가 배열인지 단일 객체인지 확인
            Object itemObj = items.get("item");
            JSONArray itemArray;
            if (itemObj instanceof JSONArray) {
                itemArray = (JSONArray) itemObj;
            } else {
                // 단일 항목인 경우 배열로 변환
                itemArray = new JSONArray();
                itemArray.put(itemObj);
            }

            // 각 항목 처리
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);

                // 필요한 데이터 추출
                String busNo = item.optString("lineno", "");

                // min1 키가 있는지 확인
                if (item.has("min1")) {
                    item.put("arrivalStatus", "운행중");
                    Map<String, Object> allData = new HashMap<>();
                    java.util.Iterator<String> keys = item.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        allData.put(key, item.get(key));
                    }
                } else {
                    item.put("min1", 0);
                    item.put("arrivalStatus", "도착 정보가 제공되지 않습니다");

                    // item의 모든 데이터를 Map으로 변환
                    Map<String, Object> allData = new HashMap<>();
                    java.util.Iterator<String> keys = item.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        allData.put(key, item.get(key));
                    }

                    arrivals.add(new BusArrivalDto(busNo, 0, allData));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML to JSON: " + e.getMessage(), e);
        }

        if (arrivals.isEmpty()) {
            throw new RuntimeException("No bus arrival data found for stopId: " + bstopid);
        }

        return arrivals;
    }
}
