package com.jobmoa.app.CounselMain.view.function;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class MemberRoleCheck {

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

    public static PermissionGroup getPermissionGroup(String role) {
        return Arrays.stream(ROLES.values())
                .filter(r -> r.getKoreanName().equals(role))
                .map(ROLES::getGroup)
                .findFirst()
                .orElse(PermissionGroup.NORMAL);
    }
}
