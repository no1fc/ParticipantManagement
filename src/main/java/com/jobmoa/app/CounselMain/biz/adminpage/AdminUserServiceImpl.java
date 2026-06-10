package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link AdminUserService} 구현체.
 * <p>AdminUserDAO를 통해 사용자(전담자 로그인정보) 관리 기능을 처리한다.</p>
 */
@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserDAO adminUserDAO;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<AdminDTO> getUserList(AdminDTO dto) {
        return adminUserDAO.selectUserList(dto);
    }

    @Override
    public AdminDTO getUserOne(AdminDTO dto) {
        return adminUserDAO.selectUserOne(dto);
    }

    @Override
    public boolean addUser(AdminDTO dto) {
        if (dto.getPassword() != null && !dto.getPassword().startsWith("$2a$")) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return adminUserDAO.insertUser(dto);
    }

    @Override
    public boolean modifyUser(AdminDTO dto) {
        return adminUserDAO.updateUser(dto);
    }

    @Override
    public boolean removeUser(AdminDTO dto) {
        return adminUserDAO.deleteUser(dto);
    }

    @Override
    public boolean resetPassword(AdminDTO dto) {
        dto.setPassword("");
        return adminUserDAO.resetUserPassword(dto);
    }

    @Override
    public boolean approveUser(AdminDTO dto) {
        return adminUserDAO.approveUser(dto);
    }

    @Override
    public int getNextMemberNo() {
        return adminUserDAO.selectNextMemberNo();
    }

    @Override
    public boolean checkUserIdExists(AdminDTO dto) {
        return adminUserDAO.selectUserIdExists(dto) > 0;
    }
}
