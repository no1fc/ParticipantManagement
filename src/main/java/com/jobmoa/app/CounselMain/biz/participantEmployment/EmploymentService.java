package com.jobmoa.app.CounselMain.biz.participantEmployment;

import java.util.List;

/**
 * 참여자 취업 이력 관리 서비스.
 * 취업 정보의 조회, 등록, 수정, 삭제 기능을 제공한다.
 */
public interface EmploymentService {

    /**
     * 취업 정보 등록.
     *
     * @param employmentDTO 등록할 취업 정보
     * @return 등록 성공 여부
     */
    boolean insert(EmploymentDTO employmentDTO);

    /**
     * 취업 정보 수정.
     *
     * @param employmentDTO 수정할 취업 정보
     * @return 수정 성공 여부
     */
    boolean update(EmploymentDTO employmentDTO);

    /**
     * 취업 정보 삭제.
     *
     * @param employmentDTO 삭제 대상 취업 정보
     * @return 삭제 성공 여부
     */
    boolean delete(EmploymentDTO employmentDTO);

    /**
     * 취업 정보 단건 조회.
     *
     * @param employmentDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 취업 정보, 없으면 {@code null}
     */
    EmploymentDTO selectOne(EmploymentDTO employmentDTO);

    /**
     * 취업 정보 목록 조회.
     *
     * @param employmentDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 취업 정보 목록
     */
    List<EmploymentDTO> selectAll(EmploymentDTO employmentDTO);
}
