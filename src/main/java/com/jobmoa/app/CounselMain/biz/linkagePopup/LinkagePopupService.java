package com.jobmoa.app.CounselMain.biz.linkagePopup;

/**
 * 국취 연계실적 독려 팝업 서비스.
 */
public interface LinkagePopupService {

    /**
     * 상담사 대시보드 팝업에 표시할 연계실적 요약을 산정한다.
     *
     * @param account       전담자 계정(아이디)
     * @param counselorName 상담사명 (문구 치환용)
     * @param branch        소속 지점명 (목표 분모 산정용)
     * @return 팝업 결과 DTO (노출 불가 시 show=false)
     */
    LinkagePopupResultDTO selectPopupSummary(String account, String counselorName, String branch);
}
