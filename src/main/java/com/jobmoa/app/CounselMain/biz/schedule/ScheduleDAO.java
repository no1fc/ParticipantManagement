package com.jobmoa.app.CounselMain.biz.schedule;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상담 일정 데이터 접근 객체.
 * <p>상담사 일정 조회, 등록, 수정, 삭제, 드래그 이동, 중복 체크,
 * 참여자 검색, 오늘 일정 조회, 알림 대상 조회, 지점별 통합 조회,
 * 공개 일정 인증 등을 담당한다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "ScheduleDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class ScheduleDAO {

    @Autowired
    @Qualifier("mybatis")
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ScheduleDAO.";

    /**
     * 상담사의 일정 목록을 조회한다.
     *
     * @param dto counselorId(상담사ID)가 설정된 DTO
     * @return 일정 목록
     */
    public List<ScheduleDTO> selectScheduleList(ScheduleDTO dto) {
        log.info("ScheduleDAO selectScheduleList counselorId={}", dto.getCounselorId());
        return sqlSession.selectList(ns + "selectScheduleList", dto);
    }

    /**
     * 일정 상세 정보를 조회한다.
     *
     * @param dto scheduleId(일정ID)가 설정된 DTO
     * @return 일정 상세 정보, 없으면 null
     */
    public ScheduleDTO selectScheduleDetail(ScheduleDTO dto) {
        log.info("ScheduleDAO selectScheduleDetail scheduleId={}", dto.getScheduleId());
        return sqlSession.selectOne(ns + "selectScheduleDetail", dto);
    }

    /**
     * 신규 일정을 등록한다.
     *
     * @param dto 등록할 일정 정보
     * @return 영향받은 행 수
     */
    public int insertSchedule(ScheduleDTO dto) {
        log.info("ScheduleDAO insertSchedule");
        int result = sqlSession.insert(ns + "insertSchedule", dto);
        return result;
    }

    /**
     * 일정 정보를 수정한다.
     *
     * @param dto scheduleId(일정ID)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateSchedule(ScheduleDTO dto) {
        log.info("ScheduleDAO updateSchedule scheduleId={}", dto.getScheduleId());
        return sqlSession.update(ns + "updateSchedule", dto) > 0;
    }

    /**
     * 일정을 삭제(소프트 삭제)한다.
     *
     * @param dto scheduleId(일정ID)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteSchedule(ScheduleDTO dto) {
        log.info("ScheduleDAO deleteSchedule scheduleId={}", dto.getScheduleId());
        return sqlSession.update(ns + "deleteSchedule", dto) > 0;
    }

    /**
     * 드래그 앤 드롭으로 일정 날짜/시간을 변경한다.
     *
     * @param dto scheduleId(일정ID) 및 변경된 날짜/시간이 설정된 DTO
     * @return 수정 성공 여부
     */
    public boolean updateScheduleDrag(ScheduleDTO dto) {
        log.info("ScheduleDAO updateScheduleDrag scheduleId={}", dto.getScheduleId());
        return sqlSession.update(ns + "updateScheduleDrag", dto) > 0;
    }

    /**
     * 일정 중복 여부를 확인한다.
     *
     * @param dto counselorId(상담사ID)와 scheduleDate(일정일자)가 설정된 DTO
     * @return 중복 건수
     */
    public int selectDuplicateCheck(ScheduleDTO dto) {
        log.info("ScheduleDAO selectDuplicateCheck counselorId={}, date={}", dto.getCounselorId(), dto.getScheduleDate());
        return sqlSession.selectOne(ns + "selectDuplicateCheck", dto);
    }

    /**
     * 일정 등록 시 참여자를 검색한다.
     *
     * @param dto keyword(검색어)가 설정된 DTO
     * @return 검색된 참여자 목록
     */
    public List<ScheduleDTO> selectParticipantSearch(ScheduleDTO dto) {
        log.info("ScheduleDAO selectParticipantSearch keyword={}", dto.getKeyword());
        return sqlSession.selectList(ns + "selectParticipantSearch", dto);
    }

    /**
     * 상담사의 오늘 일정 목록을 조회한다.
     *
     * @param dto counselorId(상담사ID)가 설정된 DTO
     * @return 오늘 일정 목록
     */
    public List<ScheduleDTO> selectTodaySchedule(ScheduleDTO dto) {
        log.info("ScheduleDAO selectTodaySchedule counselorId={}", dto.getCounselorId());
        return sqlSession.selectList(ns + "selectTodaySchedule", dto);
    }

    /**
     * 알림 발송 대상 일정 목록을 조회한다.
     *
     * @return 알림 대상 일정 목록
     */
    public List<ScheduleDTO> selectAlertTargets() {
        log.info("ScheduleDAO selectAlertTargets");
        return sqlSession.selectList(ns + "selectAlertTargets");
    }

    // ===== 2단계: 관리자 지점 일정 통합 조회 =====

    /**
     * 지점별 일정 목록을 통합 조회한다.
     *
     * @param dto branch(지점)가 설정된 DTO
     * @return 지점별 일정 목록
     */
    public List<ScheduleDTO> selectScheduleByBranch(ScheduleDTO dto) {
        log.info("ScheduleDAO selectScheduleByBranch branch={}", dto.getBranch());
        return sqlSession.selectList(ns + "selectScheduleByBranch", dto);
    }

    /**
     * 지점별 일정 통계를 조회한다.
     *
     * @param dto branch(지점)가 설정된 DTO
     * @return 일정 통계 정보
     */
    public ScheduleDTO selectScheduleStats(ScheduleDTO dto) {
        log.info("ScheduleDAO selectScheduleStats branch={}", dto.getBranch());
        return sqlSession.selectOne(ns + "selectScheduleStats", dto);
    }

    /**
     * 지점별 상담사 목록을 조회한다.
     *
     * @param dto branch(지점)가 설정된 DTO
     * @return 해당 지점의 상담사 목록
     */
    public List<ScheduleDTO> selectCounselorsByBranch(ScheduleDTO dto) {
        log.info("ScheduleDAO selectCounselorsByBranch branch={}", dto.getBranch());
        return sqlSession.selectList(ns + "selectCounselorsByBranch", dto);
    }

    // ===== 3단계: 공개 일정 인증 =====

    /**
     * 공개 일정 접근 인증을 검증한다.
     *
     * @param dto branch(지점) 및 인증 정보가 설정된 DTO
     * @return 인증 결과 (유효하면 1 이상)
     */
    public int selectVerifyPublicAccess(ScheduleDTO dto) {
        log.info("ScheduleDAO selectVerifyPublicAccess branch={}", dto.getBranch());
        return sqlSession.selectOne(ns + "selectVerifyPublicAccess", dto);
    }
}
