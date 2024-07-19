# Java WAS

2024 우아한 테크캠프 프로젝트 WAS

## 환경
Java 17

## 의존성 주입
- 빈으로 등록할 클래스에 @Component 어노테이션을 부착하여 DI가능
- 생성자 주입 중 파라미터가 인터페이스일 때, @Compnent가 붙은 구현체가 여러개라면 오류 발생 
  - 만약 생성자 파라미터의 네이밍이 구현체 네이밍과 같다면 특정 가능  

## 데이터베이스, 이미지 저장 위치
- {home 디렉토리}/java-was 하위에 저장
  - h2: /db
  - csv: /csv
  - image: /img

### feat/step-9 의 기본 데이터베이스는 csv입니다.
### csv 데이터베이스 문제점
- `,` 문자 사용 불가 (csv파일에서는 "" 문자열을 사용해 이스케이프 처리했지만, 쿼리 파싱 과정에서 오류)
- 테이블 제약 조건 없음 (동일한 id로 회원가입 가능, 먼저 가입한 id로만 로그인 가능)

## H2 데이터베이스로 서버 실행 방법
- `codesquad.app.config.H2DataSourceConfigurer`에 @Component 등록
- `codesquad.db.h2.H2ConnectionPoolManager` 에 @Component 등록
- `codesquad.app.config.CsvDataSourceConfigurer`에 @Component 등록해제
- `codesquad.db.csv.CsvDataSourceManager` @Component 등록해제

## CSV 실행 방법
- `codesquad.app.config.CsvDataSourceConfigurer`에 @Component 등록
- `codesquad.db.csv.CsvDataSourceManager` @Component 등록
- `codesquad.app.config.H2DataSourceConfigurer`에 @Component 등록해제
- `codesquad.db.h2.H2ConnectionPoolManager` 에 @Component 등록해제

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

