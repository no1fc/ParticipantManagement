package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 인원현황 대시보드 요약 카운트 DTO.
 * <p>대상 테이블: {@code J_직원}(재직상태), {@code J_직원_재직기간}(당월 입/퇴사).
 * 부서별 인원·근속 분포·최근 발령 타임라인은 {@code HrDashboardDAO}가 {@code List<Map>}로 별도 반환한다.</p>
 */
@Data
public class HrDashboardDTO {

    /** 재직 인원 (J_직원.현재재직상태 = '재직') */
    private int active;
    /** 휴직 인원 (현재재직상태 = '휴직') */
    private int onLeave;
    /** 퇴사 인원 (현재재직상태 = '퇴사') */
    private int resigned;
    /** 당월 입사 (J_직원_재직기간.입사일 이번달) */
    private int monthlyHire;
    /** 당월 퇴사 (J_직원_재직기간.퇴사일 이번달) */
    private int monthlyResign;
}
