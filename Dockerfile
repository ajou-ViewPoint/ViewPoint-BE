FROM openjdk:21-jdk-slim
# Gradle Wrapper로 빌드
COPY build/libs/viewpoint-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]