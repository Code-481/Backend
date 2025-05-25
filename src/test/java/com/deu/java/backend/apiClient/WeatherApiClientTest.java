package com.deu.java.backend.apiClient;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

class WeatherApiClientTest {
    private final WeatherApiClient client = new WeatherApiClient();

    private Element createElementFromString(String xmlFragment) throws Exception {
        String xml = "<root>" + xmlFragment + "</root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
        return doc.getDocumentElement();
    }

    @Test//ta값 잘 불러오는지
    public void testParseTaWithValidValue() throws Exception {
        Element element = createElementFromString("<ta>25</ta><announceTime>202305251200</announceTime>");
        int result = client.parseTaWithFallback(element, 20);
        assertEquals(25, result);
    }

    @Test//ta값 없을 때, 잘 이전 값을 잘 쓰는지
    public void testParseTaWithEmptyValueReturnsPrevious() throws Exception {
        Element element = createElementFromString("<ta/><announceTime>202305251300</announceTime>");
        int result = client.parseTaWithFallback(element, 20);
        assertEquals(20, result);
    }

    @Test//대처할 ta 값이 없을 때
    public void testParseTaWithEmptyAndNullPreviousThrows() throws Exception {
        Element element = createElementFromString("<ta/><announceTime>202305251600</announceTime>");
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            client.parseTaWithFallback(element, null);
        });
        assertTrue(thrown.getMessage().contains("대체할 이전 값도 없습니다"));
    }

}