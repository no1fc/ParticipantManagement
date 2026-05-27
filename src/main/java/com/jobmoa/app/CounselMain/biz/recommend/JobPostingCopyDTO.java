package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

/**
 * 상담일지 복사 모달용 DTO — JOB_POSTING 테이블의 복사 대상 컬럼 매핑
 */
@Data
public class JobPostingCopyDTO {
    // ── 기본 항목 ──
    private String companyNm;       // 기업명
    private String recrutTitle;     // 채용공고
    private String indTpNm;         // 업종
    private String jobsNm;          // 직무
    private String prefCond;        // 우대조건
    private String certificate;     // 자격면허
    private String major;           // 전공
    private String salDesc;         // 급여
    private String empTpNm;         // 고용형태
    private String regionNm;        // 근무지역
    private String wantedInfoUrl;   // 공고 URL
    private String infoSvc;         // 정보제공처

    // ── 더보기 항목 ──
    private String reperNm;         // 대표자명
    private Integer totPsncnt;      // 근로자수
    private Long capitalAmt;        // 자본금
    private Long yrSalesAmt;        // 연매출
    private String busiCont;        // 사업내용
    private String homePg;          // 홈페이지
    private String busiSize;        // 회사규모
    private String recrutPeri;      // 채용기간
    private Integer recruitCnt;     // 모집인원
    private String jobCont;         // 직무내용
    private String workTimeCont;    // 근무시간
    private String fourIns;         // 4대보험
    private String retirePay;       // 퇴직금
    private String welfareDesc;     // 복리후생
    private String career;          // 경력
    private String minEdubg;        // 최소학력
    private String maxEdubg;        // 최대학력
}
