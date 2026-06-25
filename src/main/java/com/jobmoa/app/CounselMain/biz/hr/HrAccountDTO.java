package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 계정 관리 DTO.
 * <p>대상 테이블: {@code J_직원_계정} (SSO 자격증명·계정상태), 이름은 {@code J_직원} 조인.</p>
 */
@Data
public class HrAccountDTO {

    /** 계정PK (PK, IDENTITY) */
    private Integer accountPk;
    /** 아이디 (UQ, FK→J_직원.아이디) */
    private String userId;
    /** 이름 (J_직원 조인) */
    private String userName;
    /** 비밀번호 (BCrypt, 초기화 시에만 사용) */
    private String password;
    /** 계정상태 (사용/정지/잠금/퇴사/승인대기) */
    private String accountStatus;
    /** 비밀번호변경일 */
    private String pwChangeDate;
    /** 로그인실패횟수 */
    private Integer loginFailCount;
    /** 마지막로그인일시 */
    private String lastLoginAt;
    /** 비밀번호 설정 여부 (1=설정됨, 0=미설정) */
    private Integer hasPassword;
    /** 지점 (로그인 시에만 채움 — J_참여자관리_로그인정보.지점, hrGnb 표시용) */
    private String branch;

    // ===== 검색 조건 =====
    private String searchUserId;
    private String searchUserName;
    private String searchAccountStatus;
}
