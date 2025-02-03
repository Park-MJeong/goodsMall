# GoodsMall
## **📖 목차**

1. [🚀 프로젝트 소개](#-프로젝트-소개)
2. [⚒️  스택](#-스택)
3. [💡 기술적 의사 결정](#-기술적-의사-결정)
4. [📐 System Architecture](#-system-architecture)
5. [📖 ERD](#-erd)
6. [📌 주요 서비스](#-주요-서비스)
7. [🐞 트러블 슈팅 및 성능 개선](#-트러블-슈팅-및-성능-개선)

## 🚀 프로젝트 소개

GoodsMall은 **MSA 기반**으로 설계된 eCommerce 플랫폼으로, 한정판 굿즈의 **선착순 구매**를 안정적으로 지원합니다. **Redis**와 **Kafka**를 활용해 **대규모 트래픽**과 **동시성 이슈**를 해결할 수 있도록 설계되었습니다
## ⚒️  스택

**Languages & Frameworks**

![Java](https://img.shields.io/badge/java21-333333?style=flat&logo=OpenJDK&logoColor=white)
![springboot](https://img.shields.io/badge/-springboot3.3-333333?style=flat&logo=springboot)
![springsecurity](https://img.shields.io/badge/-springsecurity-333333?style=flat&logo=springsecurity)
![springdataJPA](https://img.shields.io/badge/-springDataJPA-333333?style=flat&logo=spring)

**Data & Distributed Systems**

![MySQL](https://img.shields.io/badge/-MySQL-333333?style=flat&logo=mysql)
![Redis](https://img.shields.io/badge/-Redis-333333?style=flat&logo=Redis)
![Kafka](https://img.shields.io/badge/-Kafka-333333?style=flat&logo=apachekafka)


**DevOps & Testing**

![Docker](https://img.shields.io/badge/-Docker-333333?style=flat&logo=docker)
![K6](https://img.shields.io/badge/-k6-333333?style=flat&logo=k6)
![grapana](https://img.shields.io/badge/grafana-333333.svg?style=flat&logo=grafana&logoColor)




## 💡 기술적 의사 결정
### Frameworks

**Spring Boot**
- **자동 설정(Auto Configuration)**: 공통 설정을 자동화하여 개발 속도 향상
- **내장 웹서버 제공**: 별도의 외장 서버 없이 빠르게 실행 가능
- **실행 가능한 JAR 지원**: 간편한 배포 및 실행으로 프로덕션 환경에서도 효율적
- **생산성 향상**: 설정 간소화로 비즈니스 로직에 집중 가능
---
### Data & Distributed Systems
**MySQL** 과 **Redis**의 역할 분담

- Redis
  - **조회 성능 향상**: 캐싱을 통해 데이터 조회 속도 최적화
  - **동시성 제어 및 데이터 일관성 보장**: 원자적 명령어(DECR, INCR)와 Redisson 분산 락을 활용하여 실시간 트래픽이 많은 상품 재고를 안정적으로 관리
- MySQL
  - **영구적 데이터 저장**: 데이터의 장기 보관 및 안정적인 저장소
  - **트랜잭션 처리**: 데이터 무결성과 신뢰성을 보장하는 트랜잭션 관리

**Kafka**
- **처리량**: 초당 수백만건 이상의 메시지 처리 가능
- **데이터 영속성**: 로그기반으로 데이터를 디스크에 저장,복제기능으로 데이터 유실 최소화
- **실시간 비동기 이벤트 처리**: 서비스 간 비동기 통신을 통해 결합도를 낮추고, 높은 동시성을 요구하는 환경에서도 효율적으로 작동
- **확장성**: 분산 아키텍처로 설계뙤어 노드 추가를 통해 무한 확장가능
---
### DevOps & Testing
**Docker**
- **처리량**: 초당 수백만건 이상의 메시지 처리 가능
- **데이터 영속성**: 로그기반으로 데이터를 디스크에 저장,복제기능으로 데이터 유실 최소화
- **실시간 비동기 이벤트 처리**: 서비스 간 비동기 통신을 통해 결합도를 낮추고, 높은 동시성을 요구하는 환경에서도 효율적으로 작동
- **확장성**: 분산 아키텍처로 설계되어 노드 추가를 통해 무한 확장가능

**k6 & grafana**
- **nGrinder 대체**: nGrinder 사용 시 JVM 기반으로 리소스가 부족해 성능 테스트가 원활하지 않았던 한계 해결
- **Grafana 통합**: K6는 GUI를 지원하지 않기 떄문에 보완하기 위해 Grafana를 통해 테스트 결과를 시각적으로 모니터링

## 📐 System Architecture
![선착순구매 아키텍처](https://github.com/user-attachments/assets/f0a610d8-d35f-42d1-9e6b-34ed9c1b4f09)

##  📖 ERD
<img width="663" alt="reboot_선착순구매" src="https://github.com/user-attachments/assets/bf6f34e2-3562-4229-9695-8fc165613bc3" />

## 📌 주요 서비스
[ 🔗 API명세서 (Postman)](https://documenter.getpostman.com/view/38751432/2sAYJ7eyB6)

### 주요 서비스별 정리

**User-Service**
- **사용자 관리**: 회원가입, 로그인, 비밀번호 변경
- **JWT 인증**: 사용자 인증 및 토큰 관리
- **Google SMTP를 사용한 이메일 인증**: 신뢰성이 높고 쉽게 구현할 수 있는 Google SMTP 도입

**Product-Service**
- **상품 조회 및 검색**: 커서 기반 페이징과 Redis 캐싱을 활용해 상품 검색 속도와 성능 최적화
- **재고 관리**: Redis 캐싱을 활용해 실시간 재고 상태를 빠르게 조회하고 데이터 일관성 유지


**Order-Service**
- **Kafka를 활용한 주문 처리**: 주문 생성, 결제, 재고 반영의 속도 및 효율성 개선
- **스케줄러를 통한 주문 및 배송 상태 관리**: 주문 및 배송 상태의 주기적인 업데이트

**Payment-Service**
- **결제 처리**: 주문 결제 요청 처리
- **보상 트랜잭션**: 결제 실패 시 재고 복구 및 주문 상태 취소를 포함한 **SAGA 패턴 기반** 보상 트랜잭션 구현
- **원자적 재고감소**: Redis와 Lua Script를 활용해 재고감소 원자적으로 처리
---



[//]: # (## 💡기술적 의사결정)

[//]: # (  - )

[//]: # (  - 선착순 구매 시스템의 재고 확보 및 동시성 제어)

[//]: # (  - 장바구니 상품구매와 상세페이지에서 상품구매의 구매 및 결제 로직)

[//]: # (  - )

## 🐞 트러블 슈팅 및 성능 개선
### ✏️ 재고관리 및 동시성 처리에서 Lua 스크립트와 Redisson 분산락 성능 차이
<img alt="chart" src="https://github.com/user-attachments/assets/96fa1e72-d5e0-41c8-862d-195d62b0ba1a" />

**🚩 Lua Script 기반 처리 사용**
- **Lua Script 기반 처리**시 평균 응답 시간: 181.26ms → 41.6ms (77.05% 감소),
  TPS (초당 요청 수): 548.46 → 2344.18 (327.5% 증가)
- 애플리케이션 레벨의 락 관리 없이 Redis에서 요청을 순차적으로 처리 가능
- 요청 누락 감소 및 일관된 성능 유지 가능

### ✏️ 레디스 캐싱을 이용한 제품 상세조회 성능 개선
<img alt="chart" src="https://github.com/user-attachments/assets/54857877-496b-407c-90b5-734b1c81a897" />

- 문제 상황 : 상세페이지 접근시 매번 쿼리문이 날아감, 동시접속자가 많아지면  속도 저하 및 오류발생
- 해결 방법 : **Redis 캐싱**과 **TTL을 적용**하여 불필요한 DB 조회를 줄이고, **Look-Aside 패턴**을 활용해 캐시 미스 시 DB에서 데이터를 가져와 저장하도록 설계, 동시 접속 증가 시에도 안정적인 성능 보장



### ✏️ 이메일 인증코드 발송지연 속도 개선

- 문제 상황 : 이메일 인증코드 발송 시 서버의 응답 시간이 14초로 길어, **사용자 이탈률 증가**가 우려됨<br>
  SMTP 서버가 이메일을 처리하고 성공 여부를 응답할 때까지, 스프링 서버가 대기함으로 발생

- 해결 방법 : 기존 트랜잭션에 묶여있던 이메일 전송 작업을 분리<br>
  비동기 처리를 도입하여 SMTP 서버의 응답을 기다리지 않고 작업이 완료되도록 개선
- 서버 응답 시간이 14s에서 400ms로 단축되며, 사용자 이탈률 감소 효과를 기대할 수 있음


### ✏️ Kafka를 이용한 주문 - 결제 - 재고반영 속도 개선

[//]: # ([ 🔗 자세히 보기]&#40;https://github.com/Park-MJeong/goodsMall/wiki/Kafka%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%A3%BC%EB%AC%B8-%E2%80%90-%EA%B2%B0%EC%A0%9C-%E2%80%90-%EC%9E%AC%EA%B3%A0%EB%B0%98%EC%98%81-%EC%86%8D%EB%8F%84-%EA%B0%9C%EC%84%A0-%EB%B0%8F-Saga%ED%8C%A8%ED%84%B4-%EC%A0%81%EC%9A%A9&#41;)
- 문제 상황 : 주문-결제-재고 반영 작업이 순차적으로 이루어져, 한 단계에서 지연이 발생하면 전체 프로세스가 지연되는 성능 병목 현상이 발생
- 해결 방법 : Kafka 기반 비동기 메시징을 도입하여 각 작업을 독립적으로 수행하도록 설계, 대규모 트래픽 처리 환경에서도 안정적인 성능 보장


### ✏️ 이메일 인증코드 발송지연 속도 개선

- 문제 상황 : 이메일 인증코드 발송 시 서버의 응답 시간이 14초로 길어, **사용자 이탈률 증가**가 우려됨<br>
  SMTP 서버가 이메일을 처리하고 성공 여부를 응답할 때까지, 스프링 서버가 대기함으로 발생
- 해결 방법 : 기존 트랜잭션에 묶여있던 이메일 전송 작업을 분리<br>
  비동기 처리를 도입하여 SMTP 서버의 응답을 기다리지 않고 작업이 완료되도록 개선
- 서버 응답 시간이 14s에서 400ms로 단축되며, 사용자 이탈률 감소 효과를 기대할 수 있음


### ✏️ 모놀리식에서 MSA로 변환 후 ErrorException 처리시 500에러 통합 표시
- 문제 상황 : 모놀리식 환경에서 발생한 ErrorException이 500 에러로 통합되어 표시되고, 원인 모듈 정보가 로그에만 출력됨
- 해결 방법 : ErrorDecoder 도입: MSA 환경에 맞춰 ErrorException을 각 모듈에서 처리하도록 분리<br>
  에러 원인과 상태를 HTTP 응답으로 명확히 전달하여 디버깅 및 문제 해결 효율성 강화
