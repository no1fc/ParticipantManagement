package com.jobmoa.app.CounselMain.biz.particcertif;

import java.util.List;

/**
 * 참여자 자격증 관리 서비스 인터페이스.
 * 참여자의 자격증 정보 조회, 등록, 수정, 삭제를 정의한다.
 */
public interface ParticcertifService {

    /**
     * 참여자 자격증 정보를 단건 조회한다.
     * @param particcertifDTO 조회 조건이 담긴 DTO
     * @return 자격증 상세 정보
     */
    ParticcertifDTO selectOne(ParticcertifDTO particcertifDTO);

    /**
     * 참여자 자격증 목록을 조회한다.
     * @param particcertifDTO 검색 조건이 담긴 DTO
     * @return 자격증 목록
     */
    List<ParticcertifDTO> selectAll(ParticcertifDTO particcertifDTO);

    /**
     * 참여자 자격증 정보를 등록한다 (기존 데이터 삭제 후 재등록 방식).
     * @param particcertifDTO 등록할 자격증 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean insert(ParticcertifDTO particcertifDTO);

    /**
     * 참여자 자격증 정보를 수정한다.
     * @param particcertifDTO 수정할 자격증 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean update(ParticcertifDTO particcertifDTO);

    /**
     * 참여자 자격증 정보를 삭제한다.
     * @param particcertifDTO 삭제 대상 자격증 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean delete(ParticcertifDTO particcertifDTO);
}
