package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) 로그인 세션 빈. 잡모아 메인 {@code LoginBean}과 분리된 HR 전용 세션 데이터다.
 * <p>세션 속성 키 {@code HR_LOGIN_DATA}로 적재되며, hrGnb 등 화면에서 담당자/권한/지점 표시에 사용한다.</p>
 *
 * @see com.jobmoa.app.CounselMain.view.hr.HrAccessSupport
 */
@Data
public class HrLoginBean {

    /** 아이디 (J_직원_계정.아이디) */
    private String userId;
    /** 이름 (J_직원.이름) */
    private String userName;
    /** HR 사이트 역할 (HR_MANAGER/HR_STAFF/HR_VIEW, J_직원_사이트접속.사이트내권한) */
    private String role;
    /** 지점 (표시용, J_참여자관리_로그인정보.지점) */
    private String branch;
}
