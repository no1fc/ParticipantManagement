package com.jobmoa.app.CounselMain.biz.hr;

import java.util.List;

/**
 * HR - 발령 관리 서비스 인터페이스.
 * <p>발령이력({@code J_직원_발령이력}) 타임라인 조회 및 직급변경·휴직·복직 발령 등록을 제공한다.</p>
 */
public interface HrTransferService {

    /** 직원 목록 (직급·주부서명·발령건수 포함). 기본 재직자 조회. */
    List<HrTransferDTO> getEmployeeList(HrTransferDTO dto);

    /** 특정 직원의 발령 타임라인 (발령PK 내림차순). */
    List<HrTransferDTO> getTransferList(HrTransferDTO dto);

    /** 직급 드롭다운 목록 (기존 직급 값). */
    List<HrTransferDTO> getPositionList();

    /** 특정 직원의 현재재직상태 (컨트롤러 상태 가드용). */
    String getCurrentStatus(HrTransferDTO dto);

    /** 직급변경(재직자): 재직 직급 갱신 + 발령이력 '직급변경'. */
    boolean changePosition(HrTransferDTO dto);

    /** 휴직(재직→휴직): 재직상태 전이 + 계정 '정지' + 발령이력 '휴직'. */
    boolean startLeave(HrTransferDTO dto);

    /** 복직(휴직→재직): 재직상태 전이 + 계정 '사용' + 발령이력 '복직'. */
    boolean returnFromLeave(HrTransferDTO dto);
}
