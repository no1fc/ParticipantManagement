package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link AdminJobPlacementService} 구현체.
 * <p>AdminJobPlacementDAO를 통해 알선 관리 기능을 처리한다.</p>
 */
@Slf4j
@Service
public class AdminJobPlacementServiceImpl implements AdminJobPlacementService {

    @Autowired
    private AdminJobPlacementDAO adminJobPlacementDAO;

    @Override
    public List<AdminDTO> getJobPlacementList(AdminDTO dto) {
        return adminJobPlacementDAO.selectJobPlacementList(dto);
    }

    @Override
    public AdminDTO getJobPlacementOne(AdminDTO dto) {
        return adminJobPlacementDAO.selectJobPlacementOne(dto);
    }

    @Override
    public boolean addJobPlacement(AdminDTO dto) {
        return adminJobPlacementDAO.insertJobPlacement(dto);
    }

    @Override
    public boolean modifyJobPlacement(AdminDTO dto) {
        return adminJobPlacementDAO.updateJobPlacement(dto);
    }

    @Override
    public boolean removeJobPlacement(AdminDTO dto) {
        return adminJobPlacementDAO.deleteJobPlacement(dto);
    }
}
