package com.jobmoa.app.recruitmentFormation.view.jobinfo;

import com.jobmoa.app.recruitmentFormation.biz.RecruitmentService;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentResultDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * 채용공고 검색 REST API 컨트롤러
 * 인증 불필요 (공개 API) — WebMvcConfig LoginInterceptor 제외 경로 등록됨
 */
@Slf4j
@RestController
@RequestMapping("/recruitmentInformation")
public class jobinfoApiController {

    @Autowired
    private RecruitmentService recruitmentService;

    /**
     * 채용공고 검색 결과 반환
     * GET /recruitmentInformation/search
     *
     * @param searchDTO 검색 파라미터 (region[], occupation[], education[], career[], ...)
     * @return JSON { total, startPage, display, wantedInfo:[...] }
     */
    @GetMapping("/search")
    public ResponseEntity<RecruitmentResultDTO> search(RecruitmentSearchDTO searchDTO) {
        log.info("채용공고 검색 요청 - page:{}, display:{}", searchDTO.getStartPage(), searchDTO.getDisplay());
        log.info("채용공고 검색 파라미터: {}", searchDTO);
        try {
            RecruitmentResultDTO result = recruitmentService.search(searchDTO);
            log.info("채용공고 검색 완료 - 총 {}건 (현재 페이지 {}건)",
                    result.getTotal(), result.getWantedInfo() != null ? result.getWantedInfo().size() : 0);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("채용공고 검색 실패 (테이블 미존재 또는 DB 오류): {}", e.getMessage());
            RecruitmentResultDTO empty = new RecruitmentResultDTO();
            empty.setWantedInfo(new ArrayList<>());
            return ResponseEntity.ok(empty);
        }
    }
}