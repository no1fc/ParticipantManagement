package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AdminService} 구현체.
 * <p>AdminDAO를 통해 대시보드 KPI, 상담사 목록, 참여자 Excel, 상담사별 통계,
 * 연계 현황, 운영 현황 대시보드 등 공통 기능을 처리한다.</p>
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDAO adminDAO;

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

    // ===== 참여자 Excel 스트리밍 (대량 다운로드 메모리 최적화) =====
    @Override
    public void getParticipantExcelListStream(AdminDTO dto, ResultHandler<AdminDTO> handler) {
        adminDAO.selectParticipantExcelListStream(dto, handler);
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
