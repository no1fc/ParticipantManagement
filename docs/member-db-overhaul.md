# 회원 DB 전체 정비 계획서

> **작성일:** 2026-05-15  
> **브랜치:** `feature/member-db-overhaul`  
> **우선순위:** 상태 관리 체계화 → 지점 관리 개선 → 권한 세분화 → 보안 강화

---

## 1. 현재 상태 분석

### 1.1 메인 테이블: `J_참여자관리_로그인정보`

| 한글 컬럼명 | 영문 매핑 | 타입 | 용도 |
|---|---|---|---|
| 전담자번호 | memberLoginPK | INT (PK) | 자동증가 기본키 |
| 지점 | memberBranch | NVARCHAR | 소속 지점 |
| 이름 | memberUserName | NVARCHAR | 사용자 이름 |
| 아이디 | memberUserID | NVARCHAR | 로그인 ID |
| 비밀번호 | memberUserPW | NVARCHAR(15) | **평문 저장 (보안 취약)** |
| 권한 | memberRole | NVARCHAR | 역할 (상담, PRA, 파트장 등) |
| 관리자권한 | memberISManager | BIT | 관리자 플래그 |
| 고유번호 | memberUniqueNumber | NVARCHAR | 상담일정 접속용 고유번호 |
| 이메일 | memberEmail | NVARCHAR | 이메일 |
| 전화번호 | memberPhoneNumber | NVARCHAR | 전화번호 |
| 아이디사용여부 | useStatus | NVARCHAR | 사용/잠금/퇴사/정지 **(NULL/빈값 혼재)** |
| 입사일 | memberJoinedDate | DATE | 입사일 |
| 최종발령일 | memberAssignedDate | DATE | 최종 발령일 |
| 근속기간구분 | memberContinuous | NVARCHAR | 근속 분류 |
| 등록일 | memberRegDate | DATE | 등록일 |
| 조회순서 | viewOrder | INT | 표시 순서 |
| 실적작성여부 | performanceWrite | BIT | 실적 작성 권한 |
| 일일업무일지발급여부 | dailyReportIssue | BIT | 일일보고 발급 권한 |
| 배정가중치 | assignWeight | FLOAT | 배정 가중치 |

### 1.2 지점 테이블: `J_참여자관리_지점`

| 한글 컬럼명 | 영문 매핑 | 용도 |
|---|---|---|
| 지점번호 | branchNo | PK |
| 지점 | branchName | 지점명 |
| 지점인원 | branchStaff | 지점 인원수 |
| 유형1 | type1 | (일일업무보고로 이동됨) |
| 유형2 | type2 | (일일업무보고로 이동됨) |
| 사업부 | department | A/B 분류 |
| 순서 | branchOrder | 표시 순서 |

### 1.3 현재 발견된 문제점

| 문제 | 상세 | 위험도 |
|------|------|--------|
| 평문 비밀번호 | `비밀번호` 컬럼에 해싱 없이 저장 | CRITICAL |
| 상태값 불일치 | `아이디사용여부`에 NULL, 빈값, '사용' 혼재 | HIGH |
| 권한 enum 불일치 | MemberRoleCheck에 6개, 실제 사용 10개 | HIGH |
| 셀프 회원가입 없음 | 관리자만 사용자 생성 가능 | MEDIUM |
| 지점 물리 삭제 | 삭제 시 복구 불가, 이력 손실 | MEDIUM |
| SecurityConfig 미사용 | Spring Security 비어있음 | LOW |

### 1.4 현재 권한 체계

**실제 사용 중인 권한 (10개):**
상담, PRA, 파트장, 팀장, 총괄, 차장, 이사, 상무이사, 전무, 대표

**MemberRoleCheck enum (6개, 불일치):**
이사, 차장, 본부장(미사용), 총괄, 팀장, 파트장

**세션 플래그 (3개 Boolean):**

| 플래그 | 조건 | 용도 |
|--------|------|------|
| IS_MANAGER | `관리자권한` BIT = 1 | 전체 관리자 |
| IS_BRANCH_MANAGER | `권한`이 enum에 포함 | 지점 관리자 |
| IS_PRA_MANAGER | `권한` == "PRA" | 배정 관리자 |

**세션 플래그 사용 파일 (18개):**
AdminAccessSupport, LoginController, DashboardMainController, DashBoardPage.jsp,
DashBoardBranchScoreAndSituation.jsp, UpdateController, ParticipantDeleteAjax,
DashboardAjaxController, iapBeforeSaveAjax, BranchManagement, TransferController,
ParticipantAllExcel, PraPageController, ScheduleApiController, gnb.tag, adminGnb.tag

### 1.5 사용자 상태 구분

| 상태 | 용도 | 로그인 |
|------|------|--------|
| 사용 | 정상 활동 | O |
| 정지 | 육아휴직, 장기공가 등 | X |
| 잠금 | 공용계정 접속 차단 | X |
| 퇴사 | 퇴사자 | X |
| NULL/빈값 | 기본값 (정규화 필요) | O |

---

## 2. Phase 1: 상태 관리 체계화

### 2.1 DB 변경 (DDL/DML)

```sql
-- 1) NULL/빈값 → '사용' 정규화
UPDATE J_참여자관리_로그인정보
SET 아이디사용여부 = '사용'
WHERE 아이디사용여부 IS NULL OR 아이디사용여부 = '';

-- 2) NOT NULL 제약조건
ALTER TABLE J_참여자관리_로그인정보
ALTER COLUMN 아이디사용여부 nvarchar(100) NOT NULL;

-- 3) DEFAULT 제약조건
ALTER TABLE J_참여자관리_로그인정보
ADD CONSTRAINT DF_아이디사용여부 DEFAULT '사용' FOR 아이디사용여부;

-- 4) CHECK 제약조건 (승인대기 = 셀프 회원가입용 신규 상태)
ALTER TABLE J_참여자관리_로그인정보
ADD CONSTRAINT CK_아이디사용여부
CHECK (아이디사용여부 IN ('사용', '정지', '잠금', '퇴사', '승인대기'));
```

### 2.2 MyBatis 쿼리 단순화

기존 반복 패턴:
```sql
(아이디사용여부 NOT IN ('퇴사','정지','잠금') OR 아이디사용여부 IS NULL OR 아이디사용여부 = '')
```

변경:
```sql
아이디사용여부 = '사용'
```

**수정 대상:**

| 파일 | 위치 | 변경 내용 |
|------|------|----------|
| `Member-mapping.xml` | `loginSelect` (line 18) | WHERE 조건 단순화 |
| `Dashboard-mapping.xml` | 활성 사용자 필터 전체 | 동일 |
| `Schedule-mapping.xml` | 활성 사용자 필터 | 동일 |

### 2.3 셀프 회원가입 기능

**회원가입 플로우:**
```
사용자 → /register.do 접근 → 회원가입 폼 작성
  → 아이디 중복 체크
  → 제출 → INSERT (아이디사용여부 = '승인대기')
  → 관리자가 사용자 관리 페이지에서 확인
  → 상태를 '사용'으로 변경 → 로그인 가능
```

**신규 파일:**

| 파일 | 경로 | 용도 |
|------|------|------|
| register.jsp | `src/main/webapp/WEB-INF/views/register.jsp` | 회원가입 폼 |
| register JS | `src/main/webapp/js/register_0.0.1.js` | 중복체크, 유효성검증 |
| RegisterController | `src/main/java/.../view/login/RegisterController.java` | 회원가입 컨트롤러 |

**수정 파일:**

| 파일 | 변경 내용 |
|------|----------|
| `Member-mapping.xml` | `registerUser` INSERT 쿼리 추가, `checkUserIdDuplicate` 쿼리 추가 |
| `MemberDAO.java` | `insert()` 메서드 추가 |
| `MemberService.java` | `insertOne()` 메서드 추가 |
| `MemberServiceImpl.java` | `insertOne()` 구현 |
| `AdminApiController.java` | `PUT /admin/api/users/{userNo}/approve` 승인 엔드포인트 추가 |
| `adminUserManagement.jsp` | 상태 드롭다운에 `승인대기`, `정지` 옵션 추가 |
| `login.jsp` | 회원가입 페이지 링크 추가 |
| `WebMvcConfig.java` | `/register.do` 경로 인터셉터 제외 추가 |

### 2.4 검증 항목

- [ ] `사용` 상태 사용자 로그인 성공
- [ ] `정지`/`잠금`/`퇴사`/`승인대기` 상태 사용자 로그인 차단
- [ ] 셀프 회원가입 → 승인대기 → 관리자 승인 → 로그인 가능 전체 플로우
- [ ] 대시보드/스케줄 쿼리 정상 동작

---

## 3. Phase 2: 지점 관리 개선

### 3.1 DB 변경

```sql
-- 1) 소프트 삭제 컬럼 추가
ALTER TABLE J_참여자관리_지점
ADD 사용여부 nvarchar(10) NOT NULL
    CONSTRAINT DF_지점사용여부 DEFAULT '사용';

ALTER TABLE J_참여자관리_지점
ADD CONSTRAINT CK_지점사용여부 CHECK (사용여부 IN ('사용', '미사용'));

-- 2) 지점명 유니크 제약조건
-- 사전 확인: SELECT 지점, COUNT(*) FROM J_참여자관리_지점 GROUP BY 지점 HAVING COUNT(*) > 1
ALTER TABLE J_참여자관리_지점
ADD CONSTRAINT UQ_지점명 UNIQUE (지점);
```

### 3.2 코드 변경

| 파일 | 변경 내용 |
|------|----------|
| `Admin-mapping.xml` | `deleteBranch` → `UPDATE SET 사용여부='미사용'`으로 변경 |
| `Admin-mapping.xml` | `selectBranchList` → `WHERE 사용여부='사용'` 필터 추가 |
| `Admin-mapping.xml` | `selectBranchUserCount` 쿼리 추가 (삭제 전 소속 사용자 수 확인) |
| `AdminDTO.java` | `branchUseStatus` 필드 추가 |
| `AdminServiceImpl.java` | `removeBranch()` 로직 수정 |
| `AdminApiController.java` | 지점 삭제 시 소속 사용자 수 경고 반환 |
| `adminUserManagement.jsp` | 지점 드롭다운에서 `미사용` 지점 제외 |

### 3.3 정책

- `유형1`, `유형2`는 `J_참여자관리_일일업무보고`로 이동됨 → 지점 테이블에서 유지만 하고 관리 불필요
- `사업부`는 A/B로 구분 관리 → 현행 유지
- 지점 삭제(소프트) 시 소속 사용자의 `지점` 값은 그대로 유지 (과거 이력 보존)
- 삭제된 지점은 드롭다운에서 제외, 신규 사용자 배정 불가

### 3.4 검증 항목

- [ ] 지점 소프트 삭제 → 소속 사용자 데이터 유지
- [ ] 삭제된 지점이 드롭다운에서 제외
- [ ] 신규 사용자 등록 시 삭제된 지점 선택 불가
- [ ] 기존 기능 정상 동작 (대시보드, 스케줄 등)

---

## 4. Phase 3: 권한 세분화

### 4.1 DB 변경

```sql
-- 메뉴별 권한 매핑 테이블
CREATE TABLE J_참여자관리_메뉴권한 (
    PK          int IDENTITY(1,1) NOT NULL,
    권한         nvarchar(20) NOT NULL,
    메뉴코드     nvarchar(50) NOT NULL,
    접근레벨     nvarchar(10) NOT NULL
                CONSTRAINT DF_접근레벨 DEFAULT '읽기',
    등록일       datetime DEFAULT GETDATE(),
    CONSTRAINT PK_메뉴권한 PRIMARY KEY (PK),
    CONSTRAINT UQ_권한_메뉴 UNIQUE (권한, 메뉴코드),
    CONSTRAINT CK_접근레벨 CHECK (접근레벨 IN ('읽기', '쓰기', '전체'))
);
```

**메뉴코드 정의:**

| 메뉴코드 | 설명 |
|----------|------|
| DASHBOARD | 대시보드 |
| PARTICIPANT | 참여자 관리 |
| COUNSEL | 상담 관리 |
| MYPAGE | 마이페이지 |
| RANDOM_ASSIGNMENT | 무작위 배정 (PRA) |
| BRANCH_MGMT | 지점 관리 |
| ADMIN_USER | 사용자 관리 (관리자) |
| ADMIN_BRANCH | 지점 관리 (관리자) |
| ADMIN_SETTINGS | 설정 관리 (관리자) |
| REPORT | 보고서 |
| SCHEDULE | 상담일정 |
| JOB_PLACEMENT | 취업알선 |
| RECRUITMENT | 채용정보 |

### 4.2 권한 그룹 체계

```
PermissionGroup.NORMAL         → 상담, PRA
PermissionGroup.BRANCH_MANAGER → 파트장, 팀장, 총괄, 차장, 이사
PermissionGroup.GLOBAL_ADMIN   → 상무이사, 전무, 대표
```

**MemberRoleCheck 확장 (6개 → 10개):**

| 기존 | 변경 | 비고 |
|------|------|------|
| EXECUTIVE_DIRECTOR("이사") | 유지 | BRANCH_MANAGER |
| SENIOR_MANAGER("차장") | 유지 | BRANCH_MANAGER |
| GENERAL_MANAGER("본부장") | **삭제** | JSP 폼에 없음 |
| GENERAL("총괄") | 유지 | BRANCH_MANAGER |
| TEAM_LEADER("팀장") | 유지 | BRANCH_MANAGER |
| PART_LEADER("파트장") | 유지 | BRANCH_MANAGER |
| - | COUNSELOR("상담") **추가** | NORMAL |
| - | PRA("PRA") **추가** | NORMAL |
| - | MANAGING_DIRECTOR("상무이사") **추가** | GLOBAL_ADMIN |
| - | SENIOR_DIRECTOR("전무") **추가** | GLOBAL_ADMIN |
| - | CEO("대표") **추가** | GLOBAL_ADMIN |

### 4.3 코드 변경

**수정 파일:**

| 파일 | 변경 내용 |
|------|----------|
| `MemberRoleCheck.java` | enum 10개로 확장, PermissionGroup enum 추가 |
| `LoginBean.java` | `permissionGroup` 필드 추가 |
| `LoginController.java` | 로그인 시 permissionGroup 세션 저장 |
| `AdminAccessSupport.java` | `hasMenuAccess(session, menuCode)` 메서드 추가 |
| `sql-map-config.xml` | 새 매퍼/DTO 등록 |

**신규 파일:**

| 파일 | 경로 |
|------|------|
| Permission-mapping.xml | `src/main/resources/mappings/Permission-mapping.xml` |
| PermissionDAO.java | `src/main/java/.../biz/adminpage/PermissionDAO.java` |
| PermissionService.java | `src/main/java/.../biz/adminpage/PermissionService.java` |
| PermissionServiceImpl.java | `src/main/java/.../biz/adminpage/PermissionServiceImpl.java` |

### 4.4 하위 호환성 전략

기존 세션 플래그를 사용하는 18개 파일이 있으므로 **점진적 교체**:

| 단계 | 내용 | 시점 |
|------|------|------|
| Phase 3a | 새 권한 인프라 + 기존 플래그 **병행** | 이번 정비 |
| Phase 3b | 18개 파일의 플래그 체크를 `hasMenuAccess()` 호출로 교체 | 후속 작업 |
| Phase 3c | 기존 플래그 제거 | 최종 정리 |

### 4.5 검증 항목

- [ ] 10개 권한 각각 로그인 후 올바른 메뉴 접근
- [ ] 기존 IS_BRANCH_MANAGER/IS_MANAGER/IS_PRA_MANAGER 플래그 정상 동작
- [ ] 관리자 페이지에서 메뉴권한 CRUD

---

## 5. Phase 4: 보안 강화

### 5.1 DB 변경

```sql
-- 비밀번호 컬럼 확장 (BCrypt 해시: 60자 + 여유)
ALTER TABLE J_참여자관리_로그인정보
ALTER COLUMN 비밀번호 nvarchar(72) NOT NULL;
```

### 5.2 의존성 추가

`pom.xml`에 `spring-security-crypto`만 추가 (Spring Security 전체 프레임워크 도입 안 함):

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

**Spring Security 전체를 도입하지 않는 이유:**
- 필터 체인, CSRF, SecurityContext 활성화 → LoginInterceptor 기반 기존 인증 흐름과 충돌
- 18개 파일에서 세션 플래그 직접 참조 → 전면 변경 필요
- `spring-security-crypto`만으로 `BCryptPasswordEncoder` 독립 사용 가능

### 5.3 로그인 플로우 변경

**현재:** SQL에서 비밀번호 비교
```sql
WHERE 아이디=#{memberUserID} AND 비밀번호=#{memberUserPW}
```

**변경:** SQL은 아이디로만 조회, Java에서 BCrypt.matches() 비교
```sql
WHERE 아이디=#{memberUserID} AND 아이디사용여부 = '사용'
```
```java
if (bCryptEncoder.matches(rawPassword, user.getMemberUserPW())) {
    // 로그인 성공
}
```

### 5.4 코드 변경

| 파일 | 변경 내용 |
|------|----------|
| `pom.xml` | `spring-security-crypto` 의존성 추가 |
| `RootConfig.java` | `BCryptPasswordEncoder` 빈 등록 |
| `Member-mapping.xml` | `loginSelect`에서 `AND 비밀번호=?` 제거, 아이디+상태로만 조회 |
| `Member-mapping.xml` | `OneMemberDataSelect` 동일 변경 |
| `MemberServiceImpl.java` | BCryptPasswordEncoder 주입, 로그인/비밀번호 변경에 해싱 적용 |
| `LoginController.java` | 비밀번호 불일치 시 에러 처리 분기 추가 |
| `AdminServiceImpl.java` | `resetPassword()`에서 기본 비밀번호 해싱 후 저장 |
| `Admin-mapping.xml` | `insertUser` password는 서비스에서 해싱 후 전달 |

**신규 파일:**

| 파일 | 용도 |
|------|------|
| `PasswordMigrationRunner.java` | 기존 평문 비밀번호 → BCrypt 일괄 변환 (일회성) |

### 5.5 비밀번호 마이그레이션 배포 순서

```
Step 1: ALTER COLUMN 비밀번호 nvarchar(72)
        → 안전: 컬럼 확장만, 기존 데이터 영향 없음

Step 2: 코드 배포 (평문/BCrypt 동시 지원 모드)
        → password.startsWith("$2a$") 체크로 분기

Step 3: PasswordMigrationRunner 실행
        → 전체 평문 비밀번호 BCrypt 변환

Step 4: 검증 후 다음 릴리스에서 평문 코드 경로 제거
```

### 5.6 검증 항목

- [ ] 마이그레이션 전 평문 비밀번호 로그인 정상
- [ ] 마이그레이션 후 BCrypt 비밀번호 로그인 정상
- [ ] 비밀번호 변경 플로우 정상
- [ ] 관리자 비밀번호 초기화 정상
- [ ] 신규 사용자 등록 시 비밀번호 해싱 확인

---

## 6. 전체 영향도 요약

### 파일별 변경 매트릭스

| 파일 | Phase 1 | Phase 2 | Phase 3 | Phase 4 |
|------|---------|---------|---------|---------|
| Member-mapping.xml | O | | | O |
| Admin-mapping.xml | O | O | | O |
| Dashboard-mapping.xml | O | | | |
| Schedule-mapping.xml | O | | | |
| MemberDAO.java | O | | | |
| MemberService/Impl | O | | | O |
| LoginController.java | | | O | O |
| LoginBean.java | | | O | |
| MemberRoleCheck.java | | | O | |
| AdminAccessSupport.java | | | O | |
| AdminDTO.java | | O | | |
| AdminServiceImpl.java | | O | | O |
| AdminApiController.java | O | O | | |
| adminUserManagement.jsp | O | O | | |
| login.jsp | O | | | |
| WebMvcConfig.java | O | | | |
| RootConfig.java | | | | O |
| pom.xml | | | | O |
| sql-map-config.xml | | | O | |

### 위험도 평가

| Phase | 위험도 | 근거 |
|-------|--------|------|
| Phase 1 | **Low** | 기존 로직과 의미적으로 동일, 추가 기능 |
| Phase 2 | **Low** | 추가적(additive) 변경, 데이터 손실 없음 |
| Phase 3 | **Medium** | enum 변경으로 기존 코드 영향 가능, 하위 호환성 전략으로 완화 |
| Phase 4 | **High** | 인증 핵심 로직 변경, 마이그레이션 실패 시 전체 로그인 불가 |

---

## 7. 신규 테이블 정의서

### J_참여자관리_메뉴권한 (Phase 3)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| PK | INT IDENTITY | PRIMARY KEY | 자동증가 PK |
| 권한 | NVARCHAR(20) | NOT NULL | 역할명 (상담, 팀장 등) |
| 메뉴코드 | NVARCHAR(50) | NOT NULL | 메뉴 식별 코드 |
| 접근레벨 | NVARCHAR(10) | DEFAULT '읽기' | 읽기/쓰기/전체 |
| 등록일 | DATETIME | DEFAULT GETDATE() | 등록 시점 |

**복합 유니크:** (권한, 메뉴코드)
