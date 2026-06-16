package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link AdminBranchService} 구현체.
 * <p>AdminBranchDAO를 통해 지점 관리 기능을 처리한다.</p>
 */
@Slf4j
@Service
public class AdminBranchServiceImpl implements AdminBranchService {

    @Autowired
    private AdminBranchDAO adminBranchDAO;

    @Override
    public List<AdminDTO> getBranchList(AdminDTO dto) {
        return adminBranchDAO.selectBranchList(dto);
    }

    @Override
    public AdminDTO getBranchOne(AdminDTO dto) {
        return adminBranchDAO.selectBranchOne(dto);
    }

    @Override
    public boolean addBranch(AdminDTO dto) {
        return adminBranchDAO.insertBranch(dto);
    }

    @Override
    public boolean modifyBranch(AdminDTO dto) {
        return adminBranchDAO.updateBranch(dto);
    }

    @Override
    public boolean removeBranch(AdminDTO dto) {
        return adminBranchDAO.deleteBranch(dto);
    }

    @Override
    public int getBranchUserCount(AdminDTO dto) {
        return adminBranchDAO.selectBranchUserCount(dto);
    }
}
