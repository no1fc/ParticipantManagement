package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link HrEmployeeService} 구현체. {@code J_직원} 및 관련 테이블 CRUD를 처리한다.
 * <p>신규등록은 매퍼 내 다중 INSERT가 단일 AOP 트랜잭션으로 묶인다. 비밀번호 SoT는
 * {@code J_직원_계정.비밀번호}(BCrypt). 기존 {@code BCryptPasswordEncoder} 빈 재사용.</p>
 */
@Slf4j
@Service
public class HrEmployeeServiceImpl implements HrEmployeeService {

    @Autowired
    private HrEmployeeDAO hrEmployeeDAO;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<HrEmployeeDTO> getEmployeeList(HrEmployeeDTO dto) {
        return hrEmployeeDAO.selectEmployeeList(dto);
    }

    @Override
    public HrEmployeeDTO getEmployeeOne(HrEmployeeDTO dto) {
        return hrEmployeeDAO.selectEmployeeOne(dto);
    }

    @Override
    public boolean isUserIdExists(HrEmployeeDTO dto) {
        return hrEmployeeDAO.selectUserIdExists(dto) > 0;
    }

    @Override
    public boolean addEmployee(HrEmployeeDTO dto) {
        // 평문 비밀번호를 BCrypt로 인코딩 (이미 해시면 그대로)
        String raw = dto.getPassword();
        if (raw != null && !raw.startsWith("$2a$")) {
            dto.setPassword(passwordEncoder.encode(raw));
        }
        return hrEmployeeDAO.insertEmployee(dto);
    }

    @Override
    public boolean modifyEmployee(HrEmployeeDTO dto) {
        return hrEmployeeDAO.updateEmployee(dto);
    }

    @Override
    public boolean resignEmployee(HrEmployeeDTO dto) {
        return hrEmployeeDAO.resignEmployee(dto);
    }

    @Override
    public boolean reactivateEmployee(HrEmployeeDTO dto) {
        return hrEmployeeDAO.reactivateEmployee(dto);
    }
}
