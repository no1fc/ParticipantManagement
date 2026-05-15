-- ============================================================
-- 회원 DB 전체 정비 DDL 스크립트
-- 작성일: 2026-05-15
-- 브랜치: feature/member-db-overhaul
-- 실행 순서: Phase 1 → Phase 2 → Phase 3 → Phase 4
-- ============================================================

-- ============================================================
-- Phase 1: 상태 관리 체계화
-- ============================================================

-- 1-1) NULL/빈값 → '사용' 정규화
UPDATE J_참여자관리_로그인정보
SET 아이디사용여부 = N'사용'
WHERE 아이디사용여부 IS NULL OR 아이디사용여부 = N'';

-- 1-2) NOT NULL 제약조건
ALTER TABLE J_참여자관리_로그인정보
ALTER COLUMN 아이디사용여부 nvarchar(100) NOT NULL;

-- 1-3) DEFAULT 제약조건
ALTER TABLE J_참여자관리_로그인정보
ADD CONSTRAINT DF_아이디사용여부 DEFAULT N'사용' FOR 아이디사용여부;

-- 1-4) CHECK 제약조건
ALTER TABLE J_참여자관리_로그인정보
ADD CONSTRAINT CK_아이디사용여부
CHECK (아이디사용여부 IN (N'사용', N'정지', N'잠금', N'퇴사', N'승인대기'));

-- ============================================================
-- Phase 2: 지점 관리 개선
-- ============================================================

-- 2-0) 사전 확인: 지점명 중복 체크 (결과가 있으면 UQ 추가 전 정리 필요)
-- SELECT 지점, COUNT(*) FROM J_참여자관리_지점 GROUP BY 지점 HAVING COUNT(*) > 1;

-- 2-1) 소프트 삭제 컬럼 추가
ALTER TABLE J_참여자관리_지점
ADD 사용여부 nvarchar(10) NOT NULL
    CONSTRAINT DF_지점사용여부 DEFAULT N'사용';

ALTER TABLE J_참여자관리_지점
ADD CONSTRAINT CK_지점사용여부 CHECK (사용여부 IN (N'사용', N'미사용'));

-- 2-2) 지점명 유니크 제약조건
ALTER TABLE J_참여자관리_지점
ADD CONSTRAINT UQ_지점명 UNIQUE (지점);

-- ============================================================
-- Phase 3: 권한 세분화
-- ============================================================

-- 3-1) 메뉴별 권한 매핑 테이블 생성
CREATE TABLE J_참여자관리_메뉴권한 (
    PK          int IDENTITY(1,1) NOT NULL,
    권한         nvarchar(20) NOT NULL,
    메뉴코드     nvarchar(50) NOT NULL,
    접근레벨     nvarchar(10) NOT NULL
                CONSTRAINT DF_접근레벨 DEFAULT N'읽기',
    등록일       datetime DEFAULT GETDATE(),
    CONSTRAINT PK_메뉴권한 PRIMARY KEY (PK),
    CONSTRAINT UQ_권한_메뉴 UNIQUE (권한, 메뉴코드),
    CONSTRAINT CK_접근레벨 CHECK (접근레벨 IN (N'읽기', N'쓰기', N'전체'))
);

-- 3-2) 초기 권한 데이터 시드 (필요에 따라 조정)
-- 상담 (일반)
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상담', N'DASHBOARD', N'읽기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상담', N'PARTICIPANT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상담', N'COUNSEL', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상담', N'MYPAGE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상담', N'SCHEDULE', N'읽기');

-- PRA
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'PRA', N'DASHBOARD', N'읽기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'PRA', N'PARTICIPANT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'PRA', N'COUNSEL', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'PRA', N'MYPAGE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'PRA', N'RANDOM_ASSIGNMENT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'PRA', N'SCHEDULE', N'읽기');

-- 파트장
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'파트장', N'DASHBOARD', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'파트장', N'PARTICIPANT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'파트장', N'COUNSEL', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'파트장', N'MYPAGE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'파트장', N'BRANCH_MGMT', N'읽기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'파트장', N'SCHEDULE', N'쓰기');

-- 팀장
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'팀장', N'DASHBOARD', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'팀장', N'PARTICIPANT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'팀장', N'COUNSEL', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'팀장', N'MYPAGE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'팀장', N'BRANCH_MGMT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'팀장', N'SCHEDULE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'팀장', N'REPORT', N'읽기');

-- 총괄
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'총괄', N'DASHBOARD', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'총괄', N'PARTICIPANT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'총괄', N'COUNSEL', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'총괄', N'MYPAGE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'총괄', N'BRANCH_MGMT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'총괄', N'SCHEDULE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'총괄', N'REPORT', N'쓰기');

-- 차장
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'차장', N'DASHBOARD', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'차장', N'PARTICIPANT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'차장', N'COUNSEL', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'차장', N'MYPAGE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'차장', N'BRANCH_MGMT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'차장', N'ADMIN_BRANCH', N'읽기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'차장', N'SCHEDULE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'차장', N'REPORT', N'쓰기');

-- 이사
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'이사', N'DASHBOARD', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'이사', N'PARTICIPANT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'이사', N'COUNSEL', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'이사', N'MYPAGE', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'이사', N'BRANCH_MGMT', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'이사', N'ADMIN_BRANCH', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'이사', N'SCHEDULE', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'이사', N'REPORT', N'쓰기');

-- 상무이사 (전체관리)
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'DASHBOARD', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'PARTICIPANT', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'COUNSEL', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'MYPAGE', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'ADMIN_USER', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'ADMIN_BRANCH', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'ADMIN_SETTINGS', N'읽기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'SCHEDULE', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'상무이사', N'REPORT', N'전체');

-- 전무 (전체관리)
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'DASHBOARD', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'PARTICIPANT', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'COUNSEL', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'MYPAGE', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'ADMIN_USER', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'ADMIN_BRANCH', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'ADMIN_SETTINGS', N'쓰기');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'SCHEDULE', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'전무', N'REPORT', N'전체');

-- 대표 (전체관리 - 모든 권한)
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'DASHBOARD', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'PARTICIPANT', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'COUNSEL', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'MYPAGE', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'ADMIN_USER', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'ADMIN_BRANCH', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'ADMIN_SETTINGS', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'RANDOM_ASSIGNMENT', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'BRANCH_MGMT', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'SCHEDULE', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'REPORT', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'JOB_PLACEMENT', N'전체');
INSERT INTO J_참여자관리_메뉴권한 (권한, 메뉴코드, 접근레벨) VALUES (N'대표', N'RECRUITMENT', N'전체');

-- ============================================================
-- Phase 4: 보안 강화
-- ============================================================

-- 4-1) 비밀번호 컬럼 확장 (BCrypt 해시 60자 + 여유)
ALTER TABLE J_참여자관리_로그인정보
ALTER COLUMN 비밀번호 nvarchar(72) NOT NULL;

-- 4-2) 비밀번호 마이그레이션은 Java 코드(PasswordMigrationRunner)에서 실행
-- BCrypt 해싱은 T-SQL에서 불가하므로 애플리케이션 레이어에서 처리
