# Java WAS

2024 우아한 테크캠프 프로젝트 WAS

## 환경
Java 17


## 엔드포인트
- /*.index.html로 요청하면 특수 문법을 적용한 페이지(동적 HTML)가 파싱되지 않습니다! 개선 예정

### path: / method: GET
- 홈 화면
- 로그인 전: 회원가입, 로그인 버튼 표시
- 로그인 후: 로그인, 회원가입 버튼 표시, 로그인 사용자 이름 표시

### path: /registeration method: GET
- 회원가입 화면

### path: /login method: GET
- 로그인 화면

### path: /logout method: GET
- 로그아웃 요청

### path: /user/create method: POST
- 회원가입 요청

### path: /user/list method: GET
- 로그인 시 접속 가능
- 회원가입 된 사용자 리스트 출력

