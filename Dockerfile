FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/user-registration-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
