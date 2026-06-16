package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AdminService} 구현체.
 * <p>AdminDAO를 통해 기준금액, 나은기준임금, 배정 히스토리, 자격증, 직업훈련,
 * 대시보드 KPI, 상담사 목록, 참여자 Excel, 상담사별 통계, 연계 현황,
 * 운영 현황 대시보드 등 공통/보존 기능을 처리한다.</p>
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDAO adminDAO;

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

    // ===== 참여자 Excel =====
    @Override
    public List<AdminDTO> getParticipantExcelList(AdminDTO dto) {
        return adminDAO.selectParticipantExcelList(dto);
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

    // ===== 상담사별 통계 =====
    @Override
    public List<AdminDTO> getPlacementStatsByCounselor(AdminDTO dto) {
        return adminDAO.selectPlacementStatsByCounselor(dto);
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
