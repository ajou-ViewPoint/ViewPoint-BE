# Issue: Spring Boot 테스트 실행 시 환경 변수 로드 실패

## 📋 문제 요약

`./gradlew test` 실행 시 모든 테스트가 `IllegalStateException`으로 실패합니다.

```
org.hibernate.HibernateException at DialectFactoryImpl.java:191
Caused by: Unable to determine Dialect without JDBC metadata 
(please set 'jakarta.persistence.jdbc.url' for common cases or 
'hibernate.dialect' when a custom Dialect implementation must be provided)
```

## 🔍 원인 분석

### 1. dotenv 로드 위치 문제

`ViewpointApplication.java`에서 dotenv를 로드합니다:

```java
public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();
    
    dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
    );
    
    SpringApplication.run(ViewpointApplication.class, args);
}
```

### 2. @SpringBootTest의 동작 방식

**핵심 문제**: `@SpringBootTest`는 `main()` 메서드를 호출하지 않습니다!

- Spring Boot Test는 `SpringApplication.run()`을 직접 호출하지 않음
- `SpringBootContextLoader`를 통해 `ApplicationContext`를 직접 생성
- 따라서 `main()` 내의 dotenv 로드 코드가 실행되지 않음

### 3. 결과

| 항목 | 애플리케이션 실행 | 테스트 실행 |
|------|------------------|-------------|
| dotenv 로드 | ✅ main() 실행 | ❌ main() 미실행 |
| 환경 변수 | ✅ 설정됨 | ❌ 미설정 |
| DB URL | `jdbc:mysql://localhost:3306/viewpoint` | `jdbc:mysql://${MYSQL_HOST}:3306/${MYSQL_DATABASE}` (해석 안됨) |
| 결과 | 정상 연결 | 연결 실패 |

## ✅ 해결 방안

### 방안 1: 테스트용 ApplicationContextInitializer 생성 (권장)

테스트 시작 전에 dotenv를 로드하는 초기화 클래스를 만듭니다.

```java
// src/test/java/.../config/DotenvInitializer.java
public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
```

테스트 클래스에 적용:
```java
@SpringBootTest
@ContextConfiguration(initializers = DotenvInitializer.class)
class AssemblyMemberServiceTest { ... }
```

### 방안 2: build.gradle에서 환경 변수 로드

```groovy
tasks.named('test') {
    useJUnitPlatform()
    
    // .env 파일에서 환경 변수 로드
    def envFile = file('.env')
    if (envFile.exists()) {
        envFile.readLines().each { line ->
            if (line && !line.startsWith('#') && line.contains('=')) {
                def (key, value) = line.split('=', 2)
                environment key.trim(), value.trim()
            }
        }
    }
}
```

### 방안 3: 커맨드 라인에서 환경 변수 전달

```bash
MYSQL_HOST=localhost MYSQL_DATABASE=viewpoint MYSQL_ROOT_PASSWORD=password ./gradlew test
```

## 🎯 권장 해결책

**방안 1 (ApplicationContextInitializer)** 을 권장합니다:

1. 기존 코드 수정 없이 테스트 전용 설정 추가
2. 모든 테스트에서 일관된 환경 변수 로드
3. IDE에서 테스트 실행 시에도 동작

## 📁 관련 파일

- `src/main/java/com/www/viewpoint/ViewpointApplication.java` - dotenv 로드 위치
- `src/main/resources/application.yml` - 환경 변수 참조
- `src/test/resources/application.yml` - 테스트 설정
- `build.gradle` - dotenv-java 의존성

---

# JaCoCo 테스트 커버리지 측정

## 📊 JaCoCo 설정

`build.gradle`에 JaCoCo 플러그인이 추가되었습니다.

### 실행 방법

```bash
# 테스트 실행 + 커버리지 리포트 생성
./gradlew test

# 또는 명시적으로 리포트 생성
./gradlew test jacocoTestReport

# 커버리지 검증 (최소 커버리지 미달 시 빌드 실패)
./gradlew jacocoTestCoverageVerification
```

### 리포트 위치

테스트 실행 후 아래 경로에서 리포트를 확인할 수 있습니다:

| 형식 | 경로 |
|------|------|
| HTML | `build/reports/jacoco/test/html/index.html` |
| XML | `build/reports/jacoco/test/jacocoTestReport.xml` |

### 제외 대상

다음 클래스들은 커버리지 측정에서 제외됩니다:
- `**/model/dto/**` - DTO 클래스
- `**/model/entity/**` - Entity 클래스
- `**/model/request/**`, `**/model/response/**` - Request/Response 클래스
- `**/model/enums/**` - Enum 클래스
- `**/config/**` - 설정 클래스
- `**/exception/**` - 예외 클래스
- `**/*Application*` - 메인 애플리케이션 클래스

### 커버리지 검증 규칙

현재 최소 커버리지 비율은 **0%** 로 설정되어 있습니다.
필요시 `build.gradle`의 `jacocoTestCoverageVerification`에서 `minimum` 값을 조정하세요.

```groovy
limit {
    counter = 'LINE'
    value = 'COVEREDRATIO'
    minimum = 0.70 // 70% 최소 커버리지
}
```
