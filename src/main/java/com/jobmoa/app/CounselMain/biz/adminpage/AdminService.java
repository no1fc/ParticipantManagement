package com.jobmoa.app.CounselMain.biz.adminpage;

import org.apache.ibatis.session.ResultHandler;

import java.util.List;
import java.util.Map;

/**
 * 관리자 페이지 공통/보존 기능 서비스 인터페이스.
 * <p>기준금액, 나은기준임금, 배정 히스토리, 자격증, 직업훈련, 대시보드 KPI,
 * 상담사 목록, 참여자 Excel, 상담사별 통계, 연계 현황, 운영 현황 대시보드 등
 * 기능별로 분리되지 않은(또는 보존/보류 대상) 기능을 정의한다.</p>
 * <p>사용자·지점·참여자·일일업무보고·알선·이력서요청은 각각 별도 서비스로 분리되었다
 * ({@link AdminUserService}, {@link AdminBranchService}, {@link AdminParticipantService},
 * {@link AdminDailyReportService}, {@link AdminJobPlacementService}, {@link AdminResumeRequestService}).</p>
 */
public interface AdminService {

    // ===== 기준금액 =====

    /**
     * 기준금액 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 기준금액 목록
     */
    List<AdminDTO> getStandardAmountList(AdminDTO dto);

    /**
     * 기준금액 단건을 조회한다.
     * @param dto 조회 대상 정보가 담긴 DTO
     * @return 기준금액 상세 정보
     */
    AdminDTO getStandardAmountOne(AdminDTO dto);

    /**
     * 기준금액을 등록한다.
     * @param dto 등록할 기준금액 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addStandardAmount(AdminDTO dto);

    /**
     * 기준금액을 수정한다.
     * @param dto 수정할 기준금액 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyStandardAmount(AdminDTO dto);

    /**
     * 기준금액을 삭제한다.
     * @param dto 삭제 대상 기준금액 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeStandardAmount(AdminDTO dto);

    // ===== 나은기준임금 =====

    /**
     * 나은기준임금 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 나은기준임금 목록
     */
    List<AdminDTO> getBetterWageList(AdminDTO dto);

    /**
     * 나은기준임금 단건을 조회한다.
     * @param dto 조회 대상 정보가 담긴 DTO
     * @return 나은기준임금 상세 정보
     */
    AdminDTO getBetterWageOne(AdminDTO dto);

    /**
     * 나은기준임금을 등록한다.
     * @param dto 등록할 나은기준임금 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addBetterWage(AdminDTO dto);

    /**
     * 나은기준임금을 수정한다.
     * @param dto 수정할 나은기준임금 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyBetterWage(AdminDTO dto);

    /**
     * 나은기준임금을 삭제한다.
     * @param dto 삭제 대상 나은기준임금 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeBetterWage(AdminDTO dto);

    // ===== 배정 히스토리 =====

    /**
     * CSV 배정 히스토리 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return CSV 배정 히스토리 목록
     */
    List<AdminDTO> getCsvHistoryList(AdminDTO dto);

    /**
     * 산식 배정 히스토리 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 산식 배정 히스토리 목록
     */
    List<AdminDTO> getFormulaHistoryList(AdminDTO dto);

    // ===== 자격증 =====

    /**
     * 자격증 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 자격증 목록
     */
    List<AdminDTO> getCertificateList(AdminDTO dto);

    /**
     * 자격증을 등록한다.
     * @param dto 등록할 자격증 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addCertificate(AdminDTO dto);

    /**
     * 자격증 정보를 수정한다.
     * @param dto 수정할 자격증 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyCertificate(AdminDTO dto);

    /**
     * 자격증을 삭제한다.
     * @param dto 삭제 대상 자격증 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeCertificate(AdminDTO dto);

    // ===== 직업훈련 =====

    /**
     * 직업훈련 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 직업훈련 목록
     */
    List<AdminDTO> getTrainingList(AdminDTO dto);

    /**
     * 직업훈련을 등록한다.
     * @param dto 등록할 직업훈련 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addTraining(AdminDTO dto);

    /**
     * 직업훈련 정보를 수정한다.
     * @param dto 수정할 직업훈련 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyTraining(AdminDTO dto);

    /**
     * 직업훈련을 삭제한다.
     * @param dto 삭제 대상 직업훈련 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeTraining(AdminDTO dto);

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

    // ===== 참여자 Excel =====

    /**
     * 참여자 Excel 출력용 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 참여자 Excel 데이터 목록
     */
    List<AdminDTO> getParticipantExcelList(AdminDTO dto);

    /**
     * 참여자 Excel 전체 컬럼 출력용 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 참여자 전체 컬럼 Excel 데이터 목록
     */
    List<AdminDTO> getParticipantExcelFullList(AdminDTO dto);

    // ===== Excel 빌더 - 시트별 데이터 =====

    /**
     * Excel 빌더용 희망직무 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 희망직무 목록
     */
    List<AdminDTO> getExcelWishJobList(AdminDTO dto);

    /**
     * Excel 빌더용 자격증 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 자격증 목록
     */
    List<AdminDTO> getExcelCertificateList(AdminDTO dto);

    /**
     * Excel 빌더용 직업훈련 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 직업훈련 목록
     */
    List<AdminDTO> getExcelTrainingList(AdminDTO dto);

    // ===== 참여자 Excel 스트리밍 (대량 다운로드 메모리 최적화) =====

    /**
     * 참여자 Excel 기본 목록을 스트리밍 방식으로 조회한다(전체 List 미적재).
     * @param dto 검색 조건이 담긴 DTO
     * @param handler 행 1건씩 전달받는 ResultHandler
     */
    void getParticipantExcelListStream(AdminDTO dto, ResultHandler<AdminDTO> handler);

    /**
     * 참여자 Excel 전체 컬럼 목록을 스트리밍 방식으로 조회한다(전체 List 미적재).
     * @param dto 검색 조건이 담긴 DTO
     * @param handler 행 1건씩 전달받는 ResultHandler
     */
    void getParticipantExcelFullListStream(AdminDTO dto, ResultHandler<AdminDTO> handler);

    /**
     * Excel 빌더용 희망직무 목록을 스트리밍 방식으로 조회한다(전체 List 미적재).
     * @param dto 검색 조건이 담긴 DTO
     * @param handler 행 1건씩 전달받는 ResultHandler
     */
    void getExcelWishJobListStream(AdminDTO dto, ResultHandler<AdminDTO> handler);

    /**
     * Excel 빌더용 자격증 목록을 스트리밍 방식으로 조회한다(전체 List 미적재).
     * @param dto 검색 조건이 담긴 DTO
     * @param handler 행 1건씩 전달받는 ResultHandler
     */
    void getExcelCertificateListStream(AdminDTO dto, ResultHandler<AdminDTO> handler);

    /**
     * Excel 빌더용 직업훈련 목록을 스트리밍 방식으로 조회한다(전체 List 미적재).
     * @param dto 검색 조건이 담긴 DTO
     * @param handler 행 1건씩 전달받는 ResultHandler
     */
    void getExcelTrainingListStream(AdminDTO dto, ResultHandler<AdminDTO> handler);

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
