package com.jobmoa.app.CounselMain.biz.participantCounsel;

import lombok.Data;

/**
 * 참여자 타사 연계 데이터 전송 객체.
 * 한 참여자당 다중 연계(연계일/연계유형/연계비고)를 J_참여자관리_연계 테이블과 매핑하여 관리한다.
 */
@Data
public class LinkageDTO {
    private int pk;
    private int counselJobNo;
    private String linkDate; // 연계일
    private String linkType; // 연계유형
    private String linkNote; // 연계비고 (기타 유형 선택 시 상세 사유)
}
