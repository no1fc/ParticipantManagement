package com.jobmoa.app.CounselMain.biz.hr;

import java.util.Map;

/**
 * HR - 인원현황 대시보드 서비스 인터페이스. 읽기전용 집계만 제공한다.
 */
public interface HrDashboardService {

    /**
     * 대시보드 전체 데이터를 조회한다.
     * @return {@code {summary, byDepartment, tenure, timeline}} 합성 맵
     */
    Map<String, Object> getDashboard();
}
