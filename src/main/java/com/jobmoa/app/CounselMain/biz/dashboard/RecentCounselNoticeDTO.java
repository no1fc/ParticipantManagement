package com.jobmoa.app.CounselMain.biz.dashboard;

import lombok.Data;

/**
 * 최근상담일 경과자 GNB 알림 요약 응답 DTO.
 * <p>상담사 페이지 로드 시 비동기로 조회되어 알림 벨에 standing 항목으로 표시된다.
 * 데이터 기반 라이브 값이라 별도 알림 저장소 없이 미처리(미마감) 참여자가 지속 노출된다.</p>
 */
@Data
public class RecentCounselNoticeDTO {
    private int count; // 최근상담일 26일 이상 경과자
    private int month; // 최근상담일 한달(30일) 이상 경과자
}
