package com.jobmoa.app.jobPlacement.biz.jobPlacement;

import java.util.List;

/**
 * 알선(취업 연계) 관리 서비스.
 * 알선 정보의 조회, 등록, 수정, 삭제 및 자격증/희망직무 관리 기능을 제공한다.
 */
public interface JobPlacementService {

    /**
     * 알선 정보 단건 조회.
     * 외부 상세 조회 시 희망직무 목록을 함께 반환한다.
     *
     * @param jobPlacementDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 알선 정보, 없으면 {@code null}
     */
    JobPlacementDTO selectOne(JobPlacementDTO jobPlacementDTO);

    /**
     * 알선 정보 목록 조회.
     * 외부/스타벅스 전체 조회 시 희망직무 목록을 함께 반환한다.
     *
     * @param jobPlacementDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 알선 정보 목록
     */
    List<JobPlacementDTO> selectAll(JobPlacementDTO jobPlacementDTO);

    /**
     * 알선 정보 등록.
     *
     * @param jobPlacementDTO 등록할 알선 정보
     * @return 등록 성공 여부
     */
    boolean insert(JobPlacementDTO jobPlacementDTO);

    /**
     * 알선 정보 수정.
     * 자격증 삭제/재등록, 알선 상세정보 갱신, 희망직무 갱신을 트랜잭션으로 처리한다.
     *
     * @param jobPlacementDTO 수정할 알선 정보
     * @return 수정 성공 여부
     */
    boolean update(JobPlacementDTO jobPlacementDTO);

    /**
     * 알선 정보 삭제.
     *
     * @param jobPlacementDTO 삭제 대상 알선 정보
     * @return 삭제 성공 여부
     */
    boolean delete(JobPlacementDTO jobPlacementDTO);
}