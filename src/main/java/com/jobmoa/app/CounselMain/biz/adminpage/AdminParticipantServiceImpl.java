package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link AdminParticipantService} 구현체.
 * <p>AdminParticipantDAO를 통해 참여자 관리 기능을 처리한다.</p>
 */
@Slf4j
@Service
public class AdminParticipantServiceImpl implements AdminParticipantService {

    @Autowired
    private AdminParticipantDAO adminParticipantDAO;

    @Override
    public List<AdminDTO> getParticipantList(AdminDTO dto) {
        return adminParticipantDAO.selectParticipantList(dto);
    }

    @Override
    public AdminDTO getParticipantOne(AdminDTO dto) {
        return adminParticipantDAO.selectParticipantOne(dto);
    }

    @Override
    public boolean removeParticipant(AdminDTO dto) {
        return adminParticipantDAO.deleteParticipant(dto);
    }
}
