package com.jobmoa.app.CounselMain.biz.dashboard;

import java.util.List;

/**
 * 배정 관리 서비스 인터페이스.
 * 참여자 배정 정보의 조회, 등록, 수정, 삭제를 정의한다.
 */
public interface ArrangementService {

    /**
     * 배정 목록을 조회한다.
     * @param arrangementDTO 검색 조건이 담긴 DTO
     * @return 배정 목록
     */
    List<ArrangementDTO> selectAll(ArrangementDTO arrangementDTO);

    /**
     * 배정 단건을 조회한다.
     * @param arrangementDTO 조회 조건이 담긴 DTO
     * @return 배정 상세 정보
     */
    ArrangementDTO selectOne(ArrangementDTO arrangementDTO);

    /**
     * 배정 정보를 등록한다.
     * @param arrangementDTO 등록할 배정 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean insert(ArrangementDTO arrangementDTO);

    /**
     * 배정 정보를 수정한다.
     * @param arrangementDTO 수정할 배정 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean update(ArrangementDTO arrangementDTO);

    /**
     * 배정 정보를 삭제한다.
     * @param arrangementDTO 삭제 대상 배정 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean delete(ArrangementDTO arrangementDTO);
}
