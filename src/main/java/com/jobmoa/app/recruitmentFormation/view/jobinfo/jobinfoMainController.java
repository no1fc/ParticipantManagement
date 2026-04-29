package com.jobmoa.app.recruitmentFormation.view.jobinfo;

import com.google.gson.Gson;
import com.jobmoa.app.recruitmentFormation.biz.RecruitmentService;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentResultDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 채용공고 검색 페이지 컨트롤러
 * GET /jobinfo → recruitmentInformation/index.jsp 렌더링
 * 초기 진입 시 page=1, display=10 으로 최신 공고를 서버 사이드에서 pre-fetch하여
 * JSP에 JSON으로 전달 → AJAX 왕복 없이 첫 화면 즉시 표출
 */
@Slf4j
@Controller
@RequestMapping("/jobinfo")
public class jobinfoMainController {

    @Autowired
    private RecruitmentService recruitmentService;

    private static final Gson GSON = new Gson();

    @GetMapping
    public String jobinfo(Model model) {
        log.info("jobinfo 페이지 접속 — 초기 공고 로딩 (page=1, display=10)");

        // 기본 검색 파라미터 (page=1, display=10, sortOrderBy=DESC)
        RecruitmentSearchDTO defaultSearch = new RecruitmentSearchDTO();

        RecruitmentResultDTO initialResult;
        try {
            initialResult = recruitmentService.search(defaultSearch);
            log.info("초기 공고 로딩 완료: total={}, 건수={}", initialResult.getTotal(),
                    initialResult.getWantedInfo() != null ? initialResult.getWantedInfo().size() : 0);
        } catch (Exception e) {
            log.error("초기 공고 로딩 실패: {}", e.getMessage());
            initialResult = new RecruitmentResultDTO();
            initialResult.setWantedInfo(new java.util.ArrayList<>());
        }

        // JSP에서 <script id="initialJobData"> 안에 삽입되는 JSON
        model.addAttribute("initialResultJson", GSON.toJson(initialResult));

        return "recruitmentInformation/index";
    }
}