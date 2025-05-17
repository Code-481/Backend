package com.deu.java.backend.bus.client;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.InputSource;

public class BusanBimsApiClient {

    // ENV
    Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    // API KEY
    private final String API_KEY = dotenv.get("API_KEY");
    private final String BASE_URL = dotenv.get("BASE_URL");
    private final OkHttpClient client = new OkHttpClient();

    // 도착 정보
    public List<BusArrivalDto> fetchArrivalInfo(String stopId) {

        String url = BASE_URL + "/stopArrByBstopid?serviceKey=" + API_KEY + "&bstopid=" + stopId;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String xmlResponse = response.body().string();
                return parseArrivalInfoFromXml(xmlResponse, stopId);
            } else {
                throw new RuntimeException("Failed to fetch arrival info: " + response.message());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching arrival info: " + e.getMessage(), e);
        }
    }

    // XML 파싱하여 도착 정보를 추출
    private List<BusArrivalDto> parseArrivalInfoFromXml(String xmlResponse, String stopId) throws Exception {
        List<BusArrivalDto> arrivals = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlResponse));
        Document doc = builder.parse(is);
        NodeList itemList = doc.getElementsByTagName("item");

        for (int i = 0; i < itemList.getLength(); i++) {
            Node itemNode = itemList.item(i);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element item = (Element) itemNode;
                String busNo = getTagValue("carno1", item);
                String arrivalTimeStr = getTagValue("min1", item);
                long arrivalTime = Long.parseLong(arrivalTimeStr);
                arrivals.add(new BusArrivalDto(busNo, arrivalTime));
            }
        }

        if (arrivals.isEmpty()) {
            throw new RuntimeException("No bus arrival data found for stopId: " + stopId);
        }

        return arrivals;
    }

    // 특정 태그의 값을 반환하는 메소드
    private String getTagValue(String tag, Element element) {
        NodeList nl = element.getElementsByTagName(tag);
        if (nl.getLength() > 0) {
            Node n = nl.item(0);
            return n.getTextContent();
        }
        return "";
    }
}
