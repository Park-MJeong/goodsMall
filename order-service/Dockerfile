FROM gradle:jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle :order-service:build -x test --no-daemon

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /app/order-service/build/libs/*.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]
