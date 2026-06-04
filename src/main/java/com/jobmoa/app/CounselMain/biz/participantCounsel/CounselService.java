package com.jobmoa.app.CounselMain.biz.participantCounsel;

import java.util.List;

/**
 * 참여자 상담정보 관리 서비스 인터페이스.
 * 상담 정보의 조회, 등록, 수정, 삭제를 정의한다.
 */
public interface CounselService {

    /**
     * 상담 정보를 단건 조회한다.
     * @param counselDTO 조회 조건이 담긴 DTO
     * @return 상담 상세 정보
     */
    CounselDTO selectOne(CounselDTO counselDTO);

    /**
     * 상담 정보 목록을 조회한다.
     * @param counselDTO 검색 조건이 담긴 DTO
     * @return 상담 정보 목록
     */
    List<CounselDTO> selectAll(CounselDTO counselDTO);

    /**
     * 상담 정보를 등록한다.
     * @param counselDTO 등록할 상담 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean insert(CounselDTO counselDTO);

    /**
     * 상담 정보를 수정한다.
     * @param counselDTO 수정할 상담 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean update(CounselDTO counselDTO);

    /**
     * 상담 정보를 삭제한다.
     * @param counselDTO 삭제 대상 상담 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean delete(CounselDTO counselDTO);
}
