package com.jobmoa.app.CounselMain.view.linkagePopup;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.linkagePopup.LinkagePopupResultDTO;
import com.jobmoa.app.CounselMain.biz.linkagePopup.LinkagePopupService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 국취 연계실적 독려 팝업 REST 컨트롤러.
 * <p>상담사 대시보드 진입 시 비동기로 호출되어 팝업 데이터를 JSON 으로 반환한다.
 * 로그인 인터셉터를 통과하므로 세션의 로그인 정보를 신뢰한다.</p>
 */
@Slf4j
@RestController
public class LinkagePopupApiController {

    @Autowired
    private LinkagePopupService linkagePopupService;

    /**
     * 로그인 상담사의 연계실적 팝업 요약을 조회한다.
     *
     * @param session HTTP 세션 (로그인 정보)
     * @return 팝업 결과 (비로그인/노출불가 시 show=false)
     */
    @GetMapping("/linkage-popup/summary")
    public LinkagePopupResultDTO summary(HttpSession session) {
        LoginBean login = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        if (login == null) {
            LinkagePopupResultDTO empty = new LinkagePopupResultDTO();
            empty.setShow(false);
            return empty;
        }
        return linkagePopupService.selectPopupSummary(
                login.getMemberUserID(), login.getMemberUserName(), login.getMemberBranch());
    }
}
