package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link HrTransferService} 구현체. {@code J_직원_발령이력}(append-only) 조회 및 직급변경·휴직·복직을 처리한다.
 * <p>다중 테이블 갱신(휴직/복직 = 직원 상태 + 계정 + 발령이력)은 매퍼 내 여러 SQL이 단일 AOP 트랜잭션으로
 * 묶인다(pointcut: {@code biz.hr.*Impl}). 각 write는 발령이력 INSERT를 첫 문장으로 실행해 변경 전 값을
 * 캡처하고, 상태 가드로 무효 대상 시 마스터를 훼손하지 않는다(매퍼에서 보장).</p>
 */
@Slf4j
@Service
public class HrTransferServiceImpl implements HrTransferService {

    @Autowired
    private HrTransferDAO hrTransferDAO;

    @Override
    public List<HrTransferDTO> getEmployeeList(HrTransferDTO dto) {
        return hrTransferDAO.selectEmployeeList(dto);
    }

    @Override
    public List<HrTransferDTO> getTransferList(HrTransferDTO dto) {
        return hrTransferDAO.selectTransferList(dto);
    }

    @Override
    public List<HrTransferDTO> getPositionList() {
        return hrTransferDAO.selectPositionList();
    }

    @Override
    public String getCurrentStatus(HrTransferDTO dto) {
        return hrTransferDAO.selectCurrentStatus(dto);
    }

    @Override
    public boolean changePosition(HrTransferDTO dto) {
        return hrTransferDAO.changePosition(dto);
    }

    @Override
    public boolean startLeave(HrTransferDTO dto) {
        return hrTransferDAO.startLeave(dto);
    }

    @Override
    public boolean returnFromLeave(HrTransferDTO dto) {
        return hrTransferDAO.returnFromLeave(dto);
    }
}
