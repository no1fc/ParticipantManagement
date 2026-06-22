package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 페이지 공통/보존 기능 데이터 접근 객체.
 * <p>대시보드 KPI, 상담사 목록, 참여자 Excel, 상담사별 통계, 연계 현황,
 * 운영 현황 대시보드 등 기능별로 분리되지 않은 공통 SQL 매핑을 담당한다.</p>
 * <p>사용자·지점·참여자·일일업무보고·알선·이력서요청은 각각 별도 DAO로 분리되었다
 * ({@link AdminUserDAO}, {@link AdminBranchDAO}, {@link AdminParticipantDAO},
 * {@link AdminDailyReportDAO}, {@link AdminJobPlacementDAO}, {@link AdminResumeRequestDAO}).</p>
 * <p>MyBatis 매퍼 네임스페이스 "AdminDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class AdminDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminDAO.";

    // ===== 대시보드 KPI =====

    /**
     * 대시보드 KPI 요약 데이터를 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return KPI 요약 정보
     */
    public AdminDTO selectDashboardKpi(AdminDTO dto) {
        log.info("AdminDAO selectDashboardKpi");
        return sqlSession.selectOne(ns + "selectDashboardKpi", dto);
    }

    /**
     * 지점별 참여자 통계를 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 지점별 참여자 통계 목록
     */
    public List<AdminDTO> selectBranchParticipantStats(AdminDTO dto) {
        log.info("AdminDAO selectBranchParticipantStats");
        return sqlSession.selectList(ns + "selectBranchParticipantStats", dto);
    }

    // ===== 상담사 목록 =====

    /**
     * 지점별 상담사 목록을 조회한다.
     *
     * @param dto 지점 정보가 설정된 DTO
     * @return 해당 지점의 상담사 목록
     */
    public List<AdminDTO> selectCounselorsByBranch(AdminDTO dto) {
        log.info("AdminDAO selectCounselorsByBranch");
        return sqlSession.selectList(ns + "selectCounselorsByBranch", dto);
    }

    // ===== 참여자 Excel 스트리밍 (대량 다운로드 메모리 최적화) =====

    /**
     * 참여자 Excel 기본 목록을 스트리밍 방식으로 조회한다(전체 List 미적재).
     *
     * @param dto     검색 조건이 담긴 DTO
     * @param handler 행 1건씩 전달받는 ResultHandler
     */
    public void selectParticipantExcelListStream(AdminDTO dto, ResultHandler<AdminDTO> handler) {
        log.info("AdminDAO selectParticipantExcelListStream");
        sqlSession.select(ns + "selectParticipantExcelList", dto, handler);
    }

    // ===== 상담사별 통계 =====

    /**
     * 상담사별 알선 통계를 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 상담사별 알선 통계 목록
     */
    public List<AdminDTO> selectPlacementStatsByCounselor(AdminDTO dto) {
        log.info("AdminDAO selectPlacementStatsByCounselor");
        return sqlSession.selectList(ns + "selectPlacementStatsByCounselor", dto);
    }

    // ===== 연계 현황 =====

    /**
     * 지점별 연계 현황 통계를 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 지점별 연계 현황 목록
     */
    public List<AdminDTO> selectLinkageStatsByBranch(AdminDTO dto) {
        log.info("AdminDAO selectLinkageStatsByBranch");
        return sqlSession.selectList(ns + "selectLinkageStatsByBranch", dto);
    }

    /**
     * 상담사별 연계 현황 통계를 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 상담사별 연계 현황 목록
     */
    public List<AdminDTO> selectLinkageStatsByCounselor(AdminDTO dto) {
        log.info("AdminDAO selectLinkageStatsByCounselor");
        return sqlSession.selectList(ns + "selectLinkageStatsByCounselor", dto);
    }

    /**
     * 유형별 연계 현황 통계를 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 유형별 연계 현황 목록
     */
    public List<AdminDTO> selectLinkageStatsByType(AdminDTO dto) {
        log.info("AdminDAO selectLinkageStatsByType");
        return sqlSession.selectList(ns + "selectLinkageStatsByType", dto);
    }

    // ===== 운영 현황 대시보드 =====

    /**
     * 지점별 운영 현황 대시보드 데이터를 조회한다.
     *
     * @param dto searchYear(검색연도)가 설정된 DTO
     * @return 지점별 운영 현황 데이터 목록
     */
    public List<AdminDTO> selectManagementDashboardData(AdminDTO dto) {
        log.info("AdminDAO selectManagementDashboardData year={}", dto.getSearchYear());
        return sqlSession.selectList(ns + "selectManagementDashboardData", dto);
    }
}
