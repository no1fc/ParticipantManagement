package com.jobmoa.app.recruitmentFormation.biz.dto;

import lombok.Data;

import java.util.List;

/**
 * 고용24 채용공고 검색 결과 DTO
 * /recruitmentInformation/search API 응답 JSON으로 직렬화됨
 * ※ 필드명은 index.jsp renderResults(resp) 함수의 기대값과 1:1 일치해야 함
 */
@Data
public class RecruitmentResultDTO {

    private int total;        // 전체 건수
    private int startPage;    // 현재 페이지
    private int display;      // 페이지당 건수

    private List<JobItem> wantedInfo;  // ← JSP: resp.wantedInfo

    /**
     * 개별 채용공고 항목
     * 필드명은 index.jsp buildJobCard(job) 에서 사용하는 키와 동일하게 유지
     */
    @Data
    public static class JobItem {
        private String wantedAuthNo;   // 구인인증번호 (PK)     ← DB: wanted_auth_no
        private String recrutTitle;    // 채용제목               ← DB: recrut_title
        private String compNm;         // 회사명                 ← DB: company_nm
        private String salTpNm;        // 임금형태명             ← DB: sal_tp_nm
        private String salNm;          // 급여 상세              ← DB: sal_desc
        private String empTpNm;        // 고용형태명             ← DB: emp_tp_nm
        private String regionNm;       // 근무지역               ← DB: region_nm
        private String occNm;          // 직종명                 ← DB: jobs_nm
        private String holidayTpNm;    // 근무형태               ← DB: holiday_tp_nm
        private String eduNm;          // 최소학력               ← DB: min_edubg (API 반환값 그대로)
        private String career;         // 경력                   ← DB: career
        // minCareerM / maxCareerM: DDL에 컬럼 없음, JSP에서 null 처리됨
        private String wantedAuthDt;   // 공고 등록일            ← DB: reg_dt
        private String closeDate;      // 공고 마감일            ← DB: close_dt (NULL=채용시)
        private String wantedInfoUrl;  // 워크넷 원본 URL        ← DB: wanted_info_url
    }
}