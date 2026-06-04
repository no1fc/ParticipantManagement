package com.jobmoa.app.CounselMain.view.function;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 회원 역할(직급) 검증 유틸리티 클래스.
 * <p>직급별 권한 그룹(일반, 지점관리, 전체관리)을 정의하고,
 * 주어진 역할이 관리자 권한에 해당하는지 검증하는 기능을 제공한다.</p>
 */
@Slf4j
public class MemberRoleCheck {

    /**
     * 권한 그룹 열거형.
     * <p>일반(NORMAL), 지점관리(BRANCH_MANAGER), 전체관리(GLOBAL_ADMIN)로 구분된다.</p>
     */
    @Getter
    public enum PermissionGroup {
        NORMAL("일반"),
        BRANCH_MANAGER("지점관리"),
        GLOBAL_ADMIN("전체관리");

        private final String koreanName;

        PermissionGroup(String koreanName) {
            this.koreanName = koreanName;
        }
    }

    /**
     * 시스템에서 사용되는 직급 목록 열거형.
     * <p>각 직급은 한국어 이름과 소속 권한 그룹을 갖는다.</p>
     */
    @Getter
    public enum ROLES {
        COUNSELOR("상담", PermissionGroup.NORMAL),
        PRA("PRA", PermissionGroup.NORMAL),
        PART_LEADER("파트장", PermissionGroup.BRANCH_MANAGER),
        TEAM_LEADER("팀장", PermissionGroup.BRANCH_MANAGER),
        GENERAL("총괄", PermissionGroup.BRANCH_MANAGER),
        SENIOR_MANAGER("차장", PermissionGroup.BRANCH_MANAGER),
        EXECUTIVE_DIRECTOR("이사", PermissionGroup.BRANCH_MANAGER),
        MANAGING_DIRECTOR("상무이사", PermissionGroup.GLOBAL_ADMIN),
        SENIOR_DIRECTOR("전무", PermissionGroup.GLOBAL_ADMIN),
        CEO("대표", PermissionGroup.GLOBAL_ADMIN);

        private final String koreanName;
        private final PermissionGroup group;

        ROLES(String koreanName, PermissionGroup group) {
            this.koreanName = koreanName;
            this.group = group;
        }
    }

    /**
     * 주어진 역할(직급)이 지점 관리 이상의 권한인지 확인한다.
     * @param role 확인할 직급 한국어명
     * @return 지점관리 또는 전체관리 권한이면 true
     */
    public boolean checkBranchRole(String role) {
        log.debug("Checking role: {}", role);
        boolean isValid = Arrays.stream(ROLES.values())
                .filter(r -> r.getGroup() == PermissionGroup.BRANCH_MANAGER
                        || r.getGroup() == PermissionGroup.GLOBAL_ADMIN)
                .map(ROLES::getKoreanName)
                .anyMatch(r -> r.equals(role));
        log.debug("Role validation result: {}", isValid);
        return isValid;
    }

    /**
     * 주어진 역할(직급)에 해당하는 권한 그룹을 반환한다.
     * @param role 직급 한국어명
     * @return 해당 권한 그룹 (일치하는 직급이 없으면 NORMAL)
     */
    public static PermissionGroup getPermissionGroup(String role) {
        return Arrays.stream(ROLES.values())
                .filter(r -> r.getKoreanName().equals(role))
                .map(ROLES::getGroup)
                .findFirst()
                .orElse(PermissionGroup.NORMAL);
    }
}
