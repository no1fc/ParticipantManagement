package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;
import java.util.Map;

public interface AdminService {
    // 사용자 관리
    List<AdminDTO> getUserList(AdminDTO dto);
    AdminDTO getUserOne(AdminDTO dto);
    boolean addUser(AdminDTO dto);
    boolean modifyUser(AdminDTO dto);
    boolean removeUser(AdminDTO dto);
    boolean resetPassword(AdminDTO dto);

    // 지점 관리
    List<AdminDTO> getBranchList(AdminDTO dto);
    AdminDTO getBranchOne(AdminDTO dto);
    boolean addBranch(AdminDTO dto);
    boolean modifyBranch(AdminDTO dto);
    boolean removeBranch(AdminDTO dto);

    // 참여자 관리 (관리자)
    List<AdminDTO> getParticipantList(AdminDTO dto);
    AdminDTO getParticipantOne(AdminDTO dto);
    boolean removeParticipant(AdminDTO dto);

    // 일일업무보고
    List<AdminDTO> getDailyReportList(AdminDTO dto);

    // 기준금액
    List<AdminDTO> getStandardAmountList(AdminDTO dto);
    AdminDTO getStandardAmountOne(AdminDTO dto);
    boolean addStandardAmount(AdminDTO dto);
    boolean modifyStandardAmount(AdminDTO dto);
    boolean removeStandardAmount(AdminDTO dto);

    // 나은기준임금
    List<AdminDTO> getBetterWageList(AdminDTO dto);
    AdminDTO getBetterWageOne(AdminDTO dto);
    boolean addBetterWage(AdminDTO dto);
    boolean modifyBetterWage(AdminDTO dto);
    boolean removeBetterWage(AdminDTO dto);

    // 배정 히스토리
    List<AdminDTO> getCsvHistoryList(AdminDTO dto);
    List<AdminDTO> getFormulaHistoryList(AdminDTO dto);

    // 알선 관리
    List<AdminDTO> getJobPlacementList(AdminDTO dto);
    AdminDTO getJobPlacementOne(AdminDTO dto);
    boolean addJobPlacement(AdminDTO dto);
    boolean modifyJobPlacement(AdminDTO dto);
    boolean removeJobPlacement(AdminDTO dto);

    // 이력서 요청
    List<AdminDTO> getResumeRequestList(AdminDTO dto);
    AdminDTO getResumeRequestOne(AdminDTO dto);
    boolean updateResumeStatus(AdminDTO dto);

    // 자격증
    List<AdminDTO> getCertificateList(AdminDTO dto);
    boolean addCertificate(AdminDTO dto);
    boolean modifyCertificate(AdminDTO dto);
    boolean removeCertificate(AdminDTO dto);

    // 직업훈련
    List<AdminDTO> getTrainingList(AdminDTO dto);
    boolean addTraining(AdminDTO dto);
    boolean modifyTraining(AdminDTO dto);
    boolean removeTraining(AdminDTO dto);

    // 대시보드 KPI
    Map<String, Object> getDashboardData(AdminDTO dto);

    // 상담사 목록
    List<AdminDTO> getCounselorList(AdminDTO dto);

    // 사용자 보조
    int getNextMemberNo();
    boolean checkUserIdExists(AdminDTO dto);

    // 참여자 Excel
    List<AdminDTO> getParticipantExcelList(AdminDTO dto);

    // 상담사별 통계
    List<AdminDTO> getPlacementStatsByCounselor(AdminDTO dto);

    // 참여자 Excel 전체 컬럼
    List<AdminDTO> getParticipantExcelFullList(AdminDTO dto);

    // Excel 빌더 - 시트별 데이터
    List<AdminDTO> getExcelWishJobList(AdminDTO dto);
    List<AdminDTO> getExcelCertificateList(AdminDTO dto);
    List<AdminDTO> getExcelTrainingList(AdminDTO dto);
}