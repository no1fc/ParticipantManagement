package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

import java.util.Map;

/**
 * HR 로그인 인증 결과. 성공 시 세션 적재용 {@link HrLoginBean}과 메뉴접근맵을 담고,
 * 실패 시 사용자에게 보여줄 한국어 메시지를 담는다.
 *
 * @see HrAuthService#login(String, String)
 */
@Data
public class HrAuthResultDTO {

    /** 인증 성공 여부 */
    private boolean success;
    /** 사용자 표시 메시지 (실패 사유 등) */
    private String message;
    /** 성공 시 세션 적재용 로그인 빈 */
    private HrLoginBean loginBean;
    /** 성공 시 메뉴코드 → 접근레벨 맵 (시드 전이면 빈 맵) */
    private Map<String, String> menuAccess;

    /** 실패 결과를 생성한다. */
    public static HrAuthResultDTO fail(String message) {
        HrAuthResultDTO result = new HrAuthResultDTO();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    /** 성공 결과를 생성한다. */
    public static HrAuthResultDTO ok(HrLoginBean loginBean, Map<String, String> menuAccess) {
        HrAuthResultDTO result = new HrAuthResultDTO();
        result.setSuccess(true);
        result.setLoginBean(loginBean);
        result.setMenuAccess(menuAccess);
        return result;
    }
}
