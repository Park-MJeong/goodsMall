# 선착순 구매 프로젝트
## 🚀 프로젝트 소개

GoodsMall은 한정판 굿즈를 포함한 다양한 상품을 구매할 수 있는 eCommerce 플랫폼입니다.
특히, 특정 시간대에만 판매되는 한정판 굿즈를 구매할 수 있는 기능을 중심으로 설계되었습니다.  

📅 프로젝트 기간 
        
2024년 12월 18일 ~ 2025년 1월

---

## 📌 주요기능
[ 🔗 ERD설계 ](https://github.com/Park-MJeong/goodsMall/wiki/%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4-%EC%84%A4%EA%B3%84)

[ 🔗 API명세서 (Postman)](https://documenter.getpostman.com/view/38751432/2sAYJ7eyB6)
![선착순구매 아키텍처](https://github.com/user-attachments/assets/3e826d7b-9297-45f4-b60c-006ed402b577)

### 주요 기능별 정리

1. **User-Service**:
  - **사용자 관리**: 회원가입, 로그인, 비밀번호 변경
  - **JWT 인증**: 사용자 인증 및 토큰 관리
  - **이메일 인증**: 이메일 발송 및 인증 코드 검증
2. **Product-Service**:
  - **상품 조회 및 검색**: 상품 정보를 페이징과 필터링으로 제공
3. **Payment-Service**:
  - **결제 처리**: 주문에 대한 결제 요청 처리
---

## ⚒️  스택

**Backend**

![Java](https://img.shields.io/badge/java-333333?style=flat&logo=OpenJDK&logoColor=white)
![springboot](https://img.shields.io/badge/-springboot-333333?style=flat&logo=springboot)
![springsecurity](https://img.shields.io/badge/-springsecurity-333333?style=flat&logo=springsecurity)
![MySQL](https://img.shields.io/badge/-MySQL-333333?style=flat&logo=mysql)
![JPA](https://img.shields.io/badge/-JPA-333333?style=flat&logo=JPA)
![Redis](https://img.shields.io/badge/-Redis-333333?style=flat&logo=Redis)
![Kafka](https://img.shields.io/badge/-Kafka-333333?style=flat&logo=apachekafka)


**DevOps & Testing**

![GitHub](https://img.shields.io/badge/-GitHub-333333?style=flat&logo=github)
![Postman](https://img.shields.io/badge/-Postman-333333?style=flat&logo=postman)
![Docker](https://img.shields.io/badge/-Docker-333333?style=flat&logo=docker)
![K6](https://img.shields.io/badge/-k6-333333?style=flat&logo=k6)
![grapana](https://img.shields.io/badge/grafana-333333.svg?style=flat&logo=grafana&logoColor)

### 기술적 의사결정
| 기술 | 비교대상 | 선택이유                                                                                     |
| --- | --- |------------------------------------------------------------------------------------------|
| MSA | Monolithic | 유연성, 확장성, 장애 격리 가능. API Gateway 및 분산 트랜잭션 등 추가 기술 활용                                     |
| SpringBoot 3.3 & Java21 |  | 최신 기술 도입으로 성능 향상,  LTS 버전으로 유지보수 용이                                                      |
| Redis |  | 빠른 데이터 접근, 분산 락 활용, 데이터 일관성 및 캐싱 기능 제공                                                   |
| Kafka | RabbitMQ | 모듈 간 낮은 결합도, 실시간 이벤트 처리, 보상 트랜잭션 지원                                                      |
| MySQL | MariaDB,PostgerSQL | 읽기 성능이 우수하며, 캐싱을 위한 Redis와의 조합으로 성능을 극대화 가능, 설정 및 관리가 상대적으로 간단하여 초기 개발 및 운영 비용을 줄일 수 있음. |

**MySQL** 과 **Redis**의 역할 분담
- Redis
  - 실시간 데이터(캐싱) 및 동시성 제어
  - 실시간 트래픽이 많은 상품 재고 처리
- MySQL
    - 영구적 데이터 저장 및 트랜잭션 처리

