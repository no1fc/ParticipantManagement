package com.jobmoa.app.CounselMain.biz.participant;

import lombok.Data;

import java.util.List;

@Data
public class ParticipantDTO {
    
    //Basic DB 컬럼
    private int participantJobNo; // PK(구직번호)
    private String participantRegDate; // 등록일
    private String participantBranch; // 지점
    private String participantUserid; // 전담자_계정
    private String participantPartic; // 참여자
    private String participantDob; // 생년월일
    private String participantGender; // 성별
    private String participantRecruit; // 모집경로
    private String participantPartType; // 참여유형
    private String participantSchool; // 학교명
    private String participantSpecialty; // 전공
    private String participantAddress; // 주소(시,구)
    private String participantAntecedents; // 경력
    private String participantSpecific; // 특정계층
    private String participantPlacement; // 알선요청
    private String participantEducation; // 학력
    private boolean participantClose; // 마감여부

    //Counsel DB 컬럼
    private String participantJobSkill; // 취업역량
    private String participantLastCons; // 최근상담일
    private String participantProgress; // 진행단계
    private String participantInItCons; // 초기상담일
    private String participantJobEX; // 구직만료일
    private String participantIAPDate; // IAP수료일
    private String participantStepPro; // 3단계진입일
    private String participantEXPDate; // 기간만료일
    private String participantClinic; // 클리닉실시일
    private String participantJobWant; // 희망직무
    private int participantSalWant; // 희망급여
    private String participantEmploymentService; // 간접고용서비스
    private String participantIAP3Month; // IAP수료일 3개월차
    private String participantIAP5Month; // IAP수료일 5개월차
    private boolean participantISIAP3Month; // IAP수료일 3개월 이후 여부
    private boolean participantISIAP5Month; // IAP수료일 5개월 이후 여부
    private String participantAllowanceDate; // 수당지급일
    private boolean participantISIntensiveMediation; // 집중알선여부
    
    //Counsel DB 외 컬럼
    private String participantAdventCons; // 상담도래자

    //Employment DB 컬럼
    private String participantStartDate; // 취창업일
    private String participantProcDate; // 취창업처리일
    private String participantEmpType; // 취업유형
    private String participantLoyer; // 취업처
    private int participantSalary; // 임금
    private String participantJobRole; // 직무
    private String participantIncentive; // 취업인센티브_구분
    private String participantJobcat; // 일경험분류
    private String participantMemo; // 메모
    private String participantOthers; // 기타정보
    private String participantQuit; // 기타정보

    //참여자 변경 정보
    private String participantManagerChangeDate; // 전담자_변경일
    private String participantInitialManagerAccount; // 초기전담자_계정
    private String participantModifyDate; // 참여자 수정일
    
    private String certificationName; //자격증 명칭
    private String participantUserName; // 전담자 이름
    private String participantEmail; // 이메일
    private String participantPhoneNumber; // 전화 번호
    private String participantCode; // 고유 번호

    //알선상세정보 조회 DB 정보
//    private String placementJobNo; //알선상세정보 구직번호 (나중에 추가사용을 할 수 있으니 주석)
    private String placementDetail; //알선상세정보 글 데이터

    private List<String> recommendedKeywords; // 알선 추천 키워드
    private String suggestionDetail; // 추천사 글 데이터
    private String jobCategoryLarge; // 키워드 대분류
    private String jobCategoryMid; // 키워드 중분류
    private String jobCategorySub; // 키워드 소분류

    //DB 외 정보
    private String participantCondition; // 개발자 구분

    private int rowNum; // 연번 //구직번호와 별개의 번호(순서 번호)

    //권한 관리
    private boolean participantBranchManagement;
    private boolean participantManagement;

    //페이지네이션 정보
    private int page;
    private int pageRows;
    private int totalCount;
    private int startPage;
    private int endPage;

    //검색 변수
    private String endDateOption;
    private String searchOption;
    private String search;
    private String searchType;
    private String searchFilter;
    private boolean searchStatus; // 검색 인지 아닌지 여부
    private String searchPath; // 검색한 페이지

    //정렬 변수
    private String column;
    private String order;

    //참여자 개수
    private int participantCount;

    //참여자이관을 위한 변수
    private String sourceCounselorID;
    private String targetCounselorID;
    private List<Integer> participantIDs;

    // 검색 시작 종료일
    private String searchStartDate;
    private String searchEndDate;

    // 이력서 요청 관련 필드들
    private String companyName; // 기업명
    private String contactName; // 담당자명
    private String contactEmail; // 이메일
    private String contactPhone; // 비상연락처
    private String contactOther; // 기타요청사항
    private String status; // 상태 (요청, 승인, 거부, 완료)
    private String registrationDate; // 등록일
    private String modificationDate; // 수정일
    private boolean companyPrivacy; // 기업개인정보처리동의
    private boolean contactPrivacy; // 기업담당자개인정보처리동의
    private boolean marketingConsent; // 마케팅개인정보사용동의

}
