package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 페이지 공통/보존 기능 데이터 접근 객체.
 * <p>기준금액, 나은기준임금, 배정 히스토리, 자격증, 직업훈련, 대시보드 KPI,
 * 상담사 목록, 참여자 Excel, 상담사별 통계, 연계 현황, 운영 현황 대시보드 등
 * 기능별로 분리되지 않은(또는 보존/보류 대상) SQL 매핑을 담당한다.</p>
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

    // ===== 기준금액 (J_참여자관리_기준금액) =====

    /**
     * 기준금액 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 기준금액 목록
     */
    public List<AdminDTO> selectStandardAmountList(AdminDTO dto) {
        log.info("AdminDAO selectStandardAmountList");
        return sqlSession.selectList(ns + "selectStandardAmountList", dto);
    }

    /**
     * 기준금액 단건 상세 정보를 조회한다.
     *
     * @param dto pk(기본키)가 설정된 DTO
     * @return 해당 기준금액 정보, 없으면 null
     */
    public AdminDTO selectStandardAmountOne(AdminDTO dto) {
        log.info("AdminDAO selectStandardAmountOne pk={}", dto.getPk());
        return sqlSession.selectOne(ns + "selectStandardAmountOne", dto);
    }

    /**
     * 신규 기준금액을 등록한다.
     *
     * @param dto 등록할 기준금액 정보
     * @return 등록 성공 여부
     */
    public boolean insertStandardAmount(AdminDTO dto) {
        log.info("AdminDAO insertStandardAmount");
        return sqlSession.insert(ns + "insertStandardAmount", dto) > 0;
    }

    /**
     * 기준금액 정보를 수정한다.
     *
     * @param dto pk(기본키)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateStandardAmount(AdminDTO dto) {
        log.info("AdminDAO updateStandardAmount pk={}", dto.getPk());
        return sqlSession.update(ns + "updateStandardAmount", dto) > 0;
    }

    /**
     * 기준금액을 삭제한다.
     *
     * @param dto pk(기본키)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteStandardAmount(AdminDTO dto) {
        log.info("AdminDAO deleteStandardAmount pk={}", dto.getPk());
        return sqlSession.delete(ns + "deleteStandardAmount", dto) > 0;
    }

    // ===== 나은기준임금 (J_참여자관리_나은기준임금) =====

    /**
     * 나은기준임금 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 나은기준임금 목록
     */
    public List<AdminDTO> selectBetterWageList(AdminDTO dto) {
        log.info("AdminDAO selectBetterWageList");
        return sqlSession.selectList(ns + "selectBetterWageList", dto);
    }

    /**
     * 나은기준임금 단건 상세 정보를 조회한다.
     *
     * @param dto pk(기본키)가 설정된 DTO
     * @return 해당 나은기준임금 정보, 없으면 null
     */
    public AdminDTO selectBetterWageOne(AdminDTO dto) {
        log.info("AdminDAO selectBetterWageOne pk={}", dto.getPk());
        return sqlSession.selectOne(ns + "selectBetterWageOne", dto);
    }

    /**
     * 신규 나은기준임금을 등록한다.
     *
     * @param dto 등록할 나은기준임금 정보
     * @return 등록 성공 여부
     */
    public boolean insertBetterWage(AdminDTO dto) {
        log.info("AdminDAO insertBetterWage");
        return sqlSession.insert(ns + "insertBetterWage", dto) > 0;
    }

    /**
     * 나은기준임금 정보를 수정한다.
     *
     * @param dto pk(기본키)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateBetterWage(AdminDTO dto) {
        log.info("AdminDAO updateBetterWage pk={}", dto.getPk());
        return sqlSession.update(ns + "updateBetterWage", dto) > 0;
    }

    /**
     * 나은기준임금을 삭제한다.
     *
     * @param dto pk(기본키)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteBetterWage(AdminDTO dto) {
        log.info("AdminDAO deleteBetterWage pk={}", dto.getPk());
        return sqlSession.delete(ns + "deleteBetterWage", dto) > 0;
    }

    // ===== 배정 CSV 히스토리 =====

    /**
     * 배정 CSV 업로드 히스토리 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return CSV 히스토리 목록
     */
    public List<AdminDTO> selectCsvHistoryList(AdminDTO dto) {
        log.info("AdminDAO selectCsvHistoryList");
        return sqlSession.selectList(ns + "selectCsvHistoryList", dto);
    }

    // ===== 배정 산정식 히스토리 =====

    /**
     * 배정 산정식 히스토리 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 산정식 히스토리 목록
     */
    public List<AdminDTO> selectFormulaHistoryList(AdminDTO dto) {
        log.info("AdminDAO selectFormulaHistoryList");
        return sqlSession.selectList(ns + "selectFormulaHistoryList", dto);
    }

    // ===== 자격증 (J_참여자관리_자격증) =====

    /**
     * 자격증 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 자격증 목록
     */
    public List<AdminDTO> selectCertificateList(AdminDTO dto) {
        log.info("AdminDAO selectCertificateList");
        return sqlSession.selectList(ns + "selectCertificateList", dto);
    }

    /**
     * 신규 자격증을 등록한다.
     *
     * @param dto 등록할 자격증 정보
     * @return 등록 성공 여부
     */
    public boolean insertCertificate(AdminDTO dto) {
        log.info("AdminDAO insertCertificate");
        return sqlSession.insert(ns + "insertCertificate", dto) > 0;
    }

    /**
     * 자격증 정보를 수정한다.
     *
     * @param dto certificateNo(자격증번호)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateCertificate(AdminDTO dto) {
        log.info("AdminDAO updateCertificate pk={}", dto.getCertificateNo());
        return sqlSession.update(ns + "updateCertificate", dto) > 0;
    }

    /**
     * 자격증을 삭제한다.
     *
     * @param dto certificateNo(자격증번호)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteCertificate(AdminDTO dto) {
        log.info("AdminDAO deleteCertificate pk={}", dto.getCertificateNo());
        return sqlSession.delete(ns + "deleteCertificate", dto) > 0;
    }

    // ===== 직업훈련 (J_참여자관리_직업훈련) =====

    /**
     * 직업훈련 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 직업훈련 목록
     */
    public List<AdminDTO> selectTrainingList(AdminDTO dto) {
        log.info("AdminDAO selectTrainingList");
        return sqlSession.selectList(ns + "selectTrainingList", dto);
    }

    /**
     * 신규 직업훈련을 등록한다.
     *
     * @param dto 등록할 직업훈련 정보
     * @return 등록 성공 여부
     */
    public boolean insertTraining(AdminDTO dto) {
        log.info("AdminDAO insertTraining");
        return sqlSession.insert(ns + "insertTraining", dto) > 0;
    }

    /**
     * 직업훈련 정보를 수정한다.
     *
     * @param dto trainingNo(훈련번호)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateTraining(AdminDTO dto) {
        log.info("AdminDAO updateTraining pk={}", dto.getTrainingNo());
        return sqlSession.update(ns + "updateTraining", dto) > 0;
    }

    /**
     * 직업훈련을 삭제한다.
     *
     * @param dto trainingNo(훈련번호)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteTraining(AdminDTO dto) {
        log.info("AdminDAO deleteTraining pk={}", dto.getTrainingNo());
        return sqlSession.delete(ns + "deleteTraining", dto) > 0;
    }

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

    // ===== 참여자 Excel =====

    /**
     * 참여자 Excel 다운로드용 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return Excel 출력용 참여자 목록
     */
    public List<AdminDTO> selectParticipantExcelList(AdminDTO dto) {
        log.info("AdminDAO selectParticipantExcelList");
        return sqlSession.selectList(ns + "selectParticipantExcelList", dto);
    }

    // ===== 참여자 Excel 전체 컬럼 =====

    /**
     * 참여자 Excel 전체 컬럼 다운로드용 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 전체 컬럼 포함 참여자 목록
     */
    public List<AdminDTO> selectParticipantExcelFullList(AdminDTO dto) {
        log.info("AdminDAO selectParticipantExcelFullList");
        return sqlSession.selectList(ns + "selectParticipantExcelFullList", dto);
    }

    // ===== Excel 빌더 - 희망직무 =====

    /**
     * Excel 빌더용 희망직무 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 희망직무 목록
     */
    public List<AdminDTO> selectExcelWishJobList(AdminDTO dto) {
        log.info("AdminDAO selectExcelWishJobList");
        return sqlSession.selectList(ns + "selectExcelWishJobList", dto);
    }

    // ===== Excel 빌더 - 자격증 =====

    /**
     * Excel 빌더용 자격증 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 자격증 목록
     */
    public List<AdminDTO> selectExcelCertificateList(AdminDTO dto) {
        log.info("AdminDAO selectExcelCertificateList");
        return sqlSession.selectList(ns + "selectExcelCertificateList", dto);
    }

    // ===== Excel 빌더 - 직업훈련 =====

    /**
     * Excel 빌더용 직업훈련 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 직업훈련 목록
     */
    public List<AdminDTO> selectExcelTrainingList(AdminDTO dto) {
        log.info("AdminDAO selectExcelTrainingList");
        return sqlSession.selectList(ns + "selectExcelTrainingList", dto);
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
