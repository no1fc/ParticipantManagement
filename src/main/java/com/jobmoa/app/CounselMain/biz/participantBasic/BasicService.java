package com.jobmoa.app.CounselMain.biz.participantBasic;

import java.util.List;

/**
 * 참여자 기본정보 관리 서비스 인터페이스.
 * 기본정보의 조회, 등록, 수정, 삭제를 정의한다.
 */
public interface BasicService {

    /**
     * 참여자 기본정보를 등록한다.
     * @param basicDTO 등록할 기본정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean insert(BasicDTO basicDTO);

    /**
     * 참여자 기본정보를 수정한다.
     * @param basicDTO 수정할 기본정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean update(BasicDTO basicDTO);

    /**
     * 참여자 기본정보를 삭제한다.
     * @param basicDTO 삭제 대상 기본정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean delete(BasicDTO basicDTO);

    /**
     * 참여자 기본정보를 단건 조회한다.
     * @param basicDTO 조회 조건이 담긴 DTO
     * @return 기본정보 상세 데이터
     */
    BasicDTO selectOne(BasicDTO basicDTO);

    /**
     * 참여자 기본정보 목록을 조회한다.
     * @param basicDTO 검색 조건이 담긴 DTO
     * @return 기본정보 목록
     */
    List<BasicDTO> selectAll(BasicDTO basicDTO);
}
