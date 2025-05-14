package com.deu.java.backend.bus.client;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
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
    public BusArrivalDto fetchArrivalInfo(String stopId) {
        
        String url = BASE_URL + "/stopArrByBstopid?serviceKey=" + API_KEY + "&bstopid=" + stopId;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String xmlResponse = response.body().string();
                return parseArrivalInfoFromXml(xmlResponse);
            } else {
                throw new RuntimeException("Failed to fetch arrival info: " + response.message());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching arrival info: " + e.getMessage(), e);
        }
    }

    // XML 파싱하여 도착 정보를 추출
    private BusArrivalDto parseArrivalInfoFromXml(String xmlResponse) throws Exception {
        // XML 파서 설정
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlResponse));  // StringReader로 XML 문자열 파싱
        Document doc = builder.parse(is);  // XML 파싱

        // <response> -> <body> -> <items> -> <item> 순으로 접근
        NodeList itemList = doc.getElementsByTagName("item");

        if (itemList.getLength() > 0) {
            Node itemNode = itemList.item(0);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element item = (Element) itemNode;

                // 필요한 데이터 추출
                String busNo = getTagValue("carno1", item);  // 첫 번째 버스 번호
                String arrivalTimeStr = getTagValue("min1", item);  // 첫 번째 버스의 도착 시간 (분)

                // 도착 시간이 문자열이라면 숫자로 변환
                long arrivalTime = Long.parseLong(arrivalTimeStr);

                // BusArrivalDto 객체 생성 후 반환
                return new BusArrivalDto(busNo, arrivalTime);
            }
        }

        // 데이터가 없으면 예외를 던짐
        throw new RuntimeException("No bus arrival data found for the given stop ID.");
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