package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;

/**
 * 관리자 - 일일업무보고 서비스 인터페이스.
 */
public interface AdminDailyReportService {

    /**
     * 일일업무보고 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 일일업무보고 목록
     */
    List<AdminDTO> getDailyReportList(AdminDTO dto);
}
