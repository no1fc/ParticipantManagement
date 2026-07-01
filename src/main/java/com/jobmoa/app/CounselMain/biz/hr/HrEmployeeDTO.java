package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 직원 관리 DTO.
 * <p>대상 테이블: {@code J_직원}(신원) ⋈ {@code J_직원_재직}(현 재직) ⋈ {@code J_직원_계정}(계정요약) ⋈ {@code J_부서}(주부서명).
 * 신규등록은 {@code J_직원_부서배치}·{@code J_직원_재직기간}·{@code J_직원_발령이력}까지 다중 테이블 트랜잭션으로 처리한다.</p>
 */
@Data
public class HrEmployeeDTO {

    // ===== 신원 (J_직원) =====
    /** 아이디 (전 시스템 조인 키, 수정 불가) */
    private String userId;
    /** 직원번호 (IDENTITY, 표시용) */
    private Integer empNo;
    /** 이름 */
    private String name;
    /** 이메일 */
    private String email;
    /** 전화번호 */
    private String phone;
    /** 최초입사일 */
    private String hireDate;
    /** 현재재직상태 (재직/휴직/퇴사) */
    private String empStatus;
    /** 다우사번 */
    private String daouNo;
    /** 비고 */
    private String note;

    // ===== 재직 (J_직원_재직) =====
    /** 주부서코드 (FK J_부서) */
    private String deptCode;
    /** 주부서명 (조회 시 조인 결과) */
    private String deptName;
    /** 지점명 (부서코드에서 파생) */
    private String branchName;
    /** 직급 */
    private String position;
    /** 권한 (기본 '상담') */
    private String role;
    /** 관리자권한 */
    private Boolean isAdmin;
    /** 최종발령일 */
    private String lastAssignDate;

    // ===== 계정 (J_직원_계정) =====
    /** 계정상태 (사용/정지/잠금/퇴사/승인대기) */
    private String accountStatus;

    // ===== 등록 전용 =====
    /** 비밀번호 (신규등록 시, BCrypt 해시 문자열) */
    private String password;

    // ===== 검색 조건 =====
    private String searchName;
    private String searchUserId;
    private String searchDeptCode;
    private String searchEmpStatus;
}
