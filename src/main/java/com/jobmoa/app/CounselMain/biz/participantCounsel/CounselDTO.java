package com.jobmoa.app.CounselMain.biz.participantCounsel;

import lombok.Data;

import java.util.List;

@Data
public class CounselDTO {

    // DB 변수(컬럼)
    private int counselJobNo; // 구직번호
    private String counselPartic; // 참여자 성명
    private String counselBranch; // 지점
    private String counselUserid; // 전담자_계정
    private String counselJobSkill; // 취업역량
    private String counselLastCons; // 최근상담일
    private String counselProgress; // 진행단계
    private String counselSUCU; // 성공금충족여부
    private String counselInItCons; // 초기상담일
    private String counselJobEX; // 구직만료일
    private String counselIAPDate; // IAP수료일
    private String counselStepPro; // 3단계진입일
    private String counselEXPDate; // 기간만료(예정)일
    private String counselClinic; // 클리닉실시일
    // ※ 희망직무 / 직무 카테고리는 J_참여자관리_희망직무 테이블로 이전됨(다중 희망직무 지원).
    //    아래 필드들은 폼 검증 및 syncPrimaryJobWish() 동기화용으로만 사용됨 (DB 미저장).
    private String counselJobWant; // 희망직무 (1순위 동기화용, 미저장)
    private String counselSalWant; // 희망급여
    private String counselEducation; // 직업훈련
    private String counselPlacement; // 알선요청
    private String counselEmploymentService; // 간접고용서비스
    private String counselIAP3Month; // IAP수료일 3개월차
    private String counselIAP5Month; // IAP수료일 5개월차
    private boolean counselISIAP3Month; // IAP수료일 3개월 이후 여부
    private boolean counselISIAP5Month; // IAP수료일 5개월 이후 여부
    private String counselAllowancePayment; // 수당지급
    private boolean counselFocusedPlacement; // 집중알선요청 여부 (DB 집중알선여부 bit(1), true=희망)

    //알선상세정보 조회 DB 정보
//    private String placementJobNo; //알선상세정보 구직번호 (나중에 추가사용을 할 수 있으니 추가)
    private String placementDetail; //알선상세정보 글 데이터

    private List<String> recommendedKeywords; // 알선 추천 키워드
    private String suggestionDetail; // 추천사 글 데이터
    private String jobCategoryLarge; // 직무 카테고리 대분류 (1순위 동기화용, 미저장)
    private String jobCategoryMid;   // 직무 카테고리 중분류 (1순위 동기화용, 미저장)
    private String jobCategorySub;   // 직무 카테고리 소분류 (1순위 동기화용, 미저장)

    // 다중 희망직무 리스트 (J_참여자관리_희망직무 테이블과 매핑, 실제 저장 대상)
    private List<WishJobDTO> wishJobList;

    // DB 외 변수
    private String counselCondition; //개발자 구분

}
