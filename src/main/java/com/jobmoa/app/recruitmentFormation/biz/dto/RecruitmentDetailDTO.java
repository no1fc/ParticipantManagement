package com.jobmoa.app.recruitmentFormation.biz.dto;

import lombok.Data;

/**
 * 고용24 채용공고 상세 API 응답 파싱 + DB 업데이트용 DTO
 * API: callOpenApiSvcInfo210D01.do (callTp=D, wantedAuthNo 단건 조회)
 *
 * <p>목록 API(callTp=L)에 없는 추가 필드만 포함한다.
 * 상세 API XML 태그명이 실제 응답과 다를 경우 parseXmlToDetail() 파싱부 수정 필요.
 */
@Data
public class RecruitmentDetailDTO {

    // ── 식별 (UPDATE 조건 키) ─────────────────────────────────
    private String  wantedAuthNo;       // 구인인증번호 (PK)

    // ── 기업 상세 (corpInfo 영역) ─────────────────────────────
    private String  reperNm;            // 대표자명          ← API: reperNm
    private Integer totPsncnt;          // 근로자수          ← API: totPsncnt
    private Long    capitalAmt;         // 자본금            ← API: capitalAmt
    private Long    yrSalesAmt;         // 연매출액          ← API: yrSalesAmt
    private String  busiCont;           // 주요사업내용      ← API: busiCont
    private String  homePg;             // 회사 홈페이지     ← API: homePg
    private String  busiSize;           // 회사규모          ← API: busiSize

    // ── 구인 상세 조건 (wantedInfo 영역) ─────────────────────
    private String  recrutPeri;         // 채용기간          ← API: recrutPeri
    private Integer recrutPcnt;         // 모집인원          ← API: recrutPcnt
    private String  jobsNm;             // 모집직종          ← API: jobsNm
    private String  relJobsNm;          // 관련직종          ← API: relJobsNm
    private String  jobCont;            // 직무내용          ← API: jobCont
    private String  forLang;            // 외국어 사항       ← API: forLang
    private String  major;              // 전공              ← API: major
    private String  certificate;        // 자격면허          ← API: certificate
    private String  mltsvcExcHope;      // 병역특례 채용희망 ← API: mltsvcExcHope
    private String  compAbl;            // 컴퓨터 활용능력   ← API: compAbl
    private String  prefCond;           // 우대조건          ← API: pfCond (합산 저장)

    // ── 전형 및 접수 ──────────────────────────────────────────
    private String  selMthd;            // 전형방법          ← API: selMthd
    private String  rcptMthd;           // 접수방법          ← API: rcptMthd
    private String  submitDoc;          // 제출서류          ← API: submitDoc
    private String  etcHopeCont;        // 기타 안내사항     ← API: etcHopeCont

    // ── 근로 조건 및 복리후생 ─────────────────────────────────
    private String  nearLine;           // 인근전철역        ← API: nearLine
    private String  workTimeCont;       // 근무시간 상세     ← API: workTimeCont
    private String  fourIns;            // 4대 보험          ← API: fourIns
    private String  retirePay;          // 퇴직금            ← API: retirePay
    private String  welfareDesc;        // 기타 복리후생     ← API: welfareCont
    private String  disableCvntl;       // 장애인 편의시설   ← API: disableCvntl

    // ── 기타 코드 ─────────────────────────────────────────────
    private String  enterTpCdDetail;    // 근무형태코드      ← API: enterTpCd
    private String  salTpCd;            // 임금형태코드      ← API: salTpCd
    private String  keywordList;        // 검색 키워드(쉼표) ← API: keyword 파싱 후 결합
}