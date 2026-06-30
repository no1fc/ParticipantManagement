package com.jobmoa.app.CounselMain.view.dashboard;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardServiceImpl;
import com.jobmoa.app.CounselMain.biz.dashboard.PeriodExpiryNoticeDTO;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 기간만료 도래·경과자 GNB 알림 REST 컨트롤러.
 * <p>상담사 페이지 로드 시 비동기 호출되어 알림 벨 standing 항목 데이터를 JSON 으로 반환한다.
 * 기존 {@code selectDailyDashboard} 집계를 재사용하므로 별도 매퍼/알림 저장소가 필요 없으며,
 * 미처리(미마감) 참여자는 페이지 로드마다 재계산되어 지속 노출된다.</p>
 */
@Slf4j
@RestController
public class PeriodExpiryNotificationApiController {

    @Autowired
    private DashboardServiceImpl dashboardService;

    /**
     * 로그인 상담사의 기간만료 도래·경과자 요약을 조회한다.
     *
     * @param session HTTP 세션 (로그인 정보)
     * @return 도래자 합계/당일/경과 카운트 (비로그인 시 count=0)
     */
    @GetMapping("/notification/period-expiry/summary")
    public PeriodExpiryNoticeDTO summary(HttpSession session) {
        PeriodExpiryNoticeDTO result = new PeriodExpiryNoticeDTO();

        LoginBean login = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        if (login == null) {
            return result; // count=today=passed=0
        }

        DashboardDTO param = new DashboardDTO();
        param.setDashboardUserID(login.getMemberUserID());
        param.setDashboardBranch(login.getMemberBranch());
        param.setDashboardCondition("selectDailyDashboard");

        DashboardDTO daily = dashboardService.selectOne(param);
        if (daily != null) {
            int today = daily.getDashBoardEXPDateToday();
            int passed = daily.getDashBoardEXPDatePassed();
            result.setToday(today);
            result.setPassed(passed);
            result.setCount(today + passed);
        }
        return result;
    }
}
