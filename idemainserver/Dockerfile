# 1. 빌드를 위한 베이스 이미지
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon --warning-mode=all --scan -x test

# 2. 빌드된 애플리케이션을 실행하기 위한 베이스 이미지
FROM openjdk:17
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]
