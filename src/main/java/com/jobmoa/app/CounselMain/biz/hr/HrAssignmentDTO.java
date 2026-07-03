package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 부서배치·겸직 관리 DTO.
 * <p>대상 테이블: {@code J_직원_부서배치}(직원↔부서 M:N, 겸직·주부서·시작/종료일) +
 * 직원 선택용 {@code J_직원 ⋈ J_직원_재직 ⋈ J_부서}. 배치 변경(겸직추가/주부서변경/배치종료)은
 * {@code J_직원_발령이력}에 부서이동/겸직추가/겸직해제 1행을 함께 남긴다(append-only 원장).</p>
 * <p><b>주부서 정합:</b> {@code J_직원_재직.주부서코드}는 주부서명 조회의 denormalized 소스이므로,
 * 주부서 변경 시 {@code J_직원_부서배치.주부서여부} 플래그와 {@code J_직원_재직.주부서코드}를 함께 갱신한다.</p>
 */
@Data
public class HrAssignmentDTO {

    // ===== 직원 목록 (J_직원 ⋈ J_직원_재직 ⋈ J_부서) =====
    /** 아이디 (전 시스템 조인 키) */
    private String userId;
    /** 직원번호 (IDENTITY, 표시용) */
    private Integer empNo;
    /** 이름 */
    private String name;
    /** 현재재직상태 (재직/휴직/퇴사) */
    private String empStatus;
    /** 주부서명 (J_직원_재직.주부서코드 조인 결과) */
    private String deptName;
    /** 현재(종료일 NULL) 배치 개수 */
    private Integer assignCount;

    // ===== 배치 행 (J_직원_부서배치) =====
    /** 배치PK (IDENTITY) */
    private Integer assignPk;
    /** 부서코드 (배치 대상) */
    private String deptCode;
    /** 주부서여부 (true=주부서, false=겸직) */
    private Boolean isPrimary;
    /** 시작일 (YYYY-MM-DD) */
    private String startDate;
    /** 종료일 (YYYY-MM-DD, NULL=현재 배치) */
    private String endDate;

    // ===== 액션 파라미터 =====
    /** 발령이력 사유 (겸직추가/주부서변경/배치종료) */
    private String reason;

    // ===== 검색 조건 =====
    private String searchName;
    private String searchUserId;
    private String searchEmpStatus;
}
