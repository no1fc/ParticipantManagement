package com.jobmoa.app.CounselMain.biz.participantEducation;

import java.util.List;

/**
 * 참여자 교육 이력 관리 서비스.
 * 교육 정보의 조회, 등록, 수정, 삭제 기능을 제공한다.
 */
public interface EducationService {
    /**
     * 교육 정보 단건 조회.
     *
     * @param educationDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 교육 정보, 없으면 {@code null}
     */
    EducationDTO selectOne(EducationDTO educationDTO);

    /**
     * 교육 정보 목록 조회.
     *
     * @param educationDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 교육 정보 목록
     */
    List<EducationDTO> selectAll(EducationDTO educationDTO);

    /**
     * 교육 정보 등록.
     * 기존 데이터를 삭제 후 새로 삽입하는 덮어쓰기 방식으로 동작한다.
     *
     * @param educationDTO 등록할 교육 정보
     * @return 등록 성공 여부
     */
    boolean insert(EducationDTO educationDTO);

    /**
     * 교육 정보 수정.
     *
     * @param educationDTO 수정할 교육 정보
     * @return 수정 성공 여부
     */
    boolean update(EducationDTO educationDTO);

    /**
     * 교육 정보 삭제.
     *
     * @param educationDTO 삭제 대상 교육 정보
     * @return 삭제 성공 여부
     */
    boolean delete(EducationDTO educationDTO);
}
