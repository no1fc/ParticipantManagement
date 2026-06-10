package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link AdminDailyReportService} 구현체.
 * <p>AdminDailyReportDAO를 통해 일일업무보고 조회 기능을 처리한다.</p>
 */
@Slf4j
@Service
public class AdminDailyReportServiceImpl implements AdminDailyReportService {

    @Autowired
    private AdminDailyReportDAO adminDailyReportDAO;

    @Override
    public List<AdminDTO> getDailyReportList(AdminDTO dto) {
        return adminDailyReportDAO.selectDailyReportList(dto);
    }
}
