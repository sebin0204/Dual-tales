# 📚 Dual Tales — Backend

> 다문화 가정 아이와 부모가 함께 만드는 **AI 이중언어 맞춤형 동화 서비스**
> GDG on Campus SWU · 2026 Impact Up 프로젝트 · 🌱 SDGs 4 양질의 교육 / 🤝 SDGs 10 불평등 감소

| 항목 | 내용 |
|---|---|
| 기간 | 2026.04 ~ 2026.05 |
| 역할 | **백엔드 개발 전담** (설계 · API 구현 · AI 서버 연동 · 배포) |
| 기여 | 전체 57커밋 중 **49커밋** (+2,348 / −294 lines) |
| 원본 레포 | [GDG-SWU/2026-Tales-Runner-ImpactUp-new-](https://github.com/GDG-SWU/2026-Tales-Runner-ImpactUp-new-) (GDG on Campus SWU 조직) |

<br>

## 📖 서비스 소개

부모와 아이가 함께 인터뷰에 답하면, AI가 아이의 나이·관심사를 반영한 동화를 **한국어 + 부모 모국어** 두 언어로 생성하고 삽화까지 그려 줍니다.
언어 차이로 생기는 교육 격차를 줄이고, 동화를 함께 읽으며 가족 간 대화를 늘리는 것이 목표입니다.

<br>

## 🛠 Tech Stack

| 분류 | 기술 |
|---|---|
| Language / Framework | Java 17, Spring Boot 4.0.6, Spring Data JPA |
| Security | Spring Security, JWT (jjwt 0.11.5), BCrypt |
| Database | MySQL (Google Cloud SQL) |
| Infra / Deploy | Docker, Google Cloud Build, Cloud Run |
| API Docs | Swagger UI (springdoc-openapi 2.6.0) |
| 연동 | FastAPI AI 서버 (Gemini 2.5 Flash, Imagen 4) |

<br>

## 🏗 Architecture

```
Android App (Kotlin)
        │  REST + JWT
        ▼
Spring Boot Backend ──── HTTP ───► FastAPI AI Server ──► Gemini / Imagen 4
        │
        ▼
   Cloud SQL (MySQL)          ※ Backend: Cloud Build → Cloud Run 배포
```

<br>

## 🙋 내가 한 일 (커밋 기록 기반)

### 1. 프로젝트 세팅 & 도메인 설계 `04.29`
- Spring Boot 프로젝트 초기 세팅, README 및 **ERD 작성**
- ERD 기반 `User`, `Story`, `StoryContent` 엔티티·Repository 구현

### 2. 회원 인증 `04.29 ~ 05.04`
- 회원가입 / 로그인 API, **BCrypt 비밀번호 암호화**
- `JwtTokenProvider`, `JwtAuthenticationFilter`, `CustomUserDetailService`로 **JWT 인증** 구성
- Swagger(OpenAPI) 설정 및 JWT 인증 헤더 연동

### 3. 동화 CRUD `04.30 ~ 05.05`
- 언어별 공개 동화 피드(최신순), 내 동화 목록, 동화 상세(이중언어 본문 + 삽화) 조회
- 동화 최종 저장, 삭제, 제목 수정, 공개 여부 변경 (작성자 권한 검증)
- `page_count`, `question_ko`, `question_foreign` 필드 추가

### 4. 인터뷰 기반 동화 생성 흐름 (StoryDraft) `05.14 ~ 05.21`
- 질문/응답 API와 **중간 저장(StoryDraft)** 기능으로 멀티턴 인터뷰 상태 관리
- AI 서버가 넘겨주는 `story_state`를 JSON으로 직렬화해 DB에 저장·복원
- `AiClient` 인터페이스 → `MockAiClient`로 먼저 개발 후 `GeminiAiClient`로 교체
- AI가 `isFinal`을 반환하면 최종 동화를 요청하고 `Story` / `StoryContent`로 변환해 저장, 사용한 Draft 삭제

### 5. 배포 & 연동 안정화 `05.23 ~ 05.26`
- Dockerfile, `cloudbuild.yaml` 작성 → **Google Cloud Run 배포**
- 빌드 환경 이슈 해결 (Java 17 런타임, Gradle 8.14), **Cloud SQL Socket Factory** 의존성 추가
- 배포 후 CORS / SecurityConfig 수정, AI 서버 URL·헤더·엔드포인트 수정
- AI 서버와 요청/응답 규격 통일 (`StoryDraftService` 매핑 흐름 정리)

### 6. 모노레포 통합 `05.26 ~ 05.27`
- 백엔드 레포를 `git subtree`로 `dual-tales-backend/`에 합치고 AI·Android 폴더 구조 세팅, README 정리

<br>

## 🔍 API

| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/users/signup` | 회원가입 |
| POST | `/api/users/login` | 로그인 (JWT 발급) |
| POST | `/api/stories/draft` | 인터뷰 시작 · 첫 질문 생성 |
| PATCH | `/api/stories/draft/{draftId}` | 답변 전송 → 다음 질문 or 최종 동화 생성 |
| POST | `/api/stories` | 동화 최종 저장 |
| GET | `/api/stories/my` | 내 동화 목록 |
| GET | `/api/stories/{storyId}` | 동화 상세 (이중언어 본문 + 삽화) |
| GET | `/api/stories/feed` | 언어별 공개 동화 피드 (최신순) |
| DELETE | `/api/stories/{storyId}` | 동화 삭제 |
| PATCH | `/api/stories/{storyId}/title` | 제목 수정 |
| PATCH | `/api/stories/{storyId}/public` | 공개 여부 변경 |

<br>

## 📁 구조

```
dual-tales-backend/src/main/java/com/example/dual_tales
├── api          # Controller, DTO (user / story / story_content)
├── domain       # Entity, Repository (user / story / story_content / story_draft)
├── service      # UserService, StoryService, StoryDraftService, AiService
└── global       # config(Security, OpenAPI), security(JWT), infrastructure/ai(AI 클라이언트)
```

<br>

## 💡 배운 점
- AI 서버가 늦게 준비되는 상황에서 **인터페이스 + Mock 구현**으로 먼저 개발하고 나중에 실제 구현으로 바꾸는 방식의 장점을 경험했습니다.
- 외부 서버와 연동할 때는 **요청/응답 규격(필드명, 대소문자, 타입)을 먼저 합의**해야 디버깅 시간을 줄일 수 있다는 것을 배웠습니다.
- Cloud Run + Cloud SQL 배포 과정에서 빌드 환경, 시크릿 주입, CORS 등 실제 배포 이슈를 직접 해결해 봤습니다.
