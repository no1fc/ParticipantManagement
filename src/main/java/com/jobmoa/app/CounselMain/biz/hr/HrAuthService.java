package com.jobmoa.app.CounselMain.biz.hr;

/**
 * HR(입퇴사자관리) 로그인 인증 서비스. 잡모아 메인 로그인과 분리된 HR 전용 인증을 수행한다.
 * <p>인증 흐름: 계정·상태 검증 → 비밀번호 검증(BCrypt 이중 모드) → HR 사이트 접근 게이트 → 역할 결정 → 메뉴접근맵 적재.</p>
 *
 * @see HrAuthServiceImpl
 */
public interface HrAuthService {

    /**
     * 아이디·비밀번호로 HR 로그인을 시도한다.
     *
     * @param userId 아이디
     * @param rawPassword 입력 비밀번호(평문)
     * @return 인증 결과(성공 시 세션 빈·메뉴맵 포함, 실패 시 사유 메시지)
     */
    HrAuthResultDTO login(String userId, String rawPassword);
}
