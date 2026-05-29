package com.jobmoa.app.recruitmentFormation.biz;

import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentPostingDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentResultDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentSearchDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentSyncResultDTO;

import java.util.List;

public interface RecruitmentService {

    /**
     * 고용24 API 호출 → 검색 결과 반환 (1단계: 실시간 직접 호출)
     * JSP AJAX 및 컨트롤러 초기 로딩에 사용
     */
    RecruitmentResultDTO search(RecruitmentSearchDTO searchDTO);

    /**
     * 고용24 API 전체 페이지 호출 → DB 저장용 포스팅 리스트 반환 (2단계: 스케줄러 전용)
     * display=100 기준으로 모든 페이지를 순회해 누적 반환
     */
    List<RecruitmentPostingDTO> fetchAllForSync();

    /**
     * 고용24 API를 페이지 단위로 호출하고, 각 페이지를 즉시 DB에 저장한다.
     * 전체 데이터를 JVM 메모리에 누적하지 않도록 스케줄러에서 사용한다.
     */
    RecruitmentSyncResultDTO syncPostingsByPage(String syncDtm);

    /**
     * detail_fetched=0 인 신규 공고만 상세 API 호출 후 DB 업데이트 (스케줄러 전용)
     * 최대 5,000건 처리 / 3스레드 병렬 + 100건 배치당 1초 딜레이로 고용24 API Rate Limit 방지
     */
    void fetchDetailForNewPostings();
}
