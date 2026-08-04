package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link HrDashboardService} 구현체. 읽기전용 집계 4콜을 하나의 맵으로 합성한다.
 * <p>DAO 메서드는 모두 {@code select*} 접두사라 AOP가 read-only 트랜잭션으로 처리한다.</p>
 */
@Slf4j
@Service
public class HrDashboardServiceImpl implements HrDashboardService {

    @Autowired
    private HrDashboardDAO hrDashboardDAO;

    @Override
    public Map<String, Object> getDashboard() {
        log.info("HrDashboardServiceImpl getDashboard");
        Map<String, Object> result = new HashMap<>();
        result.put("summary", hrDashboardDAO.selectSummary());
        result.put("byDepartment", hrDashboardDAO.selectByDepartment());
        result.put("tenure", hrDashboardDAO.selectTenureDistribution());
        result.put("timeline", hrDashboardDAO.selectRecentTimeline());
        return result;
    }
}
