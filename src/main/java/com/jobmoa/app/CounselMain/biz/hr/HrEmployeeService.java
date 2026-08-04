package com.jobmoa.app.CounselMain.biz.hr;

import java.util.List;

/**
 * HR - 직원 관리 서비스 인터페이스.
 */
public interface HrEmployeeService {

    List<HrEmployeeDTO> getEmployeeList(HrEmployeeDTO dto);

    HrEmployeeDTO getEmployeeOne(HrEmployeeDTO dto);

    /** 아이디 중복 여부 (등록 시 UNIQUE 충돌 방지). */
    boolean isUserIdExists(HrEmployeeDTO dto);

    /** 신규 직원 등록 (직원/계정/재직/부서배치/재직기간/발령이력 다중 테이블). */
    boolean addEmployee(HrEmployeeDTO dto);

    /** 직원 신원·재직·계정상태 수정. */
    boolean modifyEmployee(HrEmployeeDTO dto);

    /** 퇴사 처리(soft-delete). 현재재직상태='퇴사' + 재직기간 종료. 트리거가 계정을 정지시킨다. */
    boolean resignEmployee(HrEmployeeDTO dto);

    /** 복직(오처리 취소). 현재재직상태='재직' + 정지된 계정 재활성화 + 마지막 재직기간 퇴사일 초기화. */
    boolean reactivateEmployee(HrEmployeeDTO dto);
}
