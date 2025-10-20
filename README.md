# SpartaEats - 음식 주문 관리 서비스
> **IT-6 Team Project @SpartaClub**

> 배달의민족, 쿠팡이츠와 같은 음식 주문 및 결제 관리 시스템  
> Spring Boot 기반 백엔드 서버로,  
> 회원가입 → 주소 등록 → 장바구니 → 주문 → 결제 → 리뷰까지의 전체 주문 플로우를 제공합니다.

---

## 1. 팀원 소개

| 이름 | 역할 | 주요 담당 |
|------|------|------------|
| **태성원 (팀장)** | 인증 / 인가 | JWT 기반 로그인, 회원가입, 권한 관리, 주문상태이력, 전역 보안 설계 |
| **진경천** | 주문, 주소, AI, CI/CD | 주문 생성/조회, 리뷰 요약, 답글 추천, 배달거리 계산, 자동 배포 파이프라인 |
| **이효선** | 상품(Item) | 상품 등록, 수정, 삭제(Soft Delete), 조회 API |
| **박정훈** | 매장(Store) | 매장 등록, 조회, 수정, 삭제, 카테고리 관리 |
| **김한빈** | 결제(Payment) | 결제 요청/승인/취소, 결제 로그, 트랜잭션 일관성, 글로벌 코드 관리 |
| **설정아** | 장바구니(Cart) | 장바구니 CRUD, 옵션/수량 관리, 주문과의 연결 처리 |

---

## 2. 산출물

### 2-1. ERD

- **테이블명 규칙:** 모든 테이블은 `p_` 접두사를 사용
- **PK:** UUID (단, `user_id`는 VARCHAR(20))
- **Soft Delete:** 주요 테이블에 `deleted_at`, `deleted_by` 존재

#### 주요 엔티티 관계 요약

- `p_user` ↔ `p_orders` (1:N)
- `p_orders` ↔ `p_payment` (1:1)
- `p_orders` ↔ `p_order_status_history` (1:N)
- `p_cart` ↔ `p_cart_item` ↔ `p_item` (1:N:N 구조)
- `p_review` ↔ `p_review_image` (1:N)
- `p_store` ↔ `p_item` (1:N)

#### 핵심 테이블
- **p_orders:** 주문 스냅샷 기반의 주문 원장
- **p_payment:** 결제 내역, IdempotencyKey 포함
- **p_order_status_history:** 상태 변경 이력 (Actor ID + Role 저장)
- **p_cart_item_option:** 옵션 조합 해시로 중복 방지
- **p_user:** Soft Delete + Role 기반 접근제어

> (ERD Cloud 이미지 혹은 링크 위치)

---

### 2-2. 서비스 아키텍처

#### **구조:** Layered + Port/Adapter Style
```
com.spartaeats
│
├── global/ # 예외 처리, 공통 응답, 시큐리티 설정
├── user/ # 회원가입, 로그인, 권한 관리
├── address/ # 주소 CRUD, 기본 배송지, 거리 계산
├── store/ # 매장 등록, 조회, 카테고리 관리
├── item/ # 상품 등록, 수정, 삭제
├── cart/ # 장바구니 관리
├── order/ # 주문 생성, 조회, 상태 변경, 이력 관리
├── payment/ # 결제 생성, 승인, 취소, 로그 관리
├── review/ # 리뷰 작성, 답글, 요약
└── ai/ # Gemini API 연동 (상품 설명, 리뷰 요약)
```
**특징**
- 각 도메인은 Controller → Service → Repository의 3계층 구조로 분리
- 비즈니스 복잡도가 높은 Payment, Order는 domain / application / infrastructure로 세분화
- `global` 패키지에서 예외, 응답, JWT 필터, Validator를 통합 관리
- CI/CD는 GitHub Actions 기반 자동 배포로 구현

---

### 2-3. API Docs

- **문서화 도구:** Swagger 3.0 (SpringDoc OpenAPI)
- **Swagger UI:** [http://sparta-eats.p-e.kr/swagger-ui/index.html#/]

**주요 엔드포인트**
- `/v1/auth/**` : 회원가입, 로그인
- `/v1/users/**` : 사용자 관리 (조회/수정/삭제)
- `/v1/address/**` : 주소 CRUD 및 거리계산
- `/v1/stores/**` : 매장 관리
- `/v1/items/**` : 상품 CRUD
- `/v1/cart/**` : 장바구니
- `/v1/orders/**` : 주문 생성, 조회, 상태 변경
- `/v1/payments/**` : 결제 요청, 승인, 취소, 로그
- `/v1/reviews/**` : 리뷰 작성, 요약, 답글

---

## 3. 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.6 |
| ORM / DB | Spring Data JPA, QueryDSL, PostgreSQL |
| Build Tool | Gradle (Groovy DSL) |
| Container | Docker, Docker Compose |
| Authentication | Spring Security + JWT |
| CI/CD | GitHub Actions + AWS EC2 + GHCR |
| AI API | Google Gemini 1.5 Flash (REST API 연동) |
| Map / Geo API |  Kakao Maps, TMap API |
| OAuth / Social Login API |  |
| Payment API | Toss Payments API |
| Test | JUnit5, Spring Boot Test |
| Docs | Swagger (Springdoc OpenAPI) |
| Version Control | Git, GitHub Organization (main / feature / fix / refactor) |

---

## 4. 서비스 구성 및 실행 방법

### 실행 전 준비

#### 1️⃣ 환경변수 설정 (`.env` 또는 `application-dev.yml`)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sparta_eats
    username: 
    password: 
jwt:
  secret: your_secret_key
ai:
  api-key: your_gemini_key
```

#### 2️⃣ Docker Compose로 PostgreSQL 실행
```bash
docker-compose up -d
```
#### 3️⃣ Gradle 빌드
```bash
./gradlew clean build
```
#### 4️⃣ Spring Boot 실행
```bash
java -jar build/libs/SpartaEats-0.0.1-SNAPSHOT.jar
```

## 5. 주요 기능 요약
### 💳 결제 (Payment)

- 결제 생성 → 승인 → 취소의 3단계 플로우

- IdempotencyKey로 중복 결제 방지

- PaymentLog로 요청/응답 전문 기록

- 결제–주문 상태 동기화 (markPaymentPaid, markPaymentCanceled)

### 🧾 주문 (Order)

- 장바구니 기반 주문 생성

- 금액/주소/옵션 스냅샷으로 데이터 정합성 보장

- OrderStatusHistory로 모든 상태 전이 추적

- QueryDSL로 조건 검색 및 페이지네이션

- 고객 5분 내 취소 제한 로직 구현

### 👤 회원 / 인증

- JWT 기반 Stateless 인증

- @PreAuthorize로 역할 기반 접근 제어 (CUSTOMER / OWNER / MANAGER / MASTER)

- 회원가입 즉시 로그인 토큰 발급

- Soft Delete로 데이터 보존

### 📍 주소 (Address)

- CRUD + 기본 배송지 설정

- Kakao / TMap API를 이용한 좌표 변환 및 거리 계산

- @Modifying 벌크 업데이트로 기본 배송지 갱신 성능 최적화

### 🤖 AI

- Gemini API 기반 리뷰 요약, 상품 설명 자동 생성

- WebClient 비동기 호출(@Async)로 서비스 응답성 향상

### 🚀 CI/CD

- GitHub Actions + Docker + AWS EC2 자동 배포

- Secrets 관리로 민감정보 보호

- main 브랜치 병합 시 자동 테스트 → 빌드 → 배포 파이프라인 구축

## 6. 프로젝트를 통해 배운 점

- **협업 프로세스의 중요성**

  Git Flow와 Code Review를 통해 팀 전체 코드 품질을 유지했습니다.

- **실제 서비스 관점의 설계**

  단순 CRUD를 넘어 상태 전이, 스냅샷, 멱등성 등 실무적인 설계를 경험했습니다.

- **확장성과 유연성**

  각 도메인을 독립된 모듈로 유지해 추후 마이크로서비스 전환이 가능합니다.
- **배포 자동화 경험**

  CI/CD 구축을 통해 DevOps 프로세스를 직접 경험했습니다.

## 7. 프로젝트 정보

- **팀명**: IT-6

- **과정**: 스파르타클럽 단기심화 Java 4기  (2025)

- **프로젝트 기간**: 2025.09.25 ~ 2025.10.20

- **깃허브 조직**: SpartaEats
