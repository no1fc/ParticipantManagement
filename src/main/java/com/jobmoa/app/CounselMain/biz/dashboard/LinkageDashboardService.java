package com.jobmoa.app.CounselMain.biz.dashboard;

import java.util.List;

/**
 * 연계 실적 대시보드 서비스 인터페이스.
 * <p>실적기간 중 연계일이 있는 참여자 기준의 전체/지점별/상담사별 연계 현황 조회를 정의한다.</p>
 */
public interface LinkageDashboardService {

    /**
     * 조건에 맞는 연계 현황 목록을 조회한다(지점별/상담사별).
     * @param dto condition(조회 조건)이 설정된 DTO
     * @return 연계 현황 목록
     */
    List<LinkageDashboardDTO> selectAll(LinkageDashboardDTO dto);

    /**
     * 조건에 맞는 연계 현황 단건을 조회한다(전체 합계).
     * @param dto condition(조회 조건)이 설정된 DTO
     * @return 연계 현황 단건, 없으면 null
     */
    LinkageDashboardDTO selectOne(LinkageDashboardDTO dto);
}
