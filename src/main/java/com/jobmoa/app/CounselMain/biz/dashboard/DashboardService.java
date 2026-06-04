package com.jobmoa.app.CounselMain.biz.dashboard;

import java.util.List;

/**
 * 대시보드 서비스 인터페이스.
 * 상담사 대시보드 데이터의 조회, 등록, 수정, 삭제를 정의한다.
 */
public interface DashboardService {

    /**
     * 대시보드 데이터 목록을 조회한다.
     * @param dashboardDTO 검색 조건이 담긴 DTO
     * @return 대시보드 데이터 목록
     */
    List<DashboardDTO> selectAll(DashboardDTO dashboardDTO);

    /**
     * 대시보드 데이터 단건을 조회한다.
     * @param dashboardDTO 조회 조건이 담긴 DTO
     * @return 대시보드 상세 데이터
     */
    DashboardDTO selectOne(DashboardDTO dashboardDTO);

    /**
     * 대시보드 데이터를 등록한다.
     * @param dashboardDTO 등록할 데이터가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean insert(DashboardDTO dashboardDTO);

    /**
     * 대시보드 데이터를 수정한다.
     * @param dashboardDTO 수정할 데이터가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean update(DashboardDTO dashboardDTO);

    /**
     * 대시보드 데이터를 삭제한다.
     * @param dashboardDTO 삭제 대상 데이터가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean delete(DashboardDTO dashboardDTO);
}
