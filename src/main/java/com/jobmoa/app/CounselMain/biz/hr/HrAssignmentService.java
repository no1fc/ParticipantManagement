package com.jobmoa.app.CounselMain.biz.hr;

import java.util.List;

/**
 * HR - 부서배치·겸직 관리 서비스 인터페이스.
 * <p>직원별 부서배치({@code J_직원_부서배치}) 조회 및 겸직추가·주부서변경·배치종료를 제공한다.</p>
 */
public interface HrAssignmentService {

    /** 직원 목록 (주부서명·현재 배치 개수 포함). 기본 재직자 조회. */
    List<HrAssignmentDTO> getEmployeeList(HrAssignmentDTO dto);

    /** 특정 직원의 부서배치 목록 (현재 배치·주부서 우선 정렬). */
    List<HrAssignmentDTO> getAssignmentList(HrAssignmentDTO dto);

    /** 부서 드롭다운 목록 (사용중 부서). */
    List<HrAssignmentDTO> getDeptList();

    /** 해당 직원이 이미 그 부서에 현재(종료일 NULL) 배치되어 있는지 여부. 겸직 추가 중복 가드. */
    boolean hasOpenAssignmentToDept(HrAssignmentDTO dto);

    /** 겸직 추가: 부서배치 행(주부서여부=0) 추가 + 발령이력 '겸직추가'. */
    boolean addAssignment(HrAssignmentDTO dto);

    /** 주부서 변경: 기존 주부서 해제 → 대상 배치 주부서 지정 + 재직 주부서코드 동기화 + 발령이력 '부서이동'. */
    boolean changePrimary(HrAssignmentDTO dto);

    /** 배치 종료(겸직해제): 겸직(주부서여부=0) 배치에 종료일 기록 + 발령이력 '겸직해제'. */
    boolean endAssignment(HrAssignmentDTO dto);
}
