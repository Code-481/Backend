#Backend

<details>
<summary>📡 외부 API 확장 방법 (ApiClient 구조)</summary>

### ✅ 구조적 개념

> 새로운 API 경로를 사용하고 싶다면,  
> `BusanBimsApiClient`에 대응되는 함수만 추가하면 됩니다.

- 기본 `BASE_URL`은 `.env`에서 관리
- 각 함수는 API path, 파라미터, XML 파싱 로직만 다르게 작성
- 서비스 계층에서 해당 함수만 호출하면 됨

---

### 📁 예시: `/getBusLocation` 경로 추가 시

#### 1. `.env` 확인  
BASE_URL=https://apis.data.go.kr/busan/bims

API_KEY=your_encoded_key

#### 2. ApiClient에 함수 추가  (URL과 return값만 바꾸면 됨.)
```java
public BusLocationDto fetchBusLocation(String routeId) { \
    String url = BASE_URL + "/getBusLocation?serviceKey=" + API_KEY + "&routeid=" + routeId;
    
    Request request = new Request.Builder().url(url).build();
    try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
            String xmlResponse = response.body().string();
            return parseBusLocationFromXml(xmlResponse);
        } else {
            throw new RuntimeException("API 호출 실패: " + response.message());
        }
    } catch (Exception e) {
        throw new RuntimeException("API 호출 중 오류: " + e.getMessage(), e);
    }
}
```


#### 3. XML 파싱 함수 추가 (xml 파싱, 원하는 정보 추출(getTagValue사용), 기호에 맞게 예외 처리 후 정보 추출)
```java
private BusLocationDto parseBusLocationFromXml(String xml) throws Exception {
    // DocumentBuilderFactory -> XML → DTO 로직 구현
    // e.g., busNo, latitude, longitude 등 파싱
}
```

#### 4. 서비스 계층에서 호출
BusLocationDto location = apiClient.fetchBusLocation("12345");
