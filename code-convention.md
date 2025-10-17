# Code Convention

DDD 패턴 Layerd Architecture

```java
src/main/java/com/example
│
├── global
│   └── infrastructure
│       └── config
│           ├── security
│           │   └── SecurityConfig.java
│           └── schedule
│               └── ScheduleConfig.java
│
├── article
│   ├── application
│   │   └── service
│   │       └── ArticleServiceV1.java
│   │
│   ├── domain
│   │   ├── entity
│   │   │   └── ArticleEntity.java
│   │   └── repository
│   │       └── ArticleRepository.java
│   │
│   ├── infrastructure
│   │   ├── api
│   │   │   └── gemini
│   │   │       ├── dto
│   │   │       │   └── response  
│   │   │       │       └── ResGeminiPostGenerateContentDto.java
│   │   │       └── client
│   │   │           └── GeminiClient.java
│   │   ├── repository
│   │   │   └── article
│   │   │       └── ArticleRepositoryImpl.java (생략가능)
│   │   └── schedule
│   │       └── articleScheduler.java
│   │
│   └── presentation
│       ├── controller
│       │   └── ArticleControllerV1.java
│       ├── advice
│       │   └── GlobalExceptionHandler.java
│       └── dto
│           ├── request
│           │   └── ReqArticleGetByIdDtoV1.java
│           └── response
│               └── ResArticleGetByIdDtoV1.java
│
├── user
│   ├── application
│   │   └── service
│   │       └── UserServiceV1.java
│   │
│   ├── domain
│   │   ├── entity
│   │   │   └── UserEntity.java
│   │   └── repository
│   │       └── UserRepository.java
│   │
│   ├── infrastructure
│   │   └── repository
│   │       └── user
│   │           └── UserRepositoryImpl.java (생략가능)
│   │
│   └── presentation
│       ├── controller
│       │   └── UserControllerV1.java
│       ├── advice
│       │   └── GlobalExceptionHandler.java
│       └── dto
│           ├── request
│           │   └── ReqUserGetByIdDtoV1.java
│           └── response
│               └── ResUserGetByIdDtoV1.java
│       
└── AppApplication.java
```

# GitHub Rule
### **깃허브 규칙**

- 브렌치 전략
    - main: 제품 출시 브랜치
    - dev: 출시를 위해 개발하는 브랜치
    - feat/{기능명}: 새로운 기능 개발하는 브랜치
    - refactor/{기능명}: 개발된 기능을 리팩터링하는 브랜치
    - fix: 출시 버전에서 발생한 버그를 수정하는 브랜치
    - release/{버전}: 배포 가능한 브렌치

### 깃헙 커밋 규칙

```java
[티켓번호] 티켓명 또는 기능명

- 상세 내용 1
- 상세 내용 2
```

| 작업 타입 | 작업내용 |
| --- | --- |
| ✨ feat | 해당 파일에 새로운 기능이 생김 |
| 🎉 init | 없던 파일을 생성함, 초기 세팅 |
| 🐛 bugfix | 버그 수정 |
| ♻️ refactor | 코드 리팩토링 |
| 🩹 fix | 코드 수정 |
| 🚚 move | 파일 옮김/정리 |
| 🔥 del | 기능/파일을 삭제 |
| 🍻 test | 테스트 코드를 작성 |
| 💄 style | css |
| 🙈 gitfix | gitignore 수정 |
| 🔨script | package.json 변경(npm 설치 등) |
| 🧹chore | 관리 등 핵심 내용이 아닌 작업 |
