# Spring JDBC로 만드는 암기 카드 앱

JDBC 표준부터 Spring JDBC, 트랜잭션, Spring Boot, Spring Data JDBC, [Spring JDBC Plus](https://github.com/naver/spring-jdbc-plus)까지,
데이터 액세스 기술을 한 층씩 쌓아 올리며 암기 카드 앱 **Flashcard**를 완성하는 책의 원고와 예제 코드 저장소입니다.

## 구성

```
book/       # Asciidoc 원고 (예제 코드는 examples/의 소스를 include로 참조)
examples/   # 장별 예제 (Gradle 멀티 프로젝트, 전부 빌드/테스트 가능)
```

| 장 | 모듈 | 내용 |
|----|------|------|
| 1장 | `ch01-jdbc-basics` | JDBC 표준 API: DataSource, Connection, Statement, ResultSet, 트랜잭션, 배치 |
| 2장 | `ch02-jdbc-template` | JdbcTemplate, NamedParameterJdbcTemplate, RowMapper, 동적 SQL |
| 3장 | `ch03-transaction-template` | PlatformTransactionManager, TransactionTemplate |
| 4장 | `ch04-transaction-aop` | @Transactional, 프록시, rollbackFor 함정 |
| 5장 | `ch05-boot-jdbc` | spring-boot-starter-jdbc, H2 콘솔, Thymeleaf 웹 앱(기본판) |
| 6장 | `ch06-data-jdbc` | Spring Data JDBC: 애그리거트, 리포지토리, @Query, 낙관적 잠금 |
| 7장 | `ch07-jdbc-plus` | Spring JDBC Plus, Flashcard 완성판(SRS, 오늘 복습 큐, 스마트 덱, 통계, CSV) |
| 8장 | `ch08-kotlin` | Kotlin 팁: data class, 확장 함수, nullable 타입 |

## 요구 사항

- JDK 25
- (원고 렌더링 시) Asciidoctor

DB는 H2를 사용하며 별도 설치가 필요 없습니다.
파일 모드 + `AUTO_SERVER=TRUE`(auto mixed mode)라서 앱을 띄운 채 H2 콘솔로 같은 DB에 접속할 수 있습니다.

## 예제 실행

```bash
cd examples

./gradlew build                      # 전체 빌드 + 테스트

./gradlew :ch01-jdbc-basics:run      # 1~4장, 8장: 콘솔 예제
./gradlew :ch05-boot-jdbc:bootRun    # 5~7장: 웹 앱 (http://localhost:8080)
./gradlew :ch07-jdbc-plus:bootRun    # 완성판 Flashcard

./gradlew :ch05-boot-jdbc:h2         # H2 자체 콘솔 실행
```

웹 앱 실행 중 H2 웹 콘솔: `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:./db/flashcard;AUTO_SERVER=TRUE`, 사용자: `sa`, 비밀번호 없음)

## 원고 빌드

```bash
cd book
./build.sh          # build/book.html 생성
```
