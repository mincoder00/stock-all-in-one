# 스탁올인원 (Stock All In One)

![홈화면](./img/홈화면.png)


"당신의 투자에 필요한 모든 정보를 한번에, [STOCK ALL IN ONE](https://bit.ly/3zxUeRe)" <br>
스탁올인원은 투자자들을 위한 종합 주식정보 제공 플랫폼입니다. 주식 투자에 필요한 정보를 한 곳에서 제공합니다.

## 주요 기능
1. **뉴스검색**
   - 홈화면에서 키워드를 입력하여 뉴스 검색 가능
   - 네이버 뉴스 검색 API 활용
   - 키워드를 포함하는 가장 최신 50개 뉴스 제공

2. **지수정보**
   - 한국투자증권 API 활용
   - 국내 주요지수, 해외 주요지수 정보 제공

3. **주가조회**
   - 한국투자증권 API 활용
   - 상단바에서 종목코드(예: 005930, TSLA 등) 입력하면 현재가를 포함한 시세, 재무 정보 제공
   
4. **실시간채팅**
   - 다른 투자자들과 실시간으로 소통할 수 있는 채팅 기능을 제공
   - Websocket으로 구현
   - Ws 프로토콜

5. **네이버금융 & 인베스팅닷컴**
   - 유용한 외부 사이트 링크 연계

* [구체적인 기능, 화면은 pdf를 참고하세요.](https://github.com/mincoder00/STOCK-ALL-IN-ONE/blob/main/detail.pdf)

## 기술 스택
- Backend: Java, Springboot
- Frontend: Bootstrap 기반 (ChatGpt, Claude 활용하여 작성)
- API: [한국투자증권 API](https://apiportal.koreainvestment.com/apiservice/oauth2#L_5c87ba63-740a-4166-93ac-803510bb9c02), [네이버 뉴스검색 API](https://developers.naver.com/docs/serviceapi/search/news/news.md#%EB%89%B4%EC%8A%A4)
- 채팅: Websocket
- 배포: AWS Elastic Beanstalk

## 프로젝트 회고
- 프로젝트 기간: *2024/09/23 ~ 2024/10/06*
- 프로젝트 내용: 단순 CRUD를 벗어나서 API, 채팅과 같은 조금 더 고차원(?)의 서비스를 구현해보고 싶어서 혼자 진행한 프로젝트였습니다. 완벽한 결과물은 아니지만, 기능들을 하나씩 추가해가며 목표한 웹사이트를 만들어가는 과정이 재밌었습니다.
- 깨달은 점: 다른 레퍼런스에서 API 코드나, 구현 예제를 가져오더라도 하나씩 뜯어보면서 그것들을 이해해야한다! 그래야 문제점이 생기면 해결하고, 추가적인 기능을 구현하는 것이 가능하다.
- 아쉬운 점: 혼자 진행하다보니 UI/UX에 대한 아쉬움, API 구현이 완벽하지 않아 잦은 500 에러 발생(한투 API 호출 안되는 경우가 있음), 복합적인 API 활용이 가능했더라면 더 유용한 정보를 제공하는 서비스가 되지 않았을까 하는 아쉬움들이 있습니다. 

## Reference
- 웹소켓: https://koopi.tistory.com/25
- 한투+스프링부트: http://yellow.kr/blog/?p=6225
- 배포 (도서): [스프링부트3 백엔드 개발자 되기](https://product.kyobobook.co.kr/detail/S000201766024)