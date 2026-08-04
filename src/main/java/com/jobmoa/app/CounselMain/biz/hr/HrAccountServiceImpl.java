package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link HrAccountService} 구현체. {@code J_직원_계정} 상태·비밀번호를 관리한다.
 * <p>비밀번호 SoT는 {@code J_직원_계정.비밀번호}(BCrypt). 기존 {@code BCryptPasswordEncoder} 빈 재사용.</p>
 */
@Slf4j
@Service
public class HrAccountServiceImpl implements HrAccountService {

    @Autowired
    private HrAccountDAO hrAccountDAO;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<HrAccountDTO> getAccountList(HrAccountDTO dto) {
        return hrAccountDAO.selectAccountList(dto);
    }

    @Override
    public HrAccountDTO getAccountOne(HrAccountDTO dto) {
        return hrAccountDAO.selectAccountOne(dto);
    }

    @Override
    public boolean changeStatus(HrAccountDTO dto) {
        return hrAccountDAO.updateAccountStatus(dto);
    }

    @Override
    public boolean resetPassword(HrAccountDTO dto) {
        // 평문 임시비밀번호를 BCrypt로 인코딩 (이미 해시면 그대로)
        String raw = dto.getPassword();
        if (raw != null && !raw.startsWith("$2a$")) {
            dto.setPassword(passwordEncoder.encode(raw));
        }
        return hrAccountDAO.resetPassword(dto);
    }

    @Override
    public boolean unlock(HrAccountDTO dto) {
        return hrAccountDAO.unlockAccount(dto);
    }
}
