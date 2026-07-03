package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 발령 관리 DTO.
 * <p>대상 테이블: {@code J_직원_발령이력}(append-only 원장) + 직원 선택용 {@code J_직원 ⋈ J_직원_재직 ⋈ J_부서}.
 * 발령이력은 이미 HrEmployee(입사)·HrEmployment(재입사/퇴사)·HrAssignment(부서이동/겸직추가/겸직해제)가
 * 기록 중이며, 이 화면은 <b>전 유형 통합 타임라인 조회</b> + 다른 화면이 소유하지 않는
 * <b>직급변경/휴직/복직</b> 발령 등록을 담당한다.</p>
 * <p><b>복직 구분:</b> HR_TRANSFER 복직은 <b>휴직자</b> 대상(현재재직상태 휴직→재직, 재직기간 무변경)이다.
 * HrEmployee 복직(오처리 취소)은 <b>퇴사자</b> 대상(재직기간 퇴사일 NULL)으로 의미가 다르다.</p>
 */
@Data
public class HrTransferDTO {

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
    /** 현재 직급 (J_직원_재직.직급) */
    private String position;
    /** 발령이력 건수 */
    private Integer transferCount;

    // ===== 발령 타임라인 행 (J_직원_발령이력) =====
    /** 발령PK (IDENTITY) */
    private Integer transferPk;
    /** 발령유형 (입사/부서이동/겸직추가/겸직해제/직급변경/휴직/복직/퇴사/재입사) */
    private String transferType;
    /** 발령일 (YYYY-MM-DD) */
    private String transferDate;
    /** 이전부서코드 */
    private String prevDept;
    /** 이전부서명 (조인 결과) */
    private String prevDeptName;
    /** 신규부서코드 */
    private String newDept;
    /** 신규부서명 (조인 결과) */
    private String newDeptName;
    /** 이전직급 */
    private String prevPosition;
    /** 신규직급 */
    private String newPosition;
    /** 사유 */
    private String reason;

    // ===== 검색 조건 =====
    private String searchName;
    private String searchUserId;
    private String searchEmpStatus;
}
