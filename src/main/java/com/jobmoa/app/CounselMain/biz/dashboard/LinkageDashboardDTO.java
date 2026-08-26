package com.jobmoa.app.CounselMain.biz.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 연계 실적 대시보드 데이터 전송 객체.
 * <p>실적기간(전년 11/1 ~ 당해 10/31) 중 연계일이 있는 참여자를 기준으로
 * 전체/지점별/상담사별 연계 참여자 수(중복제거)와 연계 건수(이벤트 수)를 전달한다.
 * 지표는 두 축으로 나뉜다.</p>
 * <ul>
 *   <li>전체 연계: 실적기간 중 연계일이 있는 참여자</li>
 *   <li>종료자 연계: 위 중 진행단계가 종료 단계인 참여자</li>
 * </ul>
 * <p>종료자 판정 기준은 {@code LinkagePopup-mapping.xml}의 {@code terminatedFilter} 및
 * 채점 CTE({@code Dashboard-mapping.xml})의 {@code 종료_F} 진행단계 목록과 동기화한다.</p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkageDashboardDTO {

    // ===== 조회 조건 =====
    private String startDate;      // 실적기간 시작일 (전년 11/1)
    private String endDate;        // 실적기간 종료일 (당해 10/31)
    private String scopeAccount;   // 권한 스코프: 일반 상담사는 본인 전담자_계정, 관리자는 null(전체)
    private String condition;      // MyBatis 매퍼 ID 디스패치용

    // ===== 축 라벨 (지점별/상담사별) =====
    private String branch;             // 지점
    private String counselorAccount;   // 상담사 계정(전담자_계정)
    private String counselorName;      // 상담사 이름

    // ===== 지표: 전체 연계 (실적기간 중 연계일 보유) =====
    private int linkageParticipantCount; // 연계 참여자 수 (COUNT DISTINCT 구직번호)
    private int linkageEventCount;       // 연계 건수 (COUNT(*))

    // ===== 지표: 종료자 연계 (위 중 종료 단계) =====
    private int terminatedParticipantCount; // 종료자 연계 참여자 수 (COUNT DISTINCT)
    private int terminatedEventCount;       // 종료자 연계 건수 (COUNT(*))
}
