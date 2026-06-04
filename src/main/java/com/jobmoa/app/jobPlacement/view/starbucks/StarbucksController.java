package com.jobmoa.app.jobPlacement.view.starbucks;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.bean.PaginationBean;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementDTO;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

/**
 * 스타벅스(외부 공개) 참여자 목록 페이지 컨트롤러.
 * 외부에서 접근 가능한 참여자 목록을 페이지네이션과 함께 제공하며,
 * 담당 상담사가 아닌 참여자의 개인정보는 마스킹 처리한다.
 */
@Slf4j
@Controller
public class StarbucksController {

    @Autowired
    private JobPlacementService jobPlacementService;

    /**
     * 스타벅스 참여자 목록 페이지를 표시한다.
     * 페이지네이션을 적용하여 참여자 목록을 조회하고,
     * 담당 상담사가 아닌 참여자의 이름/주소/나이를 마스킹 처리한다.
     *
     * @param model           뷰에 전달할 모델 객체
     * @param session         HTTP 세션 (로그인 정보 조회용)
     * @param jobPlacementDTO 검색/페이지 조건 DTO
     * @param paginationBean  페이지네이션 설정 Bean
     * @return JSP 뷰 이름 ("starbucksView/participant-list")
     */
    @GetMapping("/Starbucks")
    public String starbucksPage(Model model, HttpSession session, JobPlacementDTO jobPlacementDTO, PaginationBean paginationBean) {
        log.info("starbucksPage Start");

        int page = jobPlacementDTO.getPage() <= 0 ? 1 : jobPlacementDTO.getPage();
        int pageRows = jobPlacementDTO.getPageRows() <= 0 ? 10 : jobPlacementDTO.getPageRows();
        int limitButton = 10;

        jobPlacementDTO.setCondition("selectStarbucksCount");
        JobPlacementDTO totalCountDTO = jobPlacementService.selectOne(jobPlacementDTO);

        if (totalCountDTO != null) {
            int totalCount = totalCountDTO.getTotalCount();
            log.info("starbucksPage totalCount : [{}]", totalCount);

            paginationBean.paginationProject(page, pageRows, limitButton, totalCount);

            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            String sessionUniqueNumber = "";
            if (loginBean != null) {
                sessionUniqueNumber = loginBean.getMemberUniqueNumber();
            }

            jobPlacementDTO.setStartPage(paginationBean.getStartPage());
            jobPlacementDTO.setEndPage(paginationBean.getEndPage());
            jobPlacementDTO.setPageRows(pageRows);
            jobPlacementDTO.setCondition("selectStarbucksAll");

            List<JobPlacementDTO> starbucksDatas = jobPlacementService.selectAll(jobPlacementDTO);

            for (JobPlacementDTO p : starbucksDatas) {
                String originalName = p.getParticipant();
                String originalAddress = p.getAddress();
                int originalAge = p.getAge();
                boolean isMine = Objects.equals(p.getUniqueNumber(), sessionUniqueNumber);
                if (!isMine) {
                    hideInfo(p, originalName, originalAddress, originalAge);
                }
            }

            model.addAttribute("starbucksDatas", starbucksDatas);
        }

        model.addAttribute("page", page);
        model.addAttribute("startButton", paginationBean.getStartButton());
        model.addAttribute("endButton", paginationBean.getEndButton());
        model.addAttribute("totalButton", paginationBean.getTotalButton());

        log.info("starbucksPage End");
        return "starbucksView/participant-list";
    }

    /**
     * 참여자의 개인정보(이름, 주소, 나이)를 마스킹 처리한다.
     * 이름은 첫 글자만 노출, 주소는 11자까지만 노출, 나이는 연령대 문자열로 변환한다.
     *
     * @param data            마스킹 대상 DTO
     * @param originalName    원본 이름
     * @param originalAddress 원본 주소
     * @param age             원본 나이
     */
    private void hideInfo(JobPlacementDTO data, String originalName, String originalAddress, int age) {
        if (originalName != null && !originalName.isEmpty()) {
            String maskedName = originalName.charAt(0) + originalName.substring(1).replaceAll(".", "O");
            data.setParticipant(maskedName);
        }

        if (originalAddress != null && !originalAddress.isEmpty()) {
            String makeAddress = originalAddress.length() > 11 ? originalAddress.substring(0, 11) : originalAddress;
            data.setAddress(makeAddress + "...");
        }

        if (age != 0) {
            if (age >= 1 && age <= 39) {
                data.setAgeRangeContent("청년");
            } else if (age >= 40 && age <= 59) {
                data.setAgeRangeContent("중년");
            } else if (age >= 60 && age <= 79) {
                data.setAgeRangeContent("장년");
            } else {
                data.setAgeRangeContent("비공개");
            }
        }
    }
}