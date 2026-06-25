package com.jobmoa.app.CounselMain.biz.hr;

import java.util.List;

/**
 * HR - 계정 관리 서비스 인터페이스.
 * <p>계정은 직원당 1:1(직원 등록 시 생성)이라 임의 INSERT/DELETE는 제공하지 않고
 * 상태변경·비번초기화·잠금해제만 노출한다.</p>
 */
public interface HrAccountService {

    List<HrAccountDTO> getAccountList(HrAccountDTO dto);

    HrAccountDTO getAccountOne(HrAccountDTO dto);

    /** 계정상태 변경 (사용/정지/잠금/퇴사/승인대기). */
    boolean changeStatus(HrAccountDTO dto);

    /** 비밀번호 초기화 — 임시비밀번호를 BCrypt로 인코딩 후 저장. */
    boolean resetPassword(HrAccountDTO dto);

    /** 잠금 해제 — 로그인실패횟수=0, 계정상태='사용'. */
    boolean unlock(HrAccountDTO dto);
}
