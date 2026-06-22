package com.jobmoa.app.CounselMain.biz.linkagePopup;

import lombok.Data;

/**
 * 국취 연계실적 독려 팝업 응답 DTO.
 * <p>상담사 대시보드 진입 시 비동기로 조회되어 팝업에 렌더된다.</p>
 */
@Data
public class LinkagePopupResultDTO {

    /** 노출 여부 (설정 OFF · 모집단 0명 등일 때 false) */
    private boolean show;

    /** 노출빈도일수 (프론트 1일1회/빈도 계산용) */
    private int displayIntervalDays;

    /** 단계 (1~6) */
    private int stage;

    /** 상담사명 */
    private String counselorName;

    /** 본인 연계건수 */
    private int myCount;

    /** 목표 건수 (목표분자 ÷ 본인 지점 인원) */
    private int targetCount;

    /** 전체 평균 건수 (전체 연계합계 ÷ 전체 국취 인원) */
    private int averageCount;

    /** 분발 목표선 (평균 × 평균배수) */
    private int benchmarkCount;

    /** 목표까지 남은 건수 (max(0, 목표 - 본인)) */
    private int remainingCount;

    /** 가점 만점 기준 건수 (목표분자, "40건↑ 최대 3점" 안내용) */
    private int maxPointThreshold;

    /** 단계 문구 (토큰 치환 완료) */
    private String message;

    /** 집계기간 표기 (각주용, 예: 2025-11-01 ~ 2026-10-31) */
    private String periodLabel;
}
