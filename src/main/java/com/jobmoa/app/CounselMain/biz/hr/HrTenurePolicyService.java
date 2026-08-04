package com.jobmoa.app.CounselMain.biz.hr;

import java.util.List;

/**
 * HR - 근속정책 관리 서비스 인터페이스.
 */
public interface HrTenurePolicyService {

    List<HrTenurePolicyDTO> getTenurePolicyList(HrTenurePolicyDTO dto);

    HrTenurePolicyDTO getTenurePolicyOne(HrTenurePolicyDTO dto);

    /** 정책키 중복 여부 (등록 시 UNIQUE 충돌 방지). */
    boolean isPolicyKeyExists(HrTenurePolicyDTO dto);

    boolean addTenurePolicy(HrTenurePolicyDTO dto);

    boolean modifyTenurePolicy(HrTenurePolicyDTO dto);

    /** 소프트 삭제(사용여부='미사용'). 근속함수 참조 이력 보호. */
    boolean removeTenurePolicy(HrTenurePolicyDTO dto);
}
