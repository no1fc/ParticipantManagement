package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 입퇴사 관리 DTO.
 * <p>대상 테이블: {@code J_직원_재직기간}(입퇴사 cycle 이력) + 직원 목록용 {@code J_직원 ⋈ J_직원_재직 ⋈ J_부서}.
 * 재입사(rehire)는 새 cycle 행(재직순번 = MAX+1)을 추가하고 {@code J_직원}·{@code J_직원_계정}·{@code J_직원_발령이력}을
 * 함께 갱신하는 다중 테이블 트랜잭션으로 처리한다.</p>
 * <p><b>재입사 ≠ 복직:</b> 복직({@link HrEmployeeService#reactivateEmployee})은 마지막 cycle 퇴사일을 NULL로 되돌리는
 * 오처리 취소(근속 연속). 재입사는 새 cycle을 추가하고 cycle별 가중퍼센트·직급반영여부를 건별 지정한다.</p>
 */
@Data
public class HrEmploymentDTO {

    // ===== 직원 목록 (J_직원 ⋈ J_직원_재직 ⋈ J_부서) =====
    /** 아이디 (전 시스템 조인 키) */
    private String userId;
    /** 직원번호 (IDENTITY, 표시용) */
    private Integer empNo;
    /** 이름 */
    private String name;
    /** 현재재직상태 (재직/휴직/퇴사) */
    private String empStatus;
    /** 주부서명 (조인 결과) */
    private String deptName;
    /** 총근속일 (dbo.fn_직원_총근속일, cycle 가중 반영) */
    private Integer tenureDays;
    /** 재직 cycle 개수 */
    private Integer cycleCount;

    // ===== 재직 cycle (J_직원_재직기간) =====
    /** 재직PK (IDENTITY) */
    private Integer cyclePk;
    /** 재직순번 (1,2,3… 재입사 시 증가) */
    private Integer seq;
    /** 입사일 (YYYY-MM-DD) */
    private String hireDate;
    /** 퇴사일 (YYYY-MM-DD, NULL=재직중) */
    private String resignDate;
    /** 퇴사사유 */
    private String resignReason;
    /** 가중퍼센트 (재입사 이전 cycle 근속 반영율, NULL=정책 REHIRE_WEIGHT 상속) */
    private Integer weightPercent;
    /** 직급반영여부 (직급산정 근속에 포함) */
    private Boolean reflectPosition;

    // ===== 액션 파라미터 =====
    /** 발령이력 사유 (재입사/퇴사) */
    private String reason;

    // ===== 검색 조건 =====
    private String searchName;
    private String searchUserId;
    private String searchEmpStatus;
}
