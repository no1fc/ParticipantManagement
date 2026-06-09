package com.jobmoa.app.CounselMain.biz.recommend;

import java.util.List;

/**
 * AI 기반 참여자 채용정보 추천 서비스.
 * Gemini AI를 활용하여 참여자에게 적합한 채용공고를 추천하고 관리한다.
 */
public interface ParticipantJobRecommendService {

    /**
     * 추천 채용정보 목록을 조회한다.
     *
     * @param jobSeekerNo 구직자 번호
     * @return 추천 채용정보 목록
     */
    List<ParticipantJobRecommendDTO> getRecommendList(int jobSeekerNo);

    /**
     * 베스트 추천 채용정보를 조회한다.
     *
     * @param jobSeekerNo 구직자 번호
     * @return 베스트 추천 채용정보, 없으면 {@code null}
     */
    ParticipantJobRecommendDTO getBestRecommend(int jobSeekerNo);

    /**
     * 추천 채용정보를 저장(등록 또는 갱신)한다.
     *
     * @param dto 저장할 추천 채용정보
     * @return 저장 성공 여부
     */
    boolean saveRecommend(ParticipantJobRecommendDTO dto);

    /**
     * 해당 구직자의 추천 채용정보를 삭제한다.
     *
     * @param jobSeekerNo 구직자 번호
     * @return 삭제 성공 여부
     */
    boolean deleteRecommend(int jobSeekerNo);

    /**
     * 참여자 기본 정보를 조회한다.
     *
     * @param jobSeekerNo 구직자 번호
     * @return 참여자 기본 정보
     */
    RecommendParticipantDTO getParticipantInfo(int jobSeekerNo);

    /**
     * 참여자 희망직무 카테고리 목록을 조회한다.
     *
     * @param jobSeekerNo 구직자 번호
     * @return 카테고리 목록
     */
    List<RecommendCategoryDTO> getParticipantCategory(int jobSeekerNo);

    /**
     * 참여자 알선 상세 정보를 조회한다.
     *
     * @param jobSeekerNo 구직자 번호
     * @return 알선 상세 정보
     */
    RecommendReferralDTO getParticipantReferral(int jobSeekerNo);

    /**
     * 프론트엔드 전달용 추천 상세 응답 JSON 구조를 생성한다.
     * 참여자 정보, 카테고리, 알선정보, 추천 목록, 쿨다운 상태를 포함한다.
     *
     * @param jobSeekerNo 구직자 번호
     * @return 추천 상세 응답 DTO
     */
    RecommendParticipantResponseDTO getRecommendDetailResponse(int jobSeekerNo);

    /**
     * AI 추천을 실행하고 결과를 저장한다 (Gemini 호출 + DB 저장 전체 흐름).
     * 24시간 이내 재요청 시 기존 결과를 재사용한다.
     *
     * @param jobSeekerNo 구직자 번호
     * @return 추천 처리 결과
     */
    ProcessRecommendResultDTO processAndSaveRecommend(int jobSeekerNo);

    /**
     * AI 추천을 실행하고 결과를 저장한다.
     *
     * @param jobSeekerNo  구직자 번호
     * @param forceRefresh {@code true}이면 24시간 쿨다운을 무시하고 강제 갱신
     * @return 추천 처리 결과
     */
    ProcessRecommendResultDTO processAndSaveRecommend(int jobSeekerNo, boolean forceRefresh);

    /**
     * 상담일지 복사용 채용공고 상세 정보를 단건 조회한다.
     *
     * @param wantedAuthNo 구인인증번호
     * @return 채용공고 상세 정보
     */
    JobPostingCopyDTO getJobPostingDetail(String wantedAuthNo);
}
