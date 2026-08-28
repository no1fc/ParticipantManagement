package com.jobmoa.app.CounselMain.biz.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 연계 실적 대시보드 데이터 전송 객체.
 * <p>전체/지점별/상담사별 연계 건수(COUNT)를 두 기준으로 전달한다.</p>
 * <ul>
 *   <li>실적 기간 전체: 연계일이 실적기간(전년 11/1 ~ 당해 10/31) 내인 연계 건수</li>
 *   <li>종료일 기준: 실제종료일이 실적기간 내인 참여자의 연계 건수</li>
 * </ul>
 * <p>실제종료일 산식(취창업일 → 기간만료일)은 알선 대시보드({@code Arrangement-mapping.xml})
 * 및 채점 CTE({@code Dashboard-mapping.xml})의 종료 판정과 동일하게 유지한다.</p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkageDashboardDTO {

    // ===== 조회 조건 =====
    private String startDate;      // 실적기간 시작일 (전년 11/1)
    private String endDate;        // 실적기간 종료일 (당해 10/31)
    private String scopeAccount;   // 권한 스코프: 일반 상담사는 본인 전담자_계정, 관리자는 null(전체)
    private String condition;      // MyBatis 매퍼 ID 디스패치용

    // ===== 축 라벨 (지점별/상담사별/유형별) =====
    private String branch;             // 지점
    private String counselorAccount;   // 상담사 계정(전담자_계정)
    private String counselorName;      // 상담사 이름
    private String linkageType;        // 연계유형(실적 인정 5종)

    // ===== 지표: 연계 건수 2기준 (COUNT(*)) =====
    private int fullPeriodEventCount; // 연계 건수 · 실적 기간 전체 (연계일이 실적기간 내)
    private int terminatedEventCount; // 연계 건수 · 종료일 기준 (실제종료일이 실적기간 내)
}
