package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("recommendService")
public class ParticipantJobRecommendServiceImpl implements ParticipantJobRecommendService {

    @Autowired
    private ParticipantJobRecommendDAO participantJobRecommendDAO;

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${gemini.recommend.max-candidates:20}")
    private int maxCandidates;

    // =========================================================
    // 기존 조회 메서드
    // =========================================================

    @Override
    public List<ParticipantJobRecommendDTO> getRecommendList(int jobSeekerNo) {
        log.debug("Fetching recommendation list for job seeker: {}", jobSeekerNo);
        return participantJobRecommendDAO.selectRecommendList(jobSeekerNo);
    }

    @Override
    public ParticipantJobRecommendDTO getBestRecommend(int jobSeekerNo) {
        log.debug("Fetching best recommendation for job seeker: {}", jobSeekerNo);
        return participantJobRecommendDAO.selectBestRecommend(jobSeekerNo);
    }

    public ParticipantJobRecommendDTO getRecommendByCertNo(int jobSeekerNo, String certNo) {
        log.debug("Fetching recommendation by certNo: jobSeekerNo={}, certNo={}", jobSeekerNo, certNo);
        return participantJobRecommendDAO.selectRecommendByCertNo(jobSeekerNo, certNo);
    }

    @Override
    public boolean saveRecommend(ParticipantJobRecommendDTO dto) {
        log.debug("Saving recommendation for job seeker: {}", dto.getJobSeekerNo());
        boolean flag = participantJobRecommendDAO.insertOrUpdateRecommend(dto) > 0;
        log.debug("Recommendation saved: {}", flag);
        return flag;
    }

    @Override
    public boolean deleteRecommend(int jobSeekerNo) {
        log.debug("Deleting recommendation for job seeker: {}", jobSeekerNo);
        boolean flag = participantJobRecommendDAO.deleteRecommendByGujikNo(jobSeekerNo) > 0;
        log.debug("Recommendation deleted: {}", flag);
        return flag;
    }

    @Override
    public RecommendParticipantDTO getParticipantInfo(int jobSeekerNo) {
        log.debug("Fetching participant info for job seeker: {}", jobSeekerNo);
        return participantJobRecommendDAO.getParticipantInfo(jobSeekerNo);
    }

    @Override
    public List<RecommendCategoryDTO> getParticipantCategory(int jobSeekerNo) {
        log.debug("Fetching participant category for job seeker: {}", jobSeekerNo);
        return participantJobRecommendDAO.getParticipantCategory(jobSeekerNo);
    }

    @Override
    public RecommendReferralDTO getParticipantReferral(int jobSeekerNo) {
        log.debug("Fetching participant referral for job seeker: {}", jobSeekerNo);
        return participantJobRecommendDAO.getParticipantReferral(jobSeekerNo);
    }

    // =========================================================
    // 모달 응답 JSON 조립
    // =========================================================

    @Override
    public RecommendParticipantResponseDTO getRecommendDetailResponse(int jobSeekerNo) {
        RecommendParticipantDTO participantInfo   = getParticipantInfo(jobSeekerNo);
        List<RecommendCategoryDTO> categoryList  = getParticipantCategory(jobSeekerNo);
        RecommendReferralDTO referralInfo         = getParticipantReferral(jobSeekerNo);
        List<ParticipantJobRecommendDTO> recommendList = getRecommendList(jobSeekerNo);
        ParticipantJobRecommendDTO bestRecommend  = getBestRecommend(jobSeekerNo);

        ParticipantDetailResponseDTO participant = new ParticipantDetailResponseDTO();
        if (participantInfo != null) {
            participant.setInfoGujikNo(participantInfo.getJobSeekerNo());
            participant.setSummaryGender(participantInfo.getSummaryGender());
            participant.setInfoName(participantInfo.getInfoName());
            participant.setInfoStage(participantInfo.getInfoStage());
            participant.setInfoFocus(participantInfo.isInfoFocus());
            participant.setInfoEducation(participantInfo.getInfoEducation());
            participant.setInfoMajor(participantInfo.getInfoMajor());
        }
        participant.setCategoryList(categoryList);
        participant.setReferral(referralInfo);

        RecommendParticipantResponseDTO response = new RecommendParticipantResponseDTO();
        response.setSuccess(true);
        response.setParticipant(participant);
        response.setRecommendList(recommendList);
        response.setBestRecommend(bestRecommend != null ? bestRecommend : new ParticipantJobRecommendDTO());
        response.setShareSummary(null);

        // 24시간 쿨다운 상태 확인 (DB 서버 시간 기준 — DATEDIFF)
        Map<String, Object> cooldownStatus =
            participantJobRecommendDAO.selectCooldownStatus(jobSeekerNo);
        if (cooldownStatus != null) {
            response.setLastRecommendedAt((String) cooldownStatus.get("lastRecommendedAt"));
            Object active = cooldownStatus.get("cooldownActive");
            response.setCooldownActive(active != null && Integer.valueOf(1).equals(active));
        } else {
            response.setCooldownActive(false);
        }

        return response;
    }

    // =========================================================
    // AI 추천 저장 — 전체 흐름
    // =========================================================

    @Override
    public ProcessRecommendResultDTO processAndSaveRecommend(int jobSeekerNo) {
        return processAndSaveRecommend(jobSeekerNo, false);
    }

    @Override
    public ProcessRecommendResultDTO processAndSaveRecommend(int jobSeekerNo, boolean forceRefresh) {
        ProcessRecommendResultDTO result = new ProcessRecommendResultDTO();

        // 1. 참여자 정보 조회
        RecommendParticipantDTO participant =
            participantJobRecommendDAO.getParticipantInfo(jobSeekerNo);
        if (participant == null) {
            result.setSuccess(false);
            result.setMessage("참여자 정보를 찾을 수 없습니다.");
            result.setJobSeekerNo(jobSeekerNo);
            return result;
        }
        // 참여자명과 구직번호를 결과에 설정 (알림 표시용)
        result.setParticipantName(participant.getInfoName());
        result.setJobSeekerNo(jobSeekerNo);

        List<RecommendCategoryDTO> categoryList =
            participantJobRecommendDAO.getParticipantCategory(jobSeekerNo);
        RecommendReferralDTO referralInfo =
            participantJobRecommendDAO.getParticipantReferral(jobSeekerNo);

        // 2. 24시간 이내 재사용 (forceRefresh = false 인 경우만, DB 서버 시간 기준)
        if (!forceRefresh) {
            Map<String, Object> cooldownStatus =
                participantJobRecommendDAO.selectCooldownStatus(jobSeekerNo);
            if (cooldownStatus != null) {
                Object active = cooldownStatus.get("cooldownActive");
                if (active != null && Integer.valueOf(1).equals(active)) {
                    log.info("[추천저장] 24h 이내 재사용 jobSeekerNo={}", jobSeekerNo);
                    result.setSuccess(true);
                    result.setReused(true);
                    result.setLastRecommendedAt((String) cooldownStatus.get("lastRecommendedAt"));
                    result.setMessage("최근 24시간 이내 저장된 추천 결과를 사용합니다.");
                    return result;
                }
            }
        }

        // 3. 정보 충분 여부 확인
        if (!geminiApiService.hasEnoughInfo(participant, categoryList)) {
            result.setSuccess(false);
            result.setMessage("참여자 기본 정보가 부족하여 추천을 진행할 수 없습니다.");
            return result;
        }

        // 3-1. 희망직무 기반 관련 카테고리 DB 조회
        List<String> midCategoryNames = categoryList.stream()
                .map(RecommendCategoryDTO::getCategoryMiddle)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        List<JobCategoryDTO> relatedCategories = Collections.emptyList();
        if (!midCategoryNames.isEmpty()) {
            relatedCategories = participantJobRecommendDAO.getRelatedJobCategories(midCategoryNames);
            log.info("[추천저장] 관련 카테고리 조회 midCategoryNames={}, 결과={}건", midCategoryNames, relatedCategories.size());
        }

        // 4. Gemini 1단계: 검색 조건 생성
        SearchConditionDTO searchCondition;
        try {
            searchCondition = geminiApiService.generateSearchCondition(participant, referralInfo, categoryList, relatedCategories);
            log.info("[추천저장] Gemini 검색조건 생성 jobSeekerNo={}, searchCondition={}", jobSeekerNo, searchCondition);

            result.setSearchCondition(searchCondition);
        } catch (Exception e) {
            log.error("[추천저장] Gemini 검색조건 생성 실패 jobSeekerNo={}", jobSeekerNo, e);
            result.setSuccess(false);
            String msg = (e.getMessage() != null && e.getMessage().contains("Gemini"))
                    ? "AI 추천 서비스가 일시적으로 응답하지 않습니다. 잠시 후 다시 시도해주세요."
                    : "추천 저장 중 오류가 발생했습니다.";
            result.setMessage(msg);
            return result;
        }

        // 5. 채용정보 후보군 조회 (maxCount 필수 포함)
        searchCondition.setMaxCount(maxCandidates);
        log.info("[추천저장] Gemini 검색조건 생성 jobSeekerNo={}, searchCondition={}", jobSeekerNo, searchCondition);
        List<JobCandidateDTO> candidates =
            participantJobRecommendDAO.selectJobInfoCandidates(searchCondition);
        log.debug("[추천저장] 후보군 {}건 조회 jobSeekerNo={}", candidates.size(), jobSeekerNo);

        if (candidates.isEmpty()) {
            result.setSuccess(true);
            result.setSavedCount(0);
            result.setMessage("조건에 맞는 채용정보를 찾지 못했습니다.");
            return result;
        }

        // 6. 기존 추천 삭제
        int deleted = participantJobRecommendDAO.deleteRecommendByGujikNo(jobSeekerNo);
        log.debug("기존 추천 삭제 결과: {}", deleted > 0);

        // 7. 알선상세정보 유무에 따라 분기 저장
        String alsonDetail  = (referralInfo != null) ? referralInfo.getInfoAlsonDetail() : null;
        boolean hasAlsonDetail = (alsonDetail != null && !alsonDetail.trim().isEmpty());

//        log.debug("알선상세정보 유무: {} , referralInfo: {}, alsonDetail: {}", hasAlsonDetail, referralInfo, alsonDetail);

        if (hasAlsonDetail) {
            saveWithGeminiJudgment(participant, categoryList, referralInfo, candidates, alsonDetail, searchCondition);
        } else {
            saveWithoutGeminiJudgment(participant, categoryList, referralInfo, candidates, searchCondition);
        }

        log.debug("[추천저장] 완료 jobSeekerNo={}, {}건", jobSeekerNo, candidates.size());
        result.setSuccess(true);
        result.setSavedCount(candidates.size());
        return result;
    }

    // =========================================================
    // 저장 분기 헬퍼
    // =========================================================

    /**
     * 알선상세정보 있는 경우: Gemini 2단계로 베스트 선별 후 저장
     * bestJobInfo = "1" (베스트), "0" (일반)
     */
    private void saveWithGeminiJudgment(
            RecommendParticipantDTO participant,
            List<RecommendCategoryDTO> categoryList,
            RecommendReferralDTO referralInfo,
            List<JobCandidateDTO> candidates,
            String alsonDetail,
            SearchConditionDTO searchCondition) {

        BestSelectionResultDTO judgment;
        try {
            judgment = geminiApiService.selectBestFromCandidates(candidates, participant, alsonDetail);
            log.debug("[추천저장] Gemini 2단계 성공, 베스트 선택 결과: {}", judgment);
        } catch (Exception e) {
            log.warn("[추천저장] Gemini 2단계 실패, 베스트 없이 저장: {}", e.getMessage());
            saveWithoutGeminiJudgment(
                participant, categoryList, referralInfo, candidates, searchCondition);
            return;
        }

        String bestGujinNo = judgment.getBestGujinNo();

        // certNo 기준 점수 Map 구성 (B2 수정: 이제 certNo 필드 사용)
        Map<String, RecommendationScoreDTO> scoreMap = new HashMap<>();
        if (judgment.getScores() != null) {
            for (RecommendationScoreDTO s : judgment.getScores()) {
                if (s.getCertNo() != null) {
                    scoreMap.put(s.getCertNo(), s);
                }
            }
        }

        for (JobCandidateDTO candidate : candidates) {
            String certNo = candidate.getCertNo();
            RecommendationScoreDTO scoreInfo = scoreMap.get(certNo);

            ParticipantJobRecommendDTO dto =
                buildRecommendDto(participant, categoryList, referralInfo, candidate, searchCondition);

            dto.setBestJobInfo(certNo != null && certNo.equals(bestGujinNo));

            if (scoreInfo != null) {
                dto.setRecommendationScore(scoreInfo.getScore());
                dto.setRecommendationReason(scoreInfo.getReason());
            }

            int inserted = participantJobRecommendDAO.insertOrUpdateRecommend(dto);
            log.debug("[추천저장] 알선상세정보 있는 경우 후보자 저장 결과: {}", inserted > 0);
        }
    }

    /**
     * 알선상세정보 없는 경우: 후보군 전체 bestJobInfo = 0 으로 저장
     */
    private void saveWithoutGeminiJudgment(
            RecommendParticipantDTO participant,
            List<RecommendCategoryDTO> categoryList,
            RecommendReferralDTO referralInfo,
            List<JobCandidateDTO> candidates,
            SearchConditionDTO searchCondition) {

        for (JobCandidateDTO candidate : candidates) {
            ParticipantJobRecommendDTO dto =
                buildRecommendDto(participant, categoryList, referralInfo, candidate, searchCondition);
            dto.setBestJobInfo(false);
            int inserted = participantJobRecommendDAO.insertOrUpdateRecommend(dto);
            log.debug("[추천저장] 알선상세정보 없는 경우 후보자 저장 결과: {}", inserted > 0);
        }
    }

    /**
     * 추천 DTO 조립 (B1 수정: Map 키 접근 → JobCandidateDTO getter 사용)
     */
    private ParticipantJobRecommendDTO buildRecommendDto(
            RecommendParticipantDTO participant,
            List<RecommendCategoryDTO> categoryList,
            RecommendReferralDTO referralInfo,
            JobCandidateDTO candidate,
            SearchConditionDTO searchCondition) {

        ParticipantJobRecommendDTO dto = new ParticipantJobRecommendDTO();

        dto.setJobSeekerNo(participant.getJobSeekerNo());
        dto.setParticipantName(participant.getInfoName());
        dto.setProgressStage(participant.getInfoStage());
        dto.setEducation(participant.getInfoEducation());
        dto.setMajor(participant.getInfoMajor());


        // RecommendCategoryDTO 희망순위 1순위 희망직무만 사용
        if (categoryList != null && !categoryList.isEmpty()) {
            RecommendCategoryDTO primary = categoryList.get(0);
//            log.info("[추천저장] 카테고리 대/중분류 설정 jobSeekerNo={}, category={}", participant.getJobSeekerNo(), primary);
            // 카테고리 대/중분류는 J_참여자관리에 저장된 값 사용
            dto.setCategoryMajor(primary.getCategoryMain());
            dto.setCategoryMiddle(primary.getCategoryMiddle());
            dto.setDesiredJob(primary.getInfoJob());
        }

        if (referralInfo != null) {
            dto.setReferralDetail(referralInfo.getInfoAlsonDetail());
        }

        // Gemini 생성 검색 조건 직렬화
        try {
            dto.setGeneratedSearchCondition(objectMapper.writeValueAsString(searchCondition));
        } catch (Exception e) {
            log.warn("[추천저장] 검색조건 직렬화 실패: {}", e.getMessage());
        }

        // 채용정보 — JobCandidateDTO getter 사용
        dto.setRecommendedJobCertNo(candidate.getCertNo());
        dto.setRecommendedJobUrl(candidate.getJobPostingUrl());
        dto.setRecommendedJobCompany(candidate.getCompanyName());
        dto.setRecommendedJobTitle(candidate.getRecruitTitle());
        dto.setRecommendedJobIndustry(candidate.getIndustryType());

        return dto;
    }

    // =========================================================
    // 공유 요약 생성
    // =========================================================

    /**
     * 카카오톡 공유용 요약 텍스트 생성
     */
    private String buildShareSummary(
            RecommendParticipantDTO participantInfo,
            ParticipantJobRecommendDTO bestRecommend) {

        if (participantInfo == null || bestRecommend == null) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("[AI 추천 채용정보]\n");
        sb.append("이름: ").append(participantInfo.getInfoName()).append("\n");
        sb.append("추천 기업: ").append(bestRecommend.getRecommendedJobCompany()).append("\n");
        sb.append("채용 공고: ").append(bestRecommend.getRecommendedJobTitle()).append("\n");
        if (bestRecommend.getRecommendedJobUrl() != null) {
            sb.append("공고 링크: ").append(bestRecommend.getRecommendedJobUrl()).append("\n");
        }
        if (bestRecommend.getRecommendationReason() != null) {
            sb.append("추천 이유: ").append(bestRecommend.getRecommendationReason()).append("\n");
        }
        return sb.toString();
    }
}
