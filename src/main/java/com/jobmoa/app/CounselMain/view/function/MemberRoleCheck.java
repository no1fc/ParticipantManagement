package com.jobmoa.app.CounselMain.view.function;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class MemberRoleCheck {

    public enum ROLES {
        EXECUTIVE_DIRECTOR("이사"),
        SENIOR_MANAGER("차장"),
        GENERAL_MANAGER("본부장"),
        GENERAL("총괄"),
        TEAM_LEADER("팀장"),
        PART_LEADER("파트장");

        private final String koreanName;

        ROLES(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }
    public boolean checkBranchRole(String role){
        log.debug("Checking role: {}", role);
        boolean isValid = Arrays.stream(ROLES.values())
                .map(ROLES::getKoreanName)
                .anyMatch(r -> r.equals(role));
        log.debug("Role validation result: {}", isValid);
        return isValid;
    }

}
