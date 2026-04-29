package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class AdminDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminDAO.";

    // ===== 사용자 관리 (J_참여자관리_로그인정보) =====
    public List<AdminDTO> selectUserList(AdminDTO dto) {
        log.info("AdminDAO selectUserList");
        return sqlSession.selectList(ns + "selectUserList", dto);
    }

    public AdminDTO selectUserOne(AdminDTO dto) {
        log.info("AdminDAO selectUserOne pk={}", dto.getMemberNo());
        return sqlSession.selectOne(ns + "selectUserOne", dto);
    }

    public boolean insertUser(AdminDTO dto) {
        log.info("AdminDAO insertUser");
        return sqlSession.insert(ns + "insertUser", dto) > 0;
    }

    public boolean updateUser(AdminDTO dto) {
        log.info("AdminDAO updateUser pk={}", dto.getMemberNo());
        return sqlSession.update(ns + "updateUser", dto) > 0;
    }

    public boolean deleteUser(AdminDTO dto) {
        log.info("AdminDAO deleteUser pk={}", dto.getMemberNo());
        return sqlSession.delete(ns + "deleteUser", dto) > 0;
    }

    public boolean resetUserPassword(AdminDTO dto) {
        log.info("AdminDAO resetUserPassword pk={}", dto.getMemberNo());
        return sqlSession.update(ns + "resetUserPassword", dto) > 0;
    }

    // ===== 지점 관리 (J_참여자관리_지점) =====
    public List<AdminDTO> selectBranchList(AdminDTO dto) {
        log.info("AdminDAO selectBranchList");
        return sqlSession.selectList(ns + "selectBranchList", dto);
    }

    public AdminDTO selectBranchOne(AdminDTO dto) {
        log.info("AdminDAO selectBranchOne pk={}", dto.getBranchNo());
        return sqlSession.selectOne(ns + "selectBranchOne", dto);
    }

    public boolean insertBranch(AdminDTO dto) {
        log.info("AdminDAO insertBranch");
        return sqlSession.insert(ns + "insertBranch", dto) > 0;
    }

    public boolean updateBranch(AdminDTO dto) {
        log.info("AdminDAO updateBranch pk={}", dto.getBranchNo());
        return sqlSession.update(ns + "updateBranch", dto) > 0;
    }

    public boolean deleteBranch(AdminDTO dto) {
        log.info("AdminDAO deleteBranch pk={}", dto.getBranchNo());
        return sqlSession.delete(ns + "deleteBranch", dto) > 0;
    }

    // ===== 참여자 관리 (J_참여자관리) =====
    public List<AdminDTO> selectParticipantList(AdminDTO dto) {
        log.info("AdminDAO selectParticipantList");
        return sqlSession.selectList(ns + "selectParticipantList", dto);
    }

    public AdminDTO selectParticipantOne(AdminDTO dto) {
        log.info("AdminDAO selectParticipantOne pk={}", dto.getJobNo());
        return sqlSession.selectOne(ns + "selectParticipantOne", dto);
    }

    public boolean deleteParticipant(AdminDTO dto) {
        log.info("AdminDAO deleteParticipant pk={}", dto.getJobNo());
        return sqlSession.delete(ns + "deleteParticipant", dto) > 0;
    }

    // ===== 일일업무보고 (J_참여자관리_일일업무보고) =====
    public List<AdminDTO> selectDailyReportList(AdminDTO dto) {
        log.info("AdminDAO selectDailyReportList");
        return sqlSession.selectList(ns + "selectDailyReportList", dto);
    }

    // ===== 기준금액 (J_참여자관리_기준금액) =====
    public List<AdminDTO> selectStandardAmountList(AdminDTO dto) {
        log.info("AdminDAO selectStandardAmountList");
        return sqlSession.selectList(ns + "selectStandardAmountList", dto);
    }

    public AdminDTO selectStandardAmountOne(AdminDTO dto) {
        log.info("AdminDAO selectStandardAmountOne pk={}", dto.getPk());
        return sqlSession.selectOne(ns + "selectStandardAmountOne", dto);
    }

    public boolean insertStandardAmount(AdminDTO dto) {
        log.info("AdminDAO insertStandardAmount");
        return sqlSession.insert(ns + "insertStandardAmount", dto) > 0;
    }

    public boolean updateStandardAmount(AdminDTO dto) {
        log.info("AdminDAO updateStandardAmount pk={}", dto.getPk());
        return sqlSession.update(ns + "updateStandardAmount", dto) > 0;
    }

    public boolean deleteStandardAmount(AdminDTO dto) {
        log.info("AdminDAO deleteStandardAmount pk={}", dto.getPk());
        return sqlSession.delete(ns + "deleteStandardAmount", dto) > 0;
    }

    // ===== 나은기준임금 (J_참여자관리_나은기준임금) =====
    public List<AdminDTO> selectBetterWageList(AdminDTO dto) {
        log.info("AdminDAO selectBetterWageList");
        return sqlSession.selectList(ns + "selectBetterWageList", dto);
    }

    public AdminDTO selectBetterWageOne(AdminDTO dto) {
        log.info("AdminDAO selectBetterWageOne pk={}", dto.getPk());
        return sqlSession.selectOne(ns + "selectBetterWageOne", dto);
    }

    public boolean insertBetterWage(AdminDTO dto) {
        log.info("AdminDAO insertBetterWage");
        return sqlSession.insert(ns + "insertBetterWage", dto) > 0;
    }

    public boolean updateBetterWage(AdminDTO dto) {
        log.info("AdminDAO updateBetterWage pk={}", dto.getPk());
        return sqlSession.update(ns + "updateBetterWage", dto) > 0;
    }

    public boolean deleteBetterWage(AdminDTO dto) {
        log.info("AdminDAO deleteBetterWage pk={}", dto.getPk());
        return sqlSession.delete(ns + "deleteBetterWage", dto) > 0;
    }

    // ===== 배정 CSV 히스토리 =====
    public List<AdminDTO> selectCsvHistoryList(AdminDTO dto) {
        log.info("AdminDAO selectCsvHistoryList");
        return sqlSession.selectList(ns + "selectCsvHistoryList", dto);
    }

    // ===== 배정 산정식 히스토리 =====
    public List<AdminDTO> selectFormulaHistoryList(AdminDTO dto) {
        log.info("AdminDAO selectFormulaHistoryList");
        return sqlSession.selectList(ns + "selectFormulaHistoryList", dto);
    }

    // ===== 알선 관리 (J_참여자관리_알선상세정보) =====
    public List<AdminDTO> selectJobPlacementList(AdminDTO dto) {
        log.info("AdminDAO selectJobPlacementList");
        return sqlSession.selectList(ns + "selectJobPlacementList", dto);
    }

    public AdminDTO selectJobPlacementOne(AdminDTO dto) {
        log.info("AdminDAO selectJobPlacementOne pk={}", dto.getRegistrationNo());
        return sqlSession.selectOne(ns + "selectJobPlacementOne", dto);
    }

    public boolean insertJobPlacement(AdminDTO dto) {
        log.info("AdminDAO insertJobPlacement");
        return sqlSession.insert(ns + "insertJobPlacement", dto) > 0;
    }

    public boolean updateJobPlacement(AdminDTO dto) {
        log.info("AdminDAO updateJobPlacement pk={}", dto.getRegistrationNo());
        return sqlSession.update(ns + "updateJobPlacement", dto) > 0;
    }

    public boolean deleteJobPlacement(AdminDTO dto) {
        log.info("AdminDAO deleteJobPlacement pk={}", dto.getRegistrationNo());
        return sqlSession.delete(ns + "deleteJobPlacement", dto) > 0;
    }

    // ===== 이력서 요청 (J_참여자관리_이력서요청양식) =====
    public List<AdminDTO> selectResumeRequestList(AdminDTO dto) {
        log.info("AdminDAO selectResumeRequestList");
        return sqlSession.selectList(ns + "selectResumeRequestList", dto);
    }

    public AdminDTO selectResumeRequestOne(AdminDTO dto) {
        log.info("AdminDAO selectResumeRequestOne pk={}", dto.getResumeRegNo());
        return sqlSession.selectOne(ns + "selectResumeRequestOne", dto);
    }

    public boolean updateResumeRequestStatus(AdminDTO dto) {
        log.info("AdminDAO updateResumeRequestStatus pk={}", dto.getResumeRegNo());
        return sqlSession.update(ns + "updateResumeRequestStatus", dto) > 0;
    }

    // ===== 자격증 (J_참여자관리_자격증) =====
    public List<AdminDTO> selectCertificateList(AdminDTO dto) {
        log.info("AdminDAO selectCertificateList");
        return sqlSession.selectList(ns + "selectCertificateList", dto);
    }

    public boolean insertCertificate(AdminDTO dto) {
        log.info("AdminDAO insertCertificate");
        return sqlSession.insert(ns + "insertCertificate", dto) > 0;
    }

    public boolean updateCertificate(AdminDTO dto) {
        log.info("AdminDAO updateCertificate pk={}", dto.getCertificateNo());
        return sqlSession.update(ns + "updateCertificate", dto) > 0;
    }

    public boolean deleteCertificate(AdminDTO dto) {
        log.info("AdminDAO deleteCertificate pk={}", dto.getCertificateNo());
        return sqlSession.delete(ns + "deleteCertificate", dto) > 0;
    }

    // ===== 직업훈련 (J_참여자관리_직업훈련) =====
    public List<AdminDTO> selectTrainingList(AdminDTO dto) {
        log.info("AdminDAO selectTrainingList");
        return sqlSession.selectList(ns + "selectTrainingList", dto);
    }

    public boolean insertTraining(AdminDTO dto) {
        log.info("AdminDAO insertTraining");
        return sqlSession.insert(ns + "insertTraining", dto) > 0;
    }

    public boolean updateTraining(AdminDTO dto) {
        log.info("AdminDAO updateTraining pk={}", dto.getTrainingNo());
        return sqlSession.update(ns + "updateTraining", dto) > 0;
    }

    public boolean deleteTraining(AdminDTO dto) {
        log.info("AdminDAO deleteTraining pk={}", dto.getTrainingNo());
        return sqlSession.delete(ns + "deleteTraining", dto) > 0;
    }

    // ===== 대시보드 KPI =====
    public AdminDTO selectDashboardKpi() {
        log.info("AdminDAO selectDashboardKpi");
        return sqlSession.selectOne(ns + "selectDashboardKpi");
    }

    public List<AdminDTO> selectBranchParticipantStats() {
        log.info("AdminDAO selectBranchParticipantStats");
        return sqlSession.selectList(ns + "selectBranchParticipantStats");
    }
}