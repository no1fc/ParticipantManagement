package com.jobmoa.app.CounselMain.biz.report;

import java.util.List;

/**
 * 상담일지 및 보고서 관리 서비스.
 * 보고서 정보의 조회, 등록, 수정, 삭제 기능을 제공한다.
 */
public interface ReportService {

    /**
     * 보고서 단건 조회.
     *
     * @param reportDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 보고서 정보, 없으면 {@code null}
     */
    ReportDTO selectOne(ReportDTO reportDTO);

    /**
     * 보고서 목록 조회.
     *
     * @param reportDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 보고서 목록
     */
    List<ReportDTO> selectAll(ReportDTO reportDTO);

    /**
     * 보고서 등록.
     *
     * @param reportDTO 등록할 보고서 정보
     * @return 등록 성공 여부
     */
    boolean insert(ReportDTO reportDTO);

    /**
     * 보고서 수정.
     * 일일업무 저장 시 배정현황도 함께 갱신한다.
     *
     * @param reportDTO 수정할 보고서 정보
     * @return 수정 성공 여부
     */
    boolean update(ReportDTO reportDTO);

    /**
     * 보고서 삭제.
     *
     * @param reportDTO 삭제 대상 보고서 정보
     * @return 삭제 성공 여부
     */
    boolean delete(ReportDTO reportDTO);
}
