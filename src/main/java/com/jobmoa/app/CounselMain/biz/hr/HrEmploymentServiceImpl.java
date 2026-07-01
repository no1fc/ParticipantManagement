package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link HrEmploymentService} 구현체. {@code J_직원_재직기간} cycle 관리를 처리한다.
 * <p>다중 테이블 갱신(재입사/퇴사)은 매퍼 내 여러 SQL이 단일 AOP 트랜잭션으로 묶인다
 * (pointcut: {@code biz.hr.*Impl}). 재입사 시 {@code J_직원 → '재직'}은 퇴사 트리거를
 * 발동시키지 않으므로 계정 '정지'→'사용' 복구는 매퍼에서 명시적으로 수행한다.</p>
 */
@Slf4j
@Service
public class HrEmploymentServiceImpl implements HrEmploymentService {

    @Autowired
    private HrEmploymentDAO hrEmploymentDAO;

    @Override
    public List<HrEmploymentDTO> getEmployeeList(HrEmploymentDTO dto) {
        return hrEmploymentDAO.selectEmployeeList(dto);
    }

    @Override
    public List<HrEmploymentDTO> getCycleList(HrEmploymentDTO dto) {
        return hrEmploymentDAO.selectCycleList(dto);
    }

    @Override
    public boolean hasOpenCycle(HrEmploymentDTO dto) {
        return hrEmploymentDAO.selectOpenCycleCount(dto) > 0;
    }

    @Override
    public boolean rehire(HrEmploymentDTO dto) {
        return hrEmploymentDAO.rehire(dto);
    }

    @Override
    public boolean resign(HrEmploymentDTO dto) {
        return hrEmploymentDAO.resign(dto);
    }

    @Override
    public boolean updateCycle(HrEmploymentDTO dto) {
        return hrEmploymentDAO.updateCycle(dto);
    }
}
