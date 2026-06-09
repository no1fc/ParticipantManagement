package com.jobmoa.app.CounselMain.biz.schedule;

import java.util.List;

/**
 * 상담 일정 관리 서비스.
 * 일정 CRUD, 중복 검증, 드래그 이동, 지점별 통합 조회, 공개 일정 인증 기능을 제공한다.
 */
public interface ScheduleService {

    /**
     * 일정 목록을 조회한다.
     *
     * @param dto 조회 조건 (상담사 ID, 기간 등)
     * @return 일정 목록
     */
    List<ScheduleDTO> getScheduleList(ScheduleDTO dto);

    /**
     * 일정 상세 정보를 조회한다.
     *
     * @param dto 조회 조건 (일정 ID 등)
     * @return 일정 상세 정보
     */
    ScheduleDTO getScheduleDetail(ScheduleDTO dto);

    /**
     * 일정을 등록한다. 중복 일정이 존재하면 {@code -1}을 반환한다.
     *
     * @param dto 등록할 일정 정보
     * @return 등록된 일정 ID, 중복 시 {@code -1}
     */
    int insertSchedule(ScheduleDTO dto);

    /**
     * 일정을 수정한다. 중복 일정이 존재하면 {@code -1}을 반환한다.
     *
     * @param dto 수정할 일정 정보
     * @return 수정된 일정 ID, 중복 시 {@code -1}, 실패 시 {@code 0}
     */
    int updateSchedule(ScheduleDTO dto);

    /**
     * 일정을 삭제한다.
     *
     * @param dto 삭제 대상 일정 정보
     * @return 삭제 성공 여부
     */
    boolean deleteSchedule(ScheduleDTO dto);

    /**
     * 캘린더 드래그 앤 드롭으로 일정 시간을 변경한다.
     *
     * @param dto 변경할 일정 정보 (새 날짜, 시작/종료 시간 포함)
     * @return 변경된 일정 ID, 중복 시 {@code -1}, 실패 시 {@code 0}
     */
    int updateScheduleDrag(ScheduleDTO dto);

    /**
     * 동일 시간대에 중복 일정이 존재하는지 확인한다.
     *
     * @param dto 확인할 일정 조건
     * @return 중복 존재 여부
     */
    boolean checkDuplicate(ScheduleDTO dto);

    /**
     * 참여자를 검색한다 (일정 등록 시 참여자 검색용).
     *
     * @param dto 검색 조건
     * @return 검색된 참여자 목록
     */
    List<ScheduleDTO> searchParticipant(ScheduleDTO dto);

    /**
     * 오늘의 일정 목록을 조회한다.
     *
     * @param dto 조회 조건 (상담사 ID 등)
     * @return 오늘의 일정 목록
     */
    List<ScheduleDTO> getTodaySchedule(ScheduleDTO dto);

    /**
     * 알림 대상 일정 목록을 조회한다.
     *
     * @return 알림 대상 일정 목록
     */
    List<ScheduleDTO> getAlertTargets();

    /**
     * 관리자 지점별 일정 통합 목록을 조회한다.
     *
     * @param dto 조회 조건 (지점, 기간 등)
     * @return 지점별 일정 목록
     */
    List<ScheduleDTO> getBranchScheduleList(ScheduleDTO dto);

    /**
     * 일정 통계 정보를 조회한다.
     *
     * @param dto 조회 조건 (지점, 기간 등)
     * @return 일정 통계 정보
     */
    ScheduleDTO getScheduleStats(ScheduleDTO dto);

    /**
     * 지점 소속 상담사 목록을 조회한다.
     *
     * @param dto 조회 조건 (지점 등)
     * @return 상담사 목록
     */
    List<ScheduleDTO> getCounselorsByBranch(ScheduleDTO dto);

    /**
     * 공개 일정 접근 인증을 확인한다.
     *
     * @param dto 인증 조건
     * @return 인증 성공 여부
     */
    boolean selectVerifyPublicAccess(ScheduleDTO dto);
}
