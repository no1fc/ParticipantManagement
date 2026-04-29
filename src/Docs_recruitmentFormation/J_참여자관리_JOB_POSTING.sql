-- ============================================================
-- JOB_POSTING 테이블 DDL (MSSQL Server)
-- 고용24 Open API 채용공고 동기화 저장소 (2단계)
-- 작성일: 2026-03-30
-- 테이블명: J_참여자관리_JOB_POSTING
-- ============================================================

-- 테이블 존재 시 삭제 후 재생성 (개발 환경 전용)
-- IF OBJECT_ID('dbo.J_참여자관리_JOB_POSTING', 'U') IS NOT NULL
--     DROP TABLE dbo.J_참여자관리_JOB_POSTING;

CREATE TABLE dbo.J_참여자관리_JOB_POSTING (
    -- ── 식별 / 기업 ──────────────────────────────────────────────
                                         wanted_auth_no    VARCHAR(50)      NOT NULL,             -- [PK] 구인인증번호
                                         company_nm        NVARCHAR(100)    NOT NULL,             -- 회사명
                                         biz_reg_no        VARCHAR(20)      NULL,                 -- 사업자등록번호
                                         ind_tp_nm         NVARCHAR(100)    NULL,                 -- 업종명
    -- ── 직무 / 조건 ──────────────────────────────────────────────
                                         recrut_title      NVARCHAR(300)    NOT NULL,             -- 채용제목
                                         jobs_cd           NVARCHAR(12)     NULL,                 -- 직종코드
                                         jobs_nm           NVARCHAR(100)    NULL,                 -- 직종명 (카드 표시용)
                                         emp_tp_cd         INT              NULL,                 -- 고용형태코드 (10=정규직, 20=계약직 등)
                                         emp_tp_nm         NVARCHAR(100)    NULL,                 -- 고용형태명 (카드 표시용)
                                         sal_tp_nm         NVARCHAR(20)     NULL,                 -- 임금형태 (연봉/월급/시급/일급)
                                         sal_desc          NVARCHAR(100)    NULL,                 -- 급여 상세 텍스트
                                         min_sal           INT              NULL,                 -- 최소임금액
                                         max_sal           INT              NULL,                 -- 최대임금액
                                         min_edubg         NVARCHAR(50)     NULL,                 -- 최소학력
                                         max_edubg         NVARCHAR(50)     NULL,                 -- 최대학력
                                         career            NVARCHAR(50)     NULL,                 -- 경력 (신입/경력/관계없음)
                                         holiday_tp_nm     NVARCHAR(50)     NULL,                 -- 근무형태 (주5일근무 등)
    -- ── 근무지 ───────────────────────────────────────────────────
                                         region_nm         NVARCHAR(100)    NULL,                 -- 근무지역 요약
                                         zip_cd            VARCHAR(10)      NULL,                 -- 우편번호
                                         strt_nm_addr      NVARCHAR(200)    NULL,                 -- 도로명주소
                                         basic_addr        NVARCHAR(200)    NULL,                 -- 기본주소
                                         detail_addr       NVARCHAR(200)    NULL,                 -- 상세주소
    -- ── 시스템 / 메타 ────────────────────────────────────────────
                                         reg_dt            DATE             NULL,                 -- 공고 등록일
                                         close_dt          DATE             NULL,                 -- 공고 마감일
                                         info_svc          NVARCHAR(50)     NULL,                 -- 정보제공처 (워크넷 등)
                                         wanted_info_url   VARCHAR(1000)    NULL,                 -- 워크넷 채용정보 URL
                                         mobile_info_url   VARCHAR(1000)    NULL,                 -- 모바일 URL
                                         smodify_dtm       DATETIME         NULL,                 -- API 최종수정일시
    -- ── 자체 관리 컬럼 ───────────────────────────────────────────
                                         sync_dtm          DATETIME         NOT NULL DEFAULT GETDATE(), -- 마지막 DB 동기화 일시
                                         is_active         BIT              NOT NULL DEFAULT 1,         -- 활성여부 (삭제 처리 후 레거시 컬럼, 추후 제거 예정)
                                         created_at        DATETIME         NOT NULL DEFAULT GETDATE(), -- 최초 INSERT 일시
    -- ── 상세정보 수집 관리 ─────────────────────────────────────
                                         detail_fetched      BIT              NOT NULL DEFAULT 0,       -- 상세정보 수집 여부 (0=미수집, 1=완료)
                                         sync_dtm_detail     DATETIME         NULL,                     -- 상세정보 동기화 일시
    -- ── 기업 상세 정보 (corpInfo 영역) ───────────────────────
                                         reper_nm            NVARCHAR(100)    NULL,                     -- 대표자명
                                         tot_psncnt          INT              NULL,                     -- 근로자수
                                         capital_amt         BIGINT           NULL,                     -- 자본금
                                         yr_sales_amt        BIGINT           NULL,                     -- 연매출액
                                         busi_cont           NVARCHAR(MAX)    NULL,                     -- 주요사업내용
                                         home_pg             NVARCHAR(500)    NULL,                     -- 회사 홈페이지
                                         busi_size           NVARCHAR(50)     NULL,                     -- 회사규모
    -- ── 상세 구인 조건 (wantedInfo 영역) ─────────────────────
                                         recrut_peri         NVARCHAR(100)    NULL,                     -- 채용기간
                                         recruit_cnt         INT              NULL,                     -- 모집인원
                                         rel_jobs_nm         NVARCHAR(500)    NULL,                     -- 관련직종
                                         job_cont            NVARCHAR(MAX)    NULL,                     -- 직무내용
                                         for_lang            NVARCHAR(200)    NULL,                     -- 외국어 사항
                                         major               NVARCHAR(200)    NULL,                     -- 전공
                                         certificate         NVARCHAR(500)    NULL,                     -- 자격면허
                                         mltsvc_exc_hope     NVARCHAR(50)     NULL,                     -- 병역특례 채용희망
                                         comp_abl            NVARCHAR(200)    NULL,                     -- 컴퓨터 활용능력
                                         pref_cond           NVARCHAR(MAX)    NULL,                     -- 우대조건
    -- ── 전형 및 접수 방법 ─────────────────────────────────────
                                         sel_mthd            NVARCHAR(500)    NULL,                     -- 전형방법
                                         rcpt_mthd           NVARCHAR(500)    NULL,                     -- 접수방법
                                         submit_doc          NVARCHAR(MAX)    NULL,                     -- 제출서류
                                         etc_hope_cont       NVARCHAR(MAX)    NULL,                     -- 기타 안내사항
    -- ── 근로 조건 및 복리후생 ─────────────────────────────────
                                         near_line           NVARCHAR(200)    NULL,                     -- 인근전철역
                                         work_time_cont      NVARCHAR(MAX)    NULL,                     -- 근무시간 상세
                                         four_ins            NVARCHAR(200)    NULL,                     -- 4대 보험
                                         retire_pay          NVARCHAR(100)    NULL,                     -- 퇴직금
                                         welfare_desc        NVARCHAR(MAX)    NULL,                     -- 기타 복리후생
                                         disable_cvntl       NVARCHAR(500)    NULL,                     -- 장애인 편의시설
    -- ── 기타 코드 ─────────────────────────────────────────────
                                         enter_tp_cd_detail  VARCHAR(10)      NULL,                     -- 근무형태코드
                                         sal_tp_cd           VARCHAR(10)      NULL,                     -- 임금형태코드
                                         keyword_list        NVARCHAR(1000)   NULL,                     -- 검색 키워드 (쉼표 구분)
                                         CONSTRAINT PK_J_참여자관리_JOB_POSTING PRIMARY KEY (wanted_auth_no)
);

-- ══════════════════════════════════════════════════════════════
-- Phase 2 ALTER TABLE — 기존 테이블에 상세정보 컬럼 추가
-- (테이블이 이미 존재하는 운영/개발 환경에서 실행)
-- ══════════════════════════════════════════════════════════════
-- ALTER TABLE dbo.J_참여자관리_JOB_POSTING ADD
--     detail_fetched      BIT              NOT NULL DEFAULT 0,
--     sync_dtm_detail     DATETIME         NULL,
--     reper_nm            NVARCHAR(100)    NULL,
--     tot_psncnt          INT              NULL,
--     capital_amt         BIGINT           NULL,
--     yr_sales_amt        BIGINT           NULL,
--     busi_cont           NVARCHAR(MAX)    NULL,
--     home_pg             NVARCHAR(500)    NULL,
--     busi_size           NVARCHAR(50)     NULL,
--     recrut_peri         NVARCHAR(100)    NULL,
--     recruit_cnt         INT              NULL,
--     rel_jobs_nm         NVARCHAR(500)    NULL,
--     job_cont            NVARCHAR(MAX)    NULL,
--     for_lang            NVARCHAR(200)    NULL,
--     major               NVARCHAR(200)    NULL,
--     certificate         NVARCHAR(500)    NULL,
--     mltsvc_exc_hope     NVARCHAR(50)     NULL,
--     comp_abl            NVARCHAR(200)    NULL,
--     pref_cond           NVARCHAR(MAX)    NULL,
--     sel_mthd            NVARCHAR(500)    NULL,
--     rcpt_mthd           NVARCHAR(500)    NULL,
--     submit_doc          NVARCHAR(MAX)    NULL,
--     etc_hope_cont       NVARCHAR(MAX)    NULL,
--     near_line           NVARCHAR(200)    NULL,
--     work_time_cont      NVARCHAR(MAX)    NULL,
--     four_ins            NVARCHAR(200)    NULL,
--     retire_pay          NVARCHAR(100)    NULL,
--     welfare_desc        NVARCHAR(MAX)    NULL,
--     disable_cvntl       NVARCHAR(500)    NULL,
--     enter_tp_cd_detail  VARCHAR(10)      NULL,
--     sal_tp_cd           VARCHAR(10)      NULL,
--     keyword_list        NVARCHAR(1000)   NULL;
-- GO

-- ── 인덱스 ──────────────────────────────────────────────────────
-- 활성 공고 최신순 조회 (기본 검색 패턴)
CREATE INDEX IX_JOB_POSTING_ACTIVE_REG
    ON dbo.J_참여자관리_JOB_POSTING (is_active, reg_dt DESC);

-- 키워드 검색 보조
CREATE INDEX IX_JOB_POSTING_TITLE
    ON dbo.J_참여자관리_JOB_POSTING (recrut_title) WHERE is_active = 1;

-- 마감일 기준 검색
CREATE INDEX IX_JOB_POSTING_CLOSE_DT
    ON dbo.J_참여자관리_JOB_POSTING (close_dt) WHERE is_active = 1;

-- 동기화 관리 (deactivate 쿼리 성능)
CREATE INDEX IX_JOB_POSTING_SYNC_DTM
    ON dbo.J_참여자관리_JOB_POSTING (sync_dtm, is_active);

-- ── 주의사항 ────────────────────────────────────────────────────
-- sync_dtm: 스케줄러 UPSERT 시 현재 실행 시각으로 갱신.
--           동기화 후 sync_dtm < 현재 실행 시각인 행 = API에서 사라진 공고 → is_active=0 처리
-- is_active: 0 = 마감/삭제된 공고. 조회 시 반드시 WHERE is_active=1 조건 사용
-- jobs_nm, emp_tp_nm: 검색 결과 카드(JSP)에서 직접 출력되는 텍스트 컬럼.
--                     고용24 API 응답의 jobsNm, empTpNm 값을 그대로 저장