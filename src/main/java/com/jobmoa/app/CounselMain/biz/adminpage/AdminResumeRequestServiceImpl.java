package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link AdminResumeRequestService} 구현체.
 * <p>AdminResumeRequestDAO를 통해 이력서 요청 관리 기능을 처리한다.</p>
 */
@Slf4j
@Service
public class AdminResumeRequestServiceImpl implements AdminResumeRequestService {

    @Autowired
    private AdminResumeRequestDAO adminResumeRequestDAO;

    @Override
    public List<AdminDTO> getResumeRequestList(AdminDTO dto) {
        return adminResumeRequestDAO.selectResumeRequestList(dto);
    }

    @Override
    public AdminDTO getResumeRequestOne(AdminDTO dto) {
        return adminResumeRequestDAO.selectResumeRequestOne(dto);
    }

    @Override
    public boolean updateResumeStatus(AdminDTO dto) {
        return adminResumeRequestDAO.updateResumeRequestStatus(dto);
    }
}
