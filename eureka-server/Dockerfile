FROM gradle:jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle :eureka-server:build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/eureka-server/build/libs/*.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]