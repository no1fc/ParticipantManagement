package com.jobmoa.app.CounselMain.biz.hr;

import java.util.List;

/**
 * HR - 부서/조직 관리 서비스 인터페이스.
 */
public interface HrDepartmentService {

    List<HrDepartmentDTO> getDepartmentList(HrDepartmentDTO dto);

    HrDepartmentDTO getDepartmentOne(HrDepartmentDTO dto);

    /** 부서코드 중복 여부 (등록 시 PK 충돌 방지). */
    boolean isDeptCodeExists(HrDepartmentDTO dto);

    boolean addDepartment(HrDepartmentDTO dto);

    boolean modifyDepartment(HrDepartmentDTO dto);

    /** 소프트 삭제(사용여부='미사용'). FK 보호. */
    boolean removeDepartment(HrDepartmentDTO dto);
}
