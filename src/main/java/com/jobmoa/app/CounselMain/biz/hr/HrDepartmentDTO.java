package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 부서/조직 관리 DTO.
 * <p>대상 테이블: {@code J_부서} (부서 마스터, 자기참조 상위부서).</p>
 */
@Data
public class HrDepartmentDTO {

    /** 부서코드 (PK, nvarchar20) */
    private String deptCode;
    /** 부서명 */
    private String deptName;
    /** 부서유형 (본사/지점/팀/사업부) */
    private String deptType;
    /** 상위부서코드 (self-FK, NULL=최상위) */
    private String parentDeptCode;
    /** 상위부서명 (조회 시 self-join 결과) */
    private String parentDeptName;
    /** 지점명 (잡모아 '지점' 문자열과 1:1, 지점유형만) */
    private String branchName;
    /** 사용여부 (사용/미사용) */
    private String useStatus;
    /** 순서 */
    private Integer deptOrder;

    // ===== 검색 조건 =====
    private String searchDeptName;
    private String searchDeptType;
    private String searchUseStatus;
}
