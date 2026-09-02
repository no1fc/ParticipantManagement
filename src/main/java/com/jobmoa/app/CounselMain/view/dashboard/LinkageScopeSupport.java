package com.jobmoa.app.CounselMain.view.dashboard;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;

import java.util.Set;

/**
 * 연계 실적 대시보드의 상담사별(참여자별) 상세 표 권한 스코프 유틸리티.
 * <p>
 * 상담사별 상세 표는 관리자에게 전 지점 상담사 실적을 노출하므로, 이사 미만의 지점 관리직
 * (파트장·팀장·총괄·차장)에 한해 소속 지점으로 열람을 제한한다. 이사 이상(이사·본부장·상무·전무·대표)
 * 및 관리자(IS_MANAGER)는 전체 조회를 유지하고, 일반(상담·PRA)은 기존대로 본인 실적만 조회한다.
 * </p>
 * <p>
 * KPI·지점 차트·지점별 표에는 적용하지 않으며(상담사별 상세 표 전용), 스코프를 축소하는 방향으로만
 * 동작한다. {@link LinkageDashboardController}와 {@link LinkageDashboardExcelController}가 공용으로 사용한다.
 * </p>
 */
public final class LinkageScopeSupport {

    /** 상담사별 상세를 소속 지점으로 한정하는 직급(이사 미만 관리직). */
    private static final Set<String> BRANCH_SCOPED_ROLES =
            Set.of("파트장", "팀장", "총괄", "차장");

    private LinkageScopeSupport() {
    }

    /**
     * 상담사별 상세 표를 지점 한정할 직급이면 소속 지점명을, 아니면 {@code null}을 반환한다.
     *
     * @param loginBean 로그인 사용자 정보(직급·소속 지점)
     * @return 지점 한정 대상이면 소속 지점명, 그 외에는 {@code null}
     */
    public static String resolveCounselorBranchScope(LoginBean loginBean) {
        if (loginBean == null) {
            return null;
        }
        String role = loginBean.getMemberRole();
        if (role != null && BRANCH_SCOPED_ROLES.contains(role)) {
            return loginBean.getMemberBranch();
        }
        return null;
    }
}
