package com.jobmoa.app.CounselMain.biz.hr;

import java.util.List;

/**
 * HR - 입퇴사 관리 서비스 인터페이스.
 * <p>직원별 재직 cycle({@code J_직원_재직기간}) 조회 및 재입사·퇴사·cycle 편집을 제공한다.</p>
 */
public interface HrEmploymentService {

    /** 직원 목록 (총근속일·cycle 개수 포함). 재입사 대상 퇴사자도 노출하도록 기본 전체 조회. */
    List<HrEmploymentDTO> getEmployeeList(HrEmploymentDTO dto);

    /** 특정 직원의 재직 cycle 목록 (재직순번 오름차순). */
    List<HrEmploymentDTO> getCycleList(HrEmploymentDTO dto);

    /** 열린 cycle(퇴사일 NULL, 재직중) 존재 여부. 재입사/퇴사 가드에 사용. */
    boolean hasOpenCycle(HrEmploymentDTO dto);

    /** 재입사: 새 cycle 행(재직순번+1) 추가 + 직원 '재직' 복구 + 계정 '정지'→'사용' + 발령이력 '재입사'. */
    boolean rehire(HrEmploymentDTO dto);

    /** 퇴사: 열린 cycle에 퇴사일·퇴사사유 기록 + 직원 '퇴사'(트리거가 계정 정지) + 발령이력 '퇴사'. */
    boolean resign(HrEmploymentDTO dto);

    /** 종료된 cycle의 가중퍼센트·직급반영여부 수정 (HR 건별 지정). */
    boolean updateCycle(HrEmploymentDTO dto);
}
