package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 페이지 데이터 접근 객체.
 * <p>사용자 관리, 지점 관리, 참여자 관리, 일일업무보고, 기준금액, 나은기준임금,
 * 배정 히스토리, 알선 관리, 이력서 요청, 자격증, 직업훈련, 대시보드 KPI,
 * 연계 현황, 운영 현황 등 관리자 기능 전반의 SQL 매핑을 담당한다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "AdminDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class AdminDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminDAO.";

    // ===== 사용자 관리 (J_참여자관리_로그인정보) =====

    /**
     * 사용자(로그인정보) 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 조건에 맞는 사용자 목록
     */
    public List<AdminDTO> selectUserList(AdminDTO dto) {
        log.info("AdminDAO selectUserList");
        return sqlSession.selectList(ns + "selectUserList", dto);
    }

    /**
     * 사용자 단건 상세 정보를 조회한다.
     *
     * @param dto memberNo(회원번호)가 설정된 DTO
     * @return 해당 사용자 정보, 없으면 null
     */
    public AdminDTO selectUserOne(AdminDTO dto) {
        log.info("AdminDAO selectUserOne pk={}", dto.getMemberNo());
        return sqlSession.selectOne(ns + "selectUserOne", dto);
    }

    /**
     * 신규 사용자를 등록한다.
     *
     * @param dto 등록할 사용자 정보
     * @return 등록 성공 여부
     */
    public boolean insertUser(AdminDTO dto) {
        log.info("AdminDAO insertUser");
        return sqlSession.insert(ns + "insertUser", dto) > 0;
    }

    /**
     * 사용자 정보를 수정한다.
     *
     * @param dto memberNo(회원번호)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateUser(AdminDTO dto) {
        log.info("AdminDAO updateUser pk={}", dto.getMemberNo());
        return sqlSession.update(ns + "updateUser", dto) > 0;
    }

    /**
     * 사용자를 삭제한다.
     *
     * @param dto memberNo(회원번호)가 설정된 삭제 대상 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteUser(AdminDTO dto) {
        log.info("AdminDAO deleteUser pk={}", dto.getMemberNo());
        return sqlSession.delete(ns + "deleteUser", dto) > 0;
    }

    /**
     * 사용자 비밀번호를 초기화한다.
     *
     * @param dto memberNo(회원번호)가 설정된 DTO
     * @return 초기화 성공 여부
     */
    public boolean resetUserPassword(AdminDTO dto) {
        log.info("AdminDAO resetUserPassword pk={}", dto.getMemberNo());
        return sqlSession.update(ns + "resetUserPassword", dto) > 0;
    }

    /**
     * 사용자 가입 승인 처리를 수행한다.
     *
     * @param dto memberNo(회원번호)가 설정된 DTO
     * @return 승인 성공 여부
     */
    public boolean approveUser(AdminDTO dto) {
        log.info("AdminDAO approveUser pk={}", dto.getMemberNo());
        return sqlSession.update(ns + "approveUser", dto) > 0;
    }

    // ===== 지점 관리 (J_참여자관리_지점) =====

    /**
     * 지점 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 조건에 맞는 지점 목록
     */
    public List<AdminDTO> selectBranchList(AdminDTO dto) {
        log.info("AdminDAO selectBranchList");
        return sqlSession.selectList(ns + "selectBranchList", dto);
    }

    /**
     * 지점 단건 상세 정보를 조회한다.
     *
     * @param dto branchNo(지점번호)가 설정된 DTO
     * @return 해당 지점 정보, 없으면 null
     */
    public AdminDTO selectBranchOne(AdminDTO dto) {
        log.info("AdminDAO selectBranchOne pk={}", dto.getBranchNo());
        return sqlSession.selectOne(ns + "selectBranchOne", dto);
    }

    /**
     * 신규 지점을 등록한다.
     *
     * @param dto 등록할 지점 정보
     * @return 등록 성공 여부
     */
    public boolean insertBranch(AdminDTO dto) {
        log.info("AdminDAO insertBranch");
        return sqlSession.insert(ns + "insertBranch", dto) > 0;
    }

    /**
     * 지점 정보를 수정한다.
     *
     * @param dto branchNo(지점번호)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateBranch(AdminDTO dto) {
        log.info("AdminDAO updateBranch pk={}", dto.getBranchNo());
        return sqlSession.update(ns + "updateBranch", dto) > 0;
    }

    /**
     * 지점에 소속된 사용자 수를 조회한다.
     *
     * @param dto branchNo(지점번호)가 설정된 DTO
     * @return 해당 지점의 소속 사용자 수
     */
    public int selectBranchUserCount(AdminDTO dto) {
        log.info("AdminDAO selectBranchUserCount pk={}", dto.getBranchNo());
        return sqlSession.selectOne(ns + "selectBranchUserCount", dto);
    }

    /**
     * 지점을 소프트 삭제(비활성화)한다.
     *
     * @param dto branchNo(지점번호)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteBranch(AdminDTO dto) {
        log.info("AdminDAO deleteBranch (soft) pk={}", dto.getBranchNo());
        return sqlSession.update(ns + "deleteBranch", dto) > 0;
    }

    // ===== 참여자 관리 (J_참여자관리) =====

    /**
     * 참여자 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 조건에 맞는 참여자 목록
     */
    public List<AdminDTO> selectParticipantList(AdminDTO dto) {
        log.info("AdminDAO selectParticipantList");
        return sqlSession.selectList(ns + "selectParticipantList", dto);
    }

    /**
     * 참여자 단건 상세 정보를 조회한다.
     *
     * @param dto jobNo(구직번호)가 설정된 DTO
     * @return 해당 참여자 정보, 없으면 null
     */
    public AdminDTO selectParticipantOne(AdminDTO dto) {
        log.info("AdminDAO selectParticipantOne pk={}", dto.getJobNo());
        return sqlSession.selectOne(ns + "selectParticipantOne", dto);
    }

    /**
     * 참여자를 삭제한다.
     *
     * @param dto jobNo(구직번호)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteParticipant(AdminDTO dto) {
        log.info("AdminDAO deleteParticipant pk={}", dto.getJobNo());
        return sqlSession.delete(ns + "deleteParticipant", dto) > 0;
    }

    // ===== 일일업무보고 (J_참여자관리_일일업무보고) =====

    /**
     * 일일업무보고 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 일일업무보고 목록
     */
    public List<AdminDTO> selectDailyReportList(AdminDTO dto) {
        log.info("AdminDAO selectDailyReportList");
        return sqlSession.selectList(ns + "selectDailyReportList", dto);
    }

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

    // ===== 알선 관리 (J_참여자관리_알선상세정보) =====

    /**
     * 알선 상세정보 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 알선 목록
     */
    public List<AdminDTO> selectJobPlacementList(AdminDTO dto) {
        log.info("AdminDAO selectJobPlacementList");
        return sqlSession.selectList(ns + "selectJobPlacementList", dto);
    }

    /**
     * 알선 단건 상세 정보를 조회한다.
     *
     * @param dto registrationNo(등록번호)가 설정된 DTO
     * @return 해당 알선 정보, 없으면 null
     */
    public AdminDTO selectJobPlacementOne(AdminDTO dto) {
        log.info("AdminDAO selectJobPlacementOne pk={}", dto.getRegistrationNo());
        return sqlSession.selectOne(ns + "selectJobPlacementOne", dto);
    }

    /**
     * 신규 알선 정보를 등록한다.
     *
     * @param dto 등록할 알선 정보
     * @return 등록 성공 여부
     */
    public boolean insertJobPlacement(AdminDTO dto) {
        log.info("AdminDAO insertJobPlacement");
        return sqlSession.insert(ns + "insertJobPlacement", dto) > 0;
    }

    /**
     * 알선 정보를 수정한다.
     *
     * @param dto registrationNo(등록번호)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateJobPlacement(AdminDTO dto) {
        log.info("AdminDAO updateJobPlacement pk={}", dto.getRegistrationNo());
        return sqlSession.update(ns + "updateJobPlacement", dto) > 0;
    }

    /**
     * 알선 정보를 삭제한다.
     *
     * @param dto registrationNo(등록번호)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteJobPlacement(AdminDTO dto) {
        log.info("AdminDAO deleteJobPlacement pk={}", dto.getRegistrationNo());
        return sqlSession.delete(ns + "deleteJobPlacement", dto) > 0;
    }

    // ===== 이력서 요청 (J_참여자관리_이력서요청양식) =====

    /**
     * 이력서 요청 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 이력서 요청 목록
     */
    public List<AdminDTO> selectResumeRequestList(AdminDTO dto) {
        log.info("AdminDAO selectResumeRequestList");
        return sqlSession.selectList(ns + "selectResumeRequestList", dto);
    }

    /**
     * 이력서 요청 단건 상세 정보를 조회한다.
     *
     * @param dto resumeRegNo(이력서등록번호)가 설정된 DTO
     * @return 해당 이력서 요청 정보, 없으면 null
     */
    public AdminDTO selectResumeRequestOne(AdminDTO dto) {
        log.info("AdminDAO selectResumeRequestOne pk={}", dto.getResumeRegNo());
        return sqlSession.selectOne(ns + "selectResumeRequestOne", dto);
    }

    /**
     * 이력서 요청의 상태를 변경한다.
     *
     * @param dto resumeRegNo(이력서등록번호)와 상태값이 설정된 DTO
     * @return 상태 변경 성공 여부
     */
    public boolean updateResumeRequestStatus(AdminDTO dto) {
        log.info("AdminDAO updateResumeRequestStatus pk={}", dto.getResumeRegNo());
        return sqlSession.update(ns + "updateResumeRequestStatus", dto) > 0;
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

    // ===== 사용자 보조 =====

    /**
     * 다음 회원번호 시퀀스 값을 조회한다.
     *
     * @return 다음 회원번호
     */
    public int selectNextMemberNo() {
        log.info("AdminDAO selectNextMemberNo");
        return sqlSession.selectOne(ns + "selectNextMemberNo");
    }

    /**
     * 사용자 ID 중복 여부를 확인한다.
     *
     * @param dto userId(사용자ID)가 설정된 DTO
     * @return 중복 건수 (0이면 사용 가능)
     */
    public int selectUserIdExists(AdminDTO dto) {
        log.info("AdminDAO selectUserIdExists userId={}", dto.getUserId());
        return sqlSession.selectOne(ns + "selectUserIdExists", dto);
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