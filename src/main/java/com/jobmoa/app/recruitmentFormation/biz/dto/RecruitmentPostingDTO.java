package com.jobmoa.app.recruitmentFormation.biz.dto;

import lombok.Data;

/**
 * JOB_POSTING 테이블 매핑 DTO (2단계 스케줄러 DB 저장용)
 * 고용24 XML 응답의 <wanted> 요소를 모두 포함
 */
@Data
public class RecruitmentPostingDTO {

    // ── 식별 / 기업 ───────────────────────────────────────
    private String wantedAuthNo;    // 구인인증번호 (PK)
    private String companyNm;       // 회사명              ← API: company
    private String bizRegNo;        // 사업자등록번호      ← API: busino
    private String indTpNm;         // 업종명              ← API: indTpNm

    // ── 직무 / 조건 ───────────────────────────────────────
    private String recrutTitle;     // 채용제목            ← API: title
    private Integer jobsCd;         // 직종코드            ← API: jobsCd
    private String jobsNm;          // 직종명              ← API: jobsNm
    private Integer empTpCd;        // 고용형태코드        ← API: empTpCd
    private String empTpNm;         // 고용형태명          ← API: empTpNm
    private String salTpNm;         // 임금형태명
    private String salDesc;         // 급여 상세           ← API: sal
    private Integer minSal;         // 최소임금액
    private Integer maxSal;         // 최대임금액
    private String minEdubg;        // 최소학력
    private String maxEdubg;        // 최대학력
    private String career;          // 경력
    private String holidayTpNm;     // 근무형태

    // ── 근무지 ─────────────────────────────────────────────
    private String regionNm;        // 근무지역 요약       ← API: region
    private String zipCd;           // 우편번호
    private String strtNmAddr;      // 도로명주소
    private String basicAddr;       // 기본주소
    private String detailAddr;      // 상세주소

    // ── 시스템 / 메타 ──────────────────────────────────────
    private String regDt;           // 공고 등록일
    private String closeDt;         // 공고 마감일
    private String infoSvc;         // 정보제공처
    private String wantedInfoUrl;   // 워크넷 채용정보 URL
    private String mobileInfoUrl;   // 모바일 URL          ← API: wantedMobileInfoUrl
    private String smodifyDtm;      // 최종수정일시

    // ── 자체 관리 컬럼 ────────────────────────────────────
    private String syncDtm;         // DB 동기화 일시 (yyyy-MM-dd HH:mm:ss)
}