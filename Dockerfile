# Stage 1 : build
FROM gradle:9.7.1-jdk21 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY src ./src

RUN gradle clean bootJar --no-daemon

# Stage 2 : run
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/catalog-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
