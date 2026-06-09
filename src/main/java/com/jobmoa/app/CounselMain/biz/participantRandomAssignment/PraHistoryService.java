package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

/**
 * 참여자 랜덤 배정 이력 관리 서비스.
 * 배정 결과, 일일보고, 점수 설정, CSV 이력을 일괄 저장하는 기능을 제공한다.
 */
public interface PraHistoryService {

    /**
     * 랜덤 배정 관련 이력을 일괄 저장한다.
     * 배정 결과, 일일보고, 점수 설정, CSV 이력을 하나의 트랜잭션으로 처리한다.
     *
     * @param request  배정 이력 요청 데이터 (배정, 일일보고, 점수설정, CSV 이력 포함)
     * @param branch   지점명
     * @param writerId 작성자 ID
     * @return 전체 저장 성공 여부
     */
    boolean insertHistory(PraHistoryRequestDTO request, String branch, String writerId);
}
