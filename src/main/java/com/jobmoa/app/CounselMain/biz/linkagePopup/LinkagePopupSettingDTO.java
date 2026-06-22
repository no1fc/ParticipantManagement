package com.jobmoa.app.CounselMain.biz.linkagePopup;

import lombok.Data;

/**
 * 국취 연계실적 독려 팝업 설정 DTO.
 * <p>{@code J_참여자관리_연계실적팝업설정} 단일 행을 매핑한다.
 * 한국어 컬럼은 매퍼에서 camelCase 별칭으로 변환되어 들어온다.</p>
 */
@Data
public class LinkagePopupSettingDTO {

    /** 노출여부 (true=노출, false=중단) */
    private boolean displayEnabled;

    /** 노출빈도일수 (N일에 1회, 1=매일 최초 1회) */
    private int displayIntervalDays;

    /** 목표분자 (가점 만점 기준 건수, 기본 40) */
    private int targetNumerator;

    /** 평균배수 (분발 목표선 배수, 기본 1.5) */
    private double averageMultiplier;

    /** 집계시작일 (yyyy-MM-dd) */
    private String startDate;

    /** 집계종료일 (yyyy-MM-dd) */
    private String endDate;

    /** 상담사수 오버라이드 (null=자동 COUNT, 값=전체평균 분모 수동지정) */
    private Integer staffCountOverride;

    /** 단계1 문구 (목표 달성) */
    private String stage1Message;
    /** 단계2 문구 (목표미달 + 평균초과) */
    private String stage2Message;
    /** 단계3 문구 (평균 동일) */
    private String stage3Message;
    /** 단계4 문구 (평균미만 + 50%이상) */
    private String stage4Message;
    /** 단계5 문구 (평균 50% 미만) */
    private String stage5Message;
    /** 단계6 문구 (0건) */
    private String stage6Message;
}
