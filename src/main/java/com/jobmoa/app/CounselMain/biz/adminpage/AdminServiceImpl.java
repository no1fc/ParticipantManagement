package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ===== 사용자 관리 =====
    @Override
    public List<AdminDTO> getUserList(AdminDTO dto) {
        return adminDAO.selectUserList(dto);
    }

    @Override
    public AdminDTO getUserOne(AdminDTO dto) {
        return adminDAO.selectUserOne(dto);
    }

    @Override
    public boolean addUser(AdminDTO dto) {
        if (dto.getPassword() != null && !dto.getPassword().startsWith("$2a$")) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return adminDAO.insertUser(dto);
    }

    @Override
    public boolean modifyUser(AdminDTO dto) {
        return adminDAO.updateUser(dto);
    }

    @Override
    public boolean removeUser(AdminDTO dto) {
        return adminDAO.deleteUser(dto);
    }

    @Override
    public boolean resetPassword(AdminDTO dto) {
        dto.setPassword("");
        return adminDAO.resetUserPassword(dto);
    }

    @Override
    public boolean approveUser(AdminDTO dto) {
        return adminDAO.approveUser(dto);
    }

    // ===== 지점 관리 =====
    @Override
    public List<AdminDTO> getBranchList(AdminDTO dto) {
        return adminDAO.selectBranchList(dto);
    }

    @Override
    public AdminDTO getBranchOne(AdminDTO dto) {
        return adminDAO.selectBranchOne(dto);
    }

    @Override
    public boolean addBranch(AdminDTO dto) {
        return adminDAO.insertBranch(dto);
    }

    @Override
    public boolean modifyBranch(AdminDTO dto) {
        return adminDAO.updateBranch(dto);
    }

    @Override
    public boolean removeBranch(AdminDTO dto) {
        return adminDAO.deleteBranch(dto);
    }

    @Override
    public int getBranchUserCount(AdminDTO dto) {
        return adminDAO.selectBranchUserCount(dto);
    }

    // ===== 참여자 관리 =====
    @Override
    public List<AdminDTO> getParticipantList(AdminDTO dto) {
        return adminDAO.selectParticipantList(dto);
    }

    @Override
    public AdminDTO getParticipantOne(AdminDTO dto) {
        return adminDAO.selectParticipantOne(dto);
    }

    @Override
    public boolean removeParticipant(AdminDTO dto) {
        return adminDAO.deleteParticipant(dto);
    }

    // ===== 일일업무보고 =====
    @Override
    public List<AdminDTO> getDailyReportList(AdminDTO dto) {
        return adminDAO.selectDailyReportList(dto);
    }

    // ===== 기준금액 =====
    @Override
    public List<AdminDTO> getStandardAmountList(AdminDTO dto) {
        return adminDAO.selectStandardAmountList(dto);
    }

    @Override
    public AdminDTO getStandardAmountOne(AdminDTO dto) {
        return adminDAO.selectStandardAmountOne(dto);
    }

    @Override
    public boolean addStandardAmount(AdminDTO dto) {
        return adminDAO.insertStandardAmount(dto);
    }

    @Override
    public boolean modifyStandardAmount(AdminDTO dto) {
        return adminDAO.updateStandardAmount(dto);
    }

    @Override
    public boolean removeStandardAmount(AdminDTO dto) {
        return adminDAO.deleteStandardAmount(dto);
    }

    // ===== 나은기준임금 =====
    @Override
    public List<AdminDTO> getBetterWageList(AdminDTO dto) {
        return adminDAO.selectBetterWageList(dto);
    }

    @Override
    public AdminDTO getBetterWageOne(AdminDTO dto) {
        return adminDAO.selectBetterWageOne(dto);
    }

    @Override
    public boolean addBetterWage(AdminDTO dto) {
        return adminDAO.insertBetterWage(dto);
    }

    @Override
    public boolean modifyBetterWage(AdminDTO dto) {
        return adminDAO.updateBetterWage(dto);
    }

    @Override
    public boolean removeBetterWage(AdminDTO dto) {
        return adminDAO.deleteBetterWage(dto);
    }

    // ===== 배정 히스토리 =====
    @Override
    public List<AdminDTO> getCsvHistoryList(AdminDTO dto) {
        return adminDAO.selectCsvHistoryList(dto);
    }

    @Override
    public List<AdminDTO> getFormulaHistoryList(AdminDTO dto) {
        return adminDAO.selectFormulaHistoryList(dto);
    }

    // ===== 알선 관리 =====
    @Override
    public List<AdminDTO> getJobPlacementList(AdminDTO dto) {
        return adminDAO.selectJobPlacementList(dto);
    }

    @Override
    public AdminDTO getJobPlacementOne(AdminDTO dto) {
        return adminDAO.selectJobPlacementOne(dto);
    }

    @Override
    public boolean addJobPlacement(AdminDTO dto) {
        return adminDAO.insertJobPlacement(dto);
    }

    @Override
    public boolean modifyJobPlacement(AdminDTO dto) {
        return adminDAO.updateJobPlacement(dto);
    }

    @Override
    public boolean removeJobPlacement(AdminDTO dto) {
        return adminDAO.deleteJobPlacement(dto);
    }

    // ===== 이력서 요청 =====
    @Override
    public List<AdminDTO> getResumeRequestList(AdminDTO dto) {
        return adminDAO.selectResumeRequestList(dto);
    }

    @Override
    public AdminDTO getResumeRequestOne(AdminDTO dto) {
        return adminDAO.selectResumeRequestOne(dto);
    }

    @Override
    public boolean updateResumeStatus(AdminDTO dto) {
        return adminDAO.updateResumeRequestStatus(dto);
    }

    // ===== 자격증 =====
    @Override
    public List<AdminDTO> getCertificateList(AdminDTO dto) {
        return adminDAO.selectCertificateList(dto);
    }

    @Override
    public boolean addCertificate(AdminDTO dto) {
        return adminDAO.insertCertificate(dto);
    }

    @Override
    public boolean modifyCertificate(AdminDTO dto) {
        return adminDAO.updateCertificate(dto);
    }

    @Override
    public boolean removeCertificate(AdminDTO dto) {
        return adminDAO.deleteCertificate(dto);
    }

    // ===== 직업훈련 =====
    @Override
    public List<AdminDTO> getTrainingList(AdminDTO dto) {
        return adminDAO.selectTrainingList(dto);
    }

    @Override
    public boolean addTraining(AdminDTO dto) {
        return adminDAO.insertTraining(dto);
    }

    @Override
    public boolean modifyTraining(AdminDTO dto) {
        return adminDAO.updateTraining(dto);
    }

    @Override
    public boolean removeTraining(AdminDTO dto) {
        return adminDAO.deleteTraining(dto);
    }

    // ===== 대시보드 KPI =====
    @Override
    public Map<String, Object> getDashboardData(AdminDTO dto) {
        Map<String, Object> result = new HashMap<>();
        AdminDTO kpi = adminDAO.selectDashboardKpi(dto);
        List<AdminDTO> branchStats = adminDAO.selectBranchParticipantStats(dto);
        result.put("kpi", kpi);
        result.put("branchStats", branchStats);
        return result;
    }

    // ===== 상담사 목록 =====
    @Override
    public List<AdminDTO> getCounselorList(AdminDTO dto) {
        return adminDAO.selectCounselorsByBranch(dto);
    }

    // ===== 사용자 보조 =====
    @Override
    public int getNextMemberNo() {
        return adminDAO.selectNextMemberNo();
    }

    @Override
    public boolean checkUserIdExists(AdminDTO dto) {
        return adminDAO.selectUserIdExists(dto) > 0;
    }

    // ===== 참여자 Excel =====
    @Override
    public List<AdminDTO> getParticipantExcelList(AdminDTO dto) {
        return adminDAO.selectParticipantExcelList(dto);
    }

    // ===== 상담사별 통계 =====
    @Override
    public List<AdminDTO> getPlacementStatsByCounselor(AdminDTO dto) {
        return adminDAO.selectPlacementStatsByCounselor(dto);
    }

    // ===== 참여자 Excel 전체 컬럼 =====
    @Override
    public List<AdminDTO> getParticipantExcelFullList(AdminDTO dto) {
        return adminDAO.selectParticipantExcelFullList(dto);
    }

    // ===== Excel 빌더 - 희망직무 =====
    @Override
    public List<AdminDTO> getExcelWishJobList(AdminDTO dto) {
        return adminDAO.selectExcelWishJobList(dto);
    }

    // ===== Excel 빌더 - 자격증 =====
    @Override
    public List<AdminDTO> getExcelCertificateList(AdminDTO dto) {
        return adminDAO.selectExcelCertificateList(dto);
    }

    // ===== Excel 빌더 - 직업훈련 =====
    @Override
    public List<AdminDTO> getExcelTrainingList(AdminDTO dto) {
        return adminDAO.selectExcelTrainingList(dto);
    }

    // ===== 연계 현황 =====
    @Override
    public Map<String, Object> getLinkageStats(AdminDTO dto) {
        Map<String, Object> result = new HashMap<>();
        List<AdminDTO> branchStats = adminDAO.selectLinkageStatsByBranch(dto);
        List<AdminDTO> typeStats = adminDAO.selectLinkageStatsByType(dto);
        int totalCount = branchStats.stream().mapToInt(AdminDTO::getLinkageCount).sum();
        result.put("branchStats", branchStats);
        result.put("typeStats", typeStats);
        result.put("totalCount", totalCount);
        return result;
    }

    @Override
    public List<AdminDTO> getLinkageByCounselor(AdminDTO dto) {
        return adminDAO.selectLinkageStatsByCounselor(dto);
    }

    @Override
    public List<AdminDTO> getLinkageByType(AdminDTO dto) {
        return adminDAO.selectLinkageStatsByType(dto);
    }

    // ===== 운영 현황 대시보드 =====
    @Override
    public List<AdminDTO> getManagementDashboardData(AdminDTO dto) {
        if (dto.getSearchYear() == null || dto.getSearchYear().isEmpty()) {
            dto.setSearchYear(String.valueOf(java.time.Year.now().getValue()));
        }
        return adminDAO.selectManagementDashboardData(dto);
    }
}