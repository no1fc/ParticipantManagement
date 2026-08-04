package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link HrAssignmentService} 구현체. {@code J_직원_부서배치}(M:N) 관리를 처리한다.
 * <p>다중 테이블 갱신(주부서 변경 = 부서배치 플래그 + 재직 주부서코드 + 발령이력)은 매퍼 내 여러 SQL이
 * 단일 AOP 트랜잭션으로 묶인다(pointcut: {@code biz.hr.*Impl}). 필터드 UNIQUE
 * {@code UX_부서배치_현재주부서}(종료일 NULL AND 주부서여부=1) 전이 위반을 피하려면
 * 주부서 해제 → 지정 순서를 지켜야 한다(매퍼에서 보장).</p>
 */
@Slf4j
@Service
public class HrAssignmentServiceImpl implements HrAssignmentService {

    @Autowired
    private HrAssignmentDAO hrAssignmentDAO;

    @Override
    public List<HrAssignmentDTO> getEmployeeList(HrAssignmentDTO dto) {
        return hrAssignmentDAO.selectEmployeeList(dto);
    }

    @Override
    public List<HrAssignmentDTO> getAssignmentList(HrAssignmentDTO dto) {
        return hrAssignmentDAO.selectAssignmentList(dto);
    }

    @Override
    public List<HrAssignmentDTO> getDeptList() {
        return hrAssignmentDAO.selectDeptList();
    }

    @Override
    public boolean hasOpenAssignmentToDept(HrAssignmentDTO dto) {
        return hrAssignmentDAO.selectOpenAssignmentToDeptCount(dto) > 0;
    }

    @Override
    public boolean addAssignment(HrAssignmentDTO dto) {
        return hrAssignmentDAO.addAssignment(dto);
    }

    @Override
    public boolean changePrimary(HrAssignmentDTO dto) {
        return hrAssignmentDAO.changePrimary(dto);
    }

    @Override
    public boolean endAssignment(HrAssignmentDTO dto) {
        return hrAssignmentDAO.endAssignment(dto);
    }
}
