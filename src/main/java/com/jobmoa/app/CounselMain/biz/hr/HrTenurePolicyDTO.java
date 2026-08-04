package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 근속정책 관리 DTO.
 * <p>대상 테이블: {@code J_근속산정정책} (재입사자 근속 가중 정책, 키-값).</p>
 * <p>DB 스칼라 함수 {@code fn_직원_총근속일}/{@code fn_직원_직급산정근속일}이
 * 전역 {@code REHIRE_WEIGHT} 값을 여기서 읽어 근속을 산정한다.</p>
 */
@Data
public class HrTenurePolicyDTO {

    /** 정책키 (UNIQUE 논리키, nvarchar40, 화면상 PK 취급) 예: REHIRE_WEIGHT */
    private String policyKey;
    /** 가중퍼센트 (0~100, CHECK 제약) */
    private Integer weightPercent;
    /** 적용범위 (NULL 허용) 예: 직급산정 */
    private String scope;
    /** 사용여부 (사용/미사용, 소프트삭제 플래그) */
    private String useStatus;
    /** 비고 (NULL 허용) */
    private String remark;

    // ===== 검색 조건 =====
    private String searchScope;
    private String searchUseStatus;
}
