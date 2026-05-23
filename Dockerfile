# 1. 자바 17 실행 환경 이미지 저장
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너 내부로 복사
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 4. 스프링 부트 서버 실행 명령
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]