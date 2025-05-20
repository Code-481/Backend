#Backend
## ENV example
```
JDBC_URL =
JDBC_USER = 
JDBC_PASSWORD = 
BUSID_BUSID_API_KEY = 
```
`BUSID_BUSID_API_KEY`키는 https://www.data.go.kr/data/15092750/openapi.do 에서 가져오면됨


## GET /api/v1/univ/foods?place={name}
place -> "hyomin", "happy", "information", "suduck" <br/>
중하나 입력하면 데이터 표시, 아무것도 입력안하면 4가지 모든게 다뜸

### [기숙사] Example API
```
{
"date": "2025-05-19",
"dormType": "happy",
"food_menu": "낙지김치죽 / 동그랑떙&케찹 / 동원양반김 / 그린빈스굴소스볶음 / 추가밥&쥬시쿨주스 / 깍두기",
"getMealType": "breakfast"
},
```
getMealType으로 아침 점심 저녁으로 구분 하면됨됨

### [학식] Example API
```
{
"date": "2025-05-23",
"dormType": "suduck",
"food_menu": "여러가지",
"getMealType": "lunch"
}
```
-> food_menu 푸드 메뉴쪽에 코너가 다뜨는 오류 있습 수정중

### GET /api/v1/bus/stop/arrival?stopId={id}
버스정류장 ID 입력 하면 데이터 반환 \n
파라미터에 all이라는 값을 넣으면 정류장별로 정리 없이 바로 리스트로 뽑아줌


# 버스장류장 API 작동 원리
1. 서버 실행시 스케줄려에 의해 자동으로 파싱
2. 파싱하고 DB에 저장할때 중복되는 ID가 있으면 덮어쓰기나 없으면 새로 쓰기함
3. 사용자는 DB에 저장되어 있는 값을 읽어서 파싱함
4. 버스 데이터는 오전 5시 30분분 부터 오후 11시 10분까지 가져옴
