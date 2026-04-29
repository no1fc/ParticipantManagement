package com.jobmoa.app.jobPlacement.biz.jobPlacement;

import lombok.Data;

import java.util.List;

@Data
public class JobPlacementDTO {
    /**
     * 구직번호
     */
    private String jobNumber;

    /**
     * 상담사 지점
     */
    private String counselorBranch;

    /**
     * 상담사
     */
    private String counselor;

    /**
     * 상담사 아이디
     */
    private String counselorId;

    /**
     * 참여자
     */
    private String participant;

    /**
     * 성별
     */
    private String gender;

    /**
     * 생년월일
     */
    private String birthDate;

    /**
     * 주소
     */
    private String address;

    /**
     * 학교명
     */
    private String schoolName;

    /**
     * 전공
     */
    private String major;

    /**
     * 희망직무
     */
    private String desiredJob;

    /**
     * 희망순위
     */
    private int desiredJobRank;

    /**
     * 희망직무 목록 (순위별 객체 리스트)
     */
    private List<JobPlacementDTO> desiredJobList;

    /**
     * 경력
     */
    private String career;

    /**
     * 나이
     */
    private int age;

    /**
     * 자격증
     */
    private String certificate;

    /**
     * 여러 자격증
     */
    private String[] certificates;

    /**
     * 희망연봉
     */
    private String desiredSalary;

    /**
     * 이메일
     */
    private String email;

    /**
     * 고유번호
     */
    private String uniqueNumber;

    /**
     * 알선 상세 등록 정보
     */
    private int placementDetailNumber;

    /**
     * 알선 상세 정보
     */
    private String placementDetail;

    /**
     * 상담사 전화번호
     */
    private String counselorPhone;

    /**
     * 카테고리 대분류
     */
    private String jobCategoryLarge;

    /**
     * 카테고리 중분류
     */
    private String jobCategoryMid;

    /**
     * 카테고리 소분류
     */
    private String jobCategorySub;

    /**
     * 추천사
     */
    private String suggestionDetail;

    /**
     * 키워드
     */
    private String recommendedKeyword;

    /**
     * 키워드 배열
     */
    private String[] recommendedKeywords;

    /**
     * 페이지네이션 현재 페이지 번호
     */
    private int page;

    /**
     * 페이지네이션 시작 번호
     */
    private int startPage;

    /**
     * 페이지네이션 끝 번호
     */
    private int endPage;

    /**
     * 페이지네이션 불러올 데이터 개수
     */
    private int pageRows;

    /**
     * 페이지네이션 전체 데이터 개수
     */
    private int totalCount;

    /**
     * 검색 옵션
     */
    private String[] searchOption;

    /**
     * 개발자 변수
     */
    private String condition;

    /**
     * 검색 조건
     */
    private String searchType;

    /**
     * 검색어
     */
    private String searchKeyword;

    /**
     * 나이 필터 시작
     */
    private int ageStartFilter;

    /**
     * 나이 필터 끝
     */
    private int ageEndFilter;

    /**
     * 나이대 필터
     */
    private int ageRangeFilter;

    /**
     * 나이대 내용
     */
    private String ageRangeContent;

    /**
     * 주소 필터 배열
     */
    private String[] searchAddressFilter;

    /**
     * 카테고리 대분류 필터
     */
    private String jobCategoryLargeFilter;

    /**
     * 카테고리 중분류 필터
     */
    private String jobCategoryMidFilter;

    /**
     * 희망 연봉 필터 시작
     */
    private int desiredSalaryStartFilter;

    /**
     * 희망 연봉 필터 끝
     */
    private int desiredSalaryEndFilter;

    /**
     * 성별 필터
     */
    private String genderFilter;

    /**
     * 필터 사용 flag
     */
    private boolean filterFlag;

    /**
     * 이력서 요청 폼 기업명
     */
    private String companyName;

    /**
     * 이력서 요청 폼 담당자명
     */
    private String contactName;

    /**
     * 이력서 요청 폼 이메일
     */
    private String contactEmail;

    /**
     * 이력서 요청 폼 비상연락처
     */
    private String contactPhone;

    /**
     * 이력서 요청 폼 기타요청사항
     */
    private String contactOther;

    /**
     * 이력서 요청 폼 채용 기업(개인정보 수신자)을 위한 개인정보 처리 안내
     */
    private String companyPrivacy;

    /**
     * 이력서 요청 폼 채용 담당자 개인정보 수집·이용 동의
     */
    private String contactPrivacy;

    /**
     * 이력서 요청 폼 홍보 및 마케팅 정보 수신 동의
     */
    private String marketingConsent;

}