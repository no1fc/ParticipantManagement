package com.jobmoa.app.CounselMain.biz.dashboard;

import java.util.List;

public interface DashboardService {
    List<DashboardDTO> selectAll(DashboardDTO dashboardDTO);
    DashboardDTO selectOne(DashboardDTO dashboardDTO);
    boolean insert(DashboardDTO dashboardDTO);
    boolean update(DashboardDTO dashboardDTO);
    boolean delete(DashboardDTO dashboardDTO);
}
