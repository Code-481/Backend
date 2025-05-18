#Backend
## ENV example
```
JDBC_URL =
JDBC_USER = 
JDBC_PASSWORD = 
BUSID_BUSID_API_KEY = 
```
- `BUSID_BUSID_API_KEY`키는 https://www.data.go.kr/data/15092750/openapi.do 에서 가져오면됨


# 버스장류장 API 작동 원리
1. 서버 실행시 스케줄려에 의해 자동으로 파싱
2. 파싱하고 DB에 저장할때 중복되는 ID가 있으면 덮어쓰기나 없으면 새로 쓰기함
3. 사용자는 DB에 저장되어 있는 값을 읽어서 파싱함함