package com.deu.java.backend.apiClient;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import java.util.*;

public class BusanBimsApiClient {

    Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
    private final String API_KEY = dotenv.get("BUSID_BUSID_API_KEY");
    private final OkHttpClient client = new OkHttpClient();

    public List<BusArrivalDto> fetchArrivalInfo(String bstopid) {
        String url = "http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid?serviceKey=" + API_KEY + "&bstopid=" + bstopid;
        Request request = new Request.Builder().url(url).build();
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

    private List<BusArrivalDto> parseArrivalInfoFromXml(String xmlResponse, String bstopid) throws Exception {
        List<BusArrivalDto> arrivals = new ArrayList<>();
        JSONObject jsonResponse = XML.toJSONObject(xmlResponse);
        JSONObject response = jsonResponse.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        if (body.has("items") && !body.isNull("items")) {
            JSONObject items = body.getJSONObject("items");
            Object itemObj = items.get("item");
            JSONArray itemArray;
            if (itemObj instanceof JSONArray) {
                itemArray = (JSONArray) itemObj;
            } else {
                itemArray = new JSONArray();
                itemArray.put(itemObj);
            }
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);
                String busNo = item.optString("lineno", "");
                Map<String, Object> allData = new HashMap<>();
                java.util.Iterator<?> keys = item.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    allData.put(key, item.get(key));
                }
                boolean isOperating = item.has("min1") && !item.optString("min1", "0").equals("0")
                        && !item.optString("min1", "").equals("운행대기")
                        && !item.optString("min1", "").equals("도착정보없음");
                allData.put("arrivalStatus", isOperating);
                int bstopidx = item.optInt("bstopidx", 0);
                boolean isReverse = (bstopidx > 50);
                allData.put("isReverse", isReverse);
                long arrivalTime = isOperating ? 1L : 0L;
                BusArrivalDto dto = new BusArrivalDto(busNo, arrivalTime, allData);
                arrivals.add(dto);
            }
        }
        if (arrivals.isEmpty()) {
            throw new RuntimeException("No bus arrival data found for stopId: " + bstopid);
        }
        return arrivals;
    }
}
