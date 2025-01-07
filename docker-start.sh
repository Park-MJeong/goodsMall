#!/bin/bash

# 환경 변수 파일 지정
ENV_FILE="prod.env"

# DB 서비스 시작
echo "Starting database services..."
docker-compose -f docker-compose-db.yml --env-file $ENV_FILE up -d

# 잠시 대기 (데이터베이스 서비스가 완전히 시작될 때까지)
echo "Waiting for database services to be ready..."
sleep 10

# 애플리케이션 서비스 시작
echo "Starting application services..."
docker-compose -f docker-compose-app.yml --env-file $ENV_FILE up -d

echo "All services have been started!" 