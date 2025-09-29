FROM openjdk:21-jdk-slim

WORKDIR /app

# 필수 패키지 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    unzip \
    curl \
    bash \
 && rm -rf /var/lib/apt/lists/*

# 프로젝트 복사
COPY . .

# Gradle Wrapper로 빌드
RUN ./gradlew clean build -x test

# 2단계: 런타임
FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/build/libs/viewpoint-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]