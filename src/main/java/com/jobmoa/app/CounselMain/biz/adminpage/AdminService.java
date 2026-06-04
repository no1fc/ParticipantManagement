package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;
import java.util.Map;

/**
 * 관리자 페이지 서비스 인터페이스.
 * 사용자 관리, 지점 관리, 참여자 관리, 기준금액, 알선, 이력서 요청,
 * 자격증, 직업훈련, 대시보드 KPI, Excel 출력 등 관리자 기능 전반을 정의한다.
 */
public interface AdminService {

    // ===== 사용자 관리 =====

    /**
     * 사용자 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 사용자 목록
     */
    List<AdminDTO> getUserList(AdminDTO dto);

    /**
     * 사용자 단건을 조회한다.
     * @param dto 조회 대상 사용자 정보가 담긴 DTO
     * @return 사용자 상세 정보
     */
    AdminDTO getUserOne(AdminDTO dto);

    /**
     * 신규 사용자를 등록한다.
     * @param dto 등록할 사용자 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addUser(AdminDTO dto);

    /**
     * 사용자 정보를 수정한다.
     * @param dto 수정할 사용자 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyUser(AdminDTO dto);

    /**
     * 사용자를 삭제한다.
     * @param dto 삭제 대상 사용자 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeUser(AdminDTO dto);

    /**
     * 사용자 비밀번호를 초기화한다.
     * @param dto 대상 사용자 정보가 담긴 DTO
     * @return 초기화 성공 여부
     */
    boolean resetPassword(AdminDTO dto);

    /**
     * 사용자 계정을 승인 처리한다.
     * @param dto 승인 대상 사용자 정보가 담긴 DTO
     * @return 승인 성공 여부
     */
    boolean approveUser(AdminDTO dto);

    // ===== 지점 관리 =====

    /**
     * 지점 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 지점 목록
     */
    List<AdminDTO> getBranchList(AdminDTO dto);

    /**
     * 지점 단건을 조회한다.
     * @param dto 조회 대상 지점 정보가 담긴 DTO
     * @return 지점 상세 정보
     */
    AdminDTO getBranchOne(AdminDTO dto);

    /**
     * 신규 지점을 등록한다.
     * @param dto 등록할 지점 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addBranch(AdminDTO dto);

    /**
     * 지점 정보를 수정한다.
     * @param dto 수정할 지점 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyBranch(AdminDTO dto);

    /**
     * 지점을 삭제한다.
     * @param dto 삭제 대상 지점 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeBranch(AdminDTO dto);

    /**
     * 특정 지점에 소속된 사용자 수를 조회한다.
     * @param dto 지점 정보가 담긴 DTO
     * @return 소속 사용자 수
     */
    int getBranchUserCount(AdminDTO dto);

    // ===== 참여자 관리 (관리자) =====

    /**
     * 참여자 목록을 조회한다 (관리자 전용).
     * @param dto 검색 조건이 담긴 DTO
     * @return 참여자 목록
     */
    List<AdminDTO> getParticipantList(AdminDTO dto);

    /**
     * 참여자 단건을 조회한다 (관리자 전용).
     * @param dto 조회 대상 참여자 정보가 담긴 DTO
     * @return 참여자 상세 정보
     */
    AdminDTO getParticipantOne(AdminDTO dto);

    /**
     * 참여자를 삭제한다 (관리자 전용).
     * @param dto 삭제 대상 참여자 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeParticipant(AdminDTO dto);

    // ===== 일일업무보고 =====

    /**
     * 일일업무보고 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 일일업무보고 목록
     */
    List<AdminDTO> getDailyReportList(AdminDTO dto);

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

    // ===== 알선 관리 =====

    /**
     * 알선 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 알선 목록
     */
    List<AdminDTO> getJobPlacementList(AdminDTO dto);

    /**
     * 알선 단건을 조회한다.
     * @param dto 조회 대상 알선 정보가 담긴 DTO
     * @return 알선 상세 정보
     */
    AdminDTO getJobPlacementOne(AdminDTO dto);

    /**
     * 알선 정보를 등록한다.
     * @param dto 등록할 알선 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addJobPlacement(AdminDTO dto);

    /**
     * 알선 정보를 수정한다.
     * @param dto 수정할 알선 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyJobPlacement(AdminDTO dto);

    /**
     * 알선 정보를 삭제한다.
     * @param dto 삭제 대상 알선 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeJobPlacement(AdminDTO dto);

    // ===== 이력서 요청 =====

    /**
     * 이력서 요청 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 이력서 요청 목록
     */
    List<AdminDTO> getResumeRequestList(AdminDTO dto);

    /**
     * 이력서 요청 단건을 조회한다.
     * @param dto 조회 대상 이력서 요청 정보가 담긴 DTO
     * @return 이력서 요청 상세 정보
     */
    AdminDTO getResumeRequestOne(AdminDTO dto);

    /**
     * 이력서 요청 상태를 변경한다.
     * @param dto 상태 변경 정보가 담긴 DTO
     * @return 변경 성공 여부
     */
    boolean updateResumeStatus(AdminDTO dto);

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

    // ===== 사용자 보조 =====

    /**
     * 다음 회원번호를 채번한다.
     * @return 다음 회원번호
     */
    int getNextMemberNo();

    /**
     * 사용자 ID 중복 여부를 확인한다.
     * @param dto 확인할 사용자 ID가 담긴 DTO
     * @return 중복이면 true, 아니면 false
     */
    boolean checkUserIdExists(AdminDTO dto);

    // ===== 참여자 Excel =====

    /**
     * 참여자 Excel 출력용 목록을 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 참여자 Excel 데이터 목록
     */
    List<AdminDTO> getParticipantExcelList(AdminDTO dto);

    // ===== 상담사별 통계 =====

    /**
     * 상담사별 알선 통계를 조회한다.
     * @param dto 검색 조건이 담긴 DTO
     * @return 상담사별 통계 목록
     */
    List<AdminDTO> getPlacementStatsByCounselor(AdminDTO dto);

    // ===== 참여자 Excel 전체 컬럼 =====

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