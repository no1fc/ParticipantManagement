package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link HrAuthService} 구현체. 잡모아 메인 {@code LoginController} 인증 패턴을 미러링하되 HR 전용으로 분리한다.
 * <p>비밀번호 검증은 메인과 동일한 이중 모드(BCrypt {@code $2a$} 또는 평문)이며, 기존 {@code BCryptPasswordEncoder} 빈을 재사용한다.</p>
 */
@Slf4j
@Service
public class HrAuthServiceImpl implements HrAuthService {

    /** 활성 계정 상태 (이 상태만 로그인 허용). */
    private static final String ACTIVE_STATUS = "사용";
    /** BCrypt 해시 접두사. */
    private static final String BCRYPT_PREFIX = "$2a$";
    /** 사이트내권한 NULL 시 데모 기본 역할(읽기 전용). 설계 §5 상속 규칙의 단순화. */
    private static final String DEFAULT_ROLE = "HR_VIEW";

    @Autowired
    private HrAuthDAO hrAuthDAO;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public HrAuthResultDTO login(String userId, String rawPassword) {
        if (userId == null || userId.isBlank() || rawPassword == null || rawPassword.isEmpty()) {
            return HrAuthResultDTO.fail("아이디 또는 비밀번호를 확인해주세요.");
        }

        // 1) 계정 조회 + 상태 검증
        HrAccountDTO account = hrAuthDAO.selectAccountForLogin(userId);
        if (account == null) {
            return HrAuthResultDTO.fail("아이디 또는 비밀번호를 확인해주세요.");
        }
        if (!ACTIVE_STATUS.equals(account.getAccountStatus())) {
            return HrAuthResultDTO.fail("계정이 비활성화 상태입니다.");
        }
        String stored = account.getPassword();
        if (stored == null || stored.isBlank()) {
            return HrAuthResultDTO.fail("비밀번호가 설정되지 않은 계정입니다. 관리자에게 문의하세요.");
        }

        // 2) 비밀번호 검증 (이중 모드: BCrypt 또는 평문) — 메인 로그인과 동일
        boolean passwordMatch = stored.startsWith(BCRYPT_PREFIX)
                ? passwordEncoder.matches(rawPassword, stored)
                : rawPassword.equals(stored);
        if (!passwordMatch) {
            return HrAuthResultDTO.fail("아이디 또는 비밀번호를 확인해주세요.");
        }

        // 3) HR 사이트 접근 게이트 (J_직원_사이트접속, 사이트코드='HR', 접속허용=1)
        if (hrAuthDAO.selectHrSiteAccessCount(userId) <= 0) {
            return HrAuthResultDTO.fail("입퇴사자관리(HR) 접근 권한이 없습니다.");
        }

        // 4) 역할 결정 (사이트내권한, NULL이면 기본 역할로 폴백)
        String role = hrAuthDAO.selectHrSiteRole(userId);
        if (role == null || role.isBlank()) {
            role = DEFAULT_ROLE;
        }

        // 5) 메뉴 접근맵 적재 (시드 전이면 빈 맵)
        Map<String, String> menuAccess = loadMenuAccess(role);

        // 6) 마지막 로그인 일시 기록
        hrAuthDAO.updateLastLogin(userId);

        // 7) 세션 빈 구성
        HrLoginBean loginBean = new HrLoginBean();
        loginBean.setUserId(account.getUserId());
        loginBean.setUserName(account.getUserName());
        loginBean.setRole(role);
        loginBean.setBranch(account.getBranch());

        log.info("HR 로그인 성공 id={} role={}", userId, role);
        return HrAuthResultDTO.ok(loginBean, menuAccess);
    }

    /** 역할별 메뉴 접근레벨 맵을 구성한다(메뉴코드 → 접근레벨). */
    private Map<String, String> loadMenuAccess(String role) {
        Map<String, String> menuAccess = new LinkedHashMap<>();
        List<Map<String, Object>> rows = hrAuthDAO.selectHrMenuAccess(role);
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Object code = row.get("menuCode");
                Object level = row.get("accessLevel");
                if (code != null && level != null) {
                    menuAccess.put(code.toString(), level.toString());
                }
            }
        }
        return menuAccess;
    }
}
