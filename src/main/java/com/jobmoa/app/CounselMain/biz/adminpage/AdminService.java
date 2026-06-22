package com.jobmoa.app.CounselMain.biz.adminpage;

import org.apache.ibatis.session.ResultHandler;

import java.util.List;
import java.util.Map;

/**
 * 관리자 페이지 공통/보존 기능 서비스 인터페이스.
 * <p>대시보드 KPI, 상담사 목록, 참여자 Excel, 상담사별 통계, 연계 현황,
 * 운영 현황 대시보드 등 기능별로 분리되지 않은 공통 기능을 정의한다.</p>
 * <p>사용자·지점·참여자·일일업무보고·알선·이력서요청은 각각 별도 서비스로 분리되었다
 * ({@link AdminUserService}, {@link AdminBranchService}, {@link AdminParticipantService},
 * {@link AdminDailyReportService}, {@link AdminJobPlacementService}, {@link AdminResumeRequestService}).</p>
 */
public interface AdminService {

    // ===== 대시보드 KPI =====

    /**
     * 대시보드 KPI 데이터를 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return KPI 및 지점별 통계가 포함된 Map
     */
    Map<String, Object> getDashboardData(AdminDTO dto);

    // ===== 상담사 목록 =====

    /**
     * 지점별 상담사 목록을 조회한다.
     * @param dto 지점 정보가 담긴 DTO
     * @return 상담사 목록
     */
    List<AdminDTO> getCounselorList(AdminDTO dto);

    // ===== 참여자 Excel 스트리밍 (대량 다운로드 메모리 최적화) =====

    /**
     * 참여자 Excel 기본 목록을 스트리밍 방식으로 조회한다(전체 List 미적재).
     * @param dto 검색 조건이 담긴 DTO
     * @param handler 행 1건씩 전달받는 ResultHandler
     */
    void getParticipantExcelListStream(AdminDTO dto, ResultHandler<AdminDTO> handler);

    // ===== 상담사별 통계 =====

    /**
     * 상담사별 알선 통계를 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 상담사별 통계 목록
     */
    List<AdminDTO> getPlacementStatsByCounselor(AdminDTO dto);

    // ===== 연계 현황 =====

    /**
     * 연계 현황 통계를 조회한다 (지점별, 유형별 통계 포함).
     * @param dto 검색 조건이 담긴 DTO
     * @return 연계 현황 통계가 포함된 Map
     */
    Map<String, Object> getLinkageStats(AdminDTO dto);

    /**
     * 상담사별 연계 현황을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 상담사별 연계 현황 목록
     */
    List<AdminDTO> getLinkageByCounselor(AdminDTO dto);

    /**
     * 유형별 연계 현황을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 유형별 연계 현황 목록
     */
    List<AdminDTO> getLinkageByType(AdminDTO dto);

    // ===== 운영 현황 대시보드 =====

    /**
     * 지점별 운영 현황 대시보드 데이터를 조회한다.
     * @param dto 검색 조건(연도 등)이 담긴 DTO
     * @return 운영 현황 데이터 목록
     */
    List<AdminDTO> getManagementDashboardData(AdminDTO dto);
}
