package com.jobmoa.app.CounselMain.biz.recommend;

import java.util.List;

public interface ParticipantJobRecommendService {
    // 추천 채용정보 리스트 조회
    List<ParticipantJobRecommendDTO> getRecommendList(int jobSeekerNo);
    // 베스트 추천 채용정보 조회
    ParticipantJobRecommendDTO getBestRecommend(int jobSeekerNo);
    // 추천 채용정보 저장
    boolean saveRecommend(ParticipantJobRecommendDTO dto);
    // 추천 채용정보 삭제
    boolean deleteRecommend(int jobSeekerNo);
    // 참여자 정보 조회
    RecommendParticipantDTO getParticipantInfo(int jobSeekerNo);
    // 참여자 카테고리 조회
    List<RecommendCategoryDTO> getParticipantCategory(int jobSeekerNo);
    // 참여자 알선상세정보 조회
    RecommendReferralDTO getParticipantReferral(int jobSeekerNo);
    // 프론트 전달용 최종 JSON 구조 생성
    RecommendParticipantResponseDTO getRecommendDetailResponse(int jobSeekerNo);

    // AI 추천 저장 처리 (Gemini 호출 + DB 저장 전체 흐름)
    ProcessRecommendResultDTO processAndSaveRecommend(int jobSeekerNo);
    ProcessRecommendResultDTO processAndSaveRecommend(int jobSeekerNo, boolean forceRefresh);
}
