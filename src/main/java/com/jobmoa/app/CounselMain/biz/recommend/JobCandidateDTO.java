package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

@Data
public class JobCandidateDTO {

    private String certNo;        // 구인인증번호 (wanted_auth_no)
    private String companyName;   // 기업명 (company_nm)
    private String recruitTitle;  // 채용제목 (recrut_title)
    private String industryType;  // 업종 (ind_tp_nm)
    private String jobPostingUrl; // 공고URL (wanted_info_url)

    private String salaryDesc;    // 급여 상세 텍스트 (sal_desc)
    private String minEducation;  // 최소학력 (min_edubg)
    private String maxEducation;  // 최대학력 (max_edubg)
    private String career;        // 경력 (career)
    private String businessSize;  // 회사규모 (busi_size)
    private String relatedJobs;   // 관련직종 (rel_jobs_nm)
    private String jobContent;    // 직무내용 (job_cont)
    private String major;         // 전공 (major)
    private String certificate;   // 자격면허 (certificate)
}
