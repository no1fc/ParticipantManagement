package com.jobmoa.app.CounselMain.biz.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link LinkageDashboardService} 구현체.
 * <p>{@link LinkageDashboardDAO}를 통해 연계 실적 대시보드 데이터를 조회한다.</p>
 */
@Slf4j
@Service("LinkageDashboard")
public class LinkageDashboardServiceImpl implements LinkageDashboardService {

    @Autowired
    private LinkageDashboardDAO linkageDashboardDAO;

    @Override
    public List<LinkageDashboardDTO> selectAll(LinkageDashboardDTO dto) {
        return linkageDashboardDAO.selectAll(dto);
    }

    @Override
    public LinkageDashboardDTO selectOne(LinkageDashboardDTO dto) {
        return linkageDashboardDAO.selectOne(dto);
    }
}
