# 🏛️ ViewPoint Backend

> **대한민국 국회 정보 플랫폼 API 서버**  
> 국회의원, 법안, 정당, 위원회 등 국회 관련 데이터를 제공하는 RESTful API 서비스

---

## 📋 목차

- [프로젝트 소개](#-프로젝트-소개)
- [기술 스택](#-기술-스택)
- [주요 기능](#-주요-기능)
- [시작하기](#-시작하기)
- [API 문서](#-api-문서)
- [프로젝트 구조](#-프로젝트-구조)
- [환경 변수](#-환경-변수)

---

## 🎯 프로젝트 소개

**ViewPoint**는 대한민국 국회의 투명성을 높이기 위한 시민 참여 플랫폼입니다.  
국회의원 정보, 법안 현황, 정당별 의석 분포, 위원회 구성 등 다양한 국회 데이터를 직관적으로 제공합니다.

### 핵심 가치
- 📊 **데이터 기반** - 국회 공공데이터를 가공하여 의미 있는 정보 제공
- 🗺️ **위치 기반** - 좌표/지역 기반 지역구 국회의원 조회
- 🤖 **AI 연동** - Gemini API를 활용한 뉴스 분석 및 이미지 생성
- 📈 **정치 분석** - NOMINATE, Wordfish 등 정치학적 분석 데이터 제공

---

## 🛠 기술 스택

### Backend
| 기술 | 버전 | 설명 |
|------|------|------|
| **Java** | 21 | 프로그래밍 언어 |
| **Spring Boot** | 3.3.1 | 웹 프레임워크 |
| **Spring Data JPA** | - | ORM |
| **Spring WebFlux** | - | 리액티브 웹 클라이언트 |
| **Lombok** | - | 보일러플레이트 코드 감소 |

### Database & Infra
| 기술 | 설명 |
|------|------|
| **MySQL** | 관계형 데이터베이스 |
| **Docker** | 컨테이너화 |
| **Docker Compose** | 멀티 컨테이너 오케스트레이션 |

### External APIs & Libraries
| 기술 | 설명 |
|------|------|
| **Gemini API** | AI 텍스트/이미지 생성 |
| **Toss Payments** | 결제 처리 |
| **Kakao API** | 지역 정보 조회 |
| **JTS (LocationTech)** | 지리 공간 데이터 처리 |
| **Jsoup & Selenium** | 웹 크롤링 |

### Documentation & Testing
| 기술 | 설명 |
|------|------|
| **Springdoc OpenAPI** | Swagger API 문서 자동 생성 |
| **JaCoCo** | 테스트 커버리지 분석 |
| **JUnit 5** | 단위/통합 테스트 |

---

## ✨ 주요 기능

### 🧑‍💼 국회의원 API (`/v1/assemblymembers`)
- 전체 국회의원 목록 조회 (페이지네이션, 정렬)
- 대수(회기)별 국회의원 조회
- 복합 필터 검색 (이름, 대수, 정당)
- 의원별 발의 법안 및 투표 기록 조회

### 📜 법안 API (`/v1/bills`)
- 전체 법안 목록 조회
- 키워드, 발의일, 대수, 정당, 처리결과 기반 필터 검색
- 법안 제안자 목록 조회
- 법안별 투표 결과 요약 (찬성/반대/기권/불참)

### 🏛️ 정당 API (`/v1/parties`)
- 전체 정당 목록 조회
- 정당별 소속 의원 목록
- 대수별 정당 의석 통계 (지역구/비례대표)

### 📋 위원회 API (`/v1/committees`)
- 전체 위원회 목록 조회
- 위원회 상세 (구성원 + 정당별 분포 통계)

### 🗺️ 선거구 API (`/v1/constituencies`)
- **좌표 기반 조회** - 위도/경도로 해당 지역 국회의원 조회
- **지역 코드 기반 조회** - 시도/시군구/행정동 코드로 조회
- 랜덤 지역 코드 반환

### 💰 후원 & 결제 API (`/v1/donations`, `/v1/payments`)
- 후원 생성 및 내역 조회
- Toss Payments 결제 연동

### 📰 뉴스 API (`/api/news`)
- Google 뉴스 크롤링
- Gemini AI를 활용한 뉴스 요약 및 이미지 생성
- 일별 뉴스 캐싱

### 📊 정치 분석 API
- **NOMINATE** (`/v1/nominate`) - 회기별 의원 이념 좌표
- **Wordfish** (`/v1/wordfish`) - 위원회별 의원 발언 분석

### 🔍 통합 검색 (`/v1/main/search`)
- 법안, 국회의원, 위원회 통합 키워드 검색

---

## 🚀 시작하기

### 사전 요구사항
- Java 21+
- MySQL 8.0+
- Docker (선택)

### 로컬 실행

```bash
# 1. 저장소 클론
git clone https://github.com/your-repo/viewpoint-be.git
cd viewpoint-be/viewpoint

# 2. 환경 변수 설정 (.env 파일 생성)
cp .env.example .env
# .env 파일 편집하여 필요한 값 설정

# 3. 빌드
./gradlew build

# 4. 실행
./gradlew bootRun
```

### Docker 실행

```bash
# 1. 빌드
./gradlew bootJar

# 2. Docker Compose 실행
docker-compose up -d
```

애플리케이션은 `http://localhost:8080`에서 실행됩니다.

---

## 📖 API 문서

애플리케이션 실행 후 아래 URL에서 Swagger UI를 통해 API 문서를 확인할 수 있습니다:

| 문서 | URL |
|------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

---

## 📁 프로젝트 구조

```
src/main/java/com/www/viewpoint/
├── ViewpointApplication.java          # 메인 애플리케이션
├── global/                            # 전역 설정 및 예외 처리
│   ├── config/                        # CORS, Swagger 설정
│   └── exception/                     # 전역 예외 핸들러
├── assemblymember/                    # 국회의원 도메인
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── model/
│       ├── entity/
│       └── dto/
├── bill/                              # 법안 도메인
├── party/                             # 정당 도메인
├── committee/                         # 위원회 도메인
├── constituency/                      # 선거구 도메인
├── donation/                          # 후원/결제 도메인
├── main/                              # 메인 화면, 뉴스, 검색
│   ├── controller/
│   ├── service/
│   │   ├── GeminiClient.java          # Gemini API 클라이언트
│   │   ├── GoogleNewsCrawlerService.java
│   │   └── TopicNewsService.java
│   └── scheduler/                     # 뉴스 스케줄러
├── Rdata/                             # 정치 분석 데이터 (R 분석 결과)
│   ├── controller/
│   │   ├── NominateController.java
│   │   └── WordfishMemberThetaController.java
│   └── service/
└── share/                             # 공유 DTO
```

---

## ⚙️ 환경 변수

프로젝트 루트에 `.env` 파일을 생성하고 다음 환경 변수를 설정합니다:

```env
# Database
MYSQL_HOST=localhost
MYSQL_DATABASE=viewpoint
MYSQL_ROOT_PASSWORD=your_password

# Gemini AI
GEMINI_API_KEY=your_gemini_api_key
GEMINI_IMAGE_API_KEY=your_gemini_image_api_key

# Kakao
KAKAO_API_KEY=your_kakao_api_key

# Toss Payments
TOSS_SECRET_KEY=your_toss_secret_key
```

---

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 테스트 커버리지 리포트 생성
./gradlew test jacocoTestReport

# 커버리지 리포트 확인
open build/reports/jacoco/test/html/index.html
```

---

## 📊 테스트 커버리지

JaCoCo를 통해 테스트 커버리지를 측정합니다.  
DTO, Entity, Config, Exception 클래스는 커버리지 측정에서 제외됩니다.

---

## 📝 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

---

