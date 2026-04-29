package com.jobmoa.app.recruitmentFormation.biz;

import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentPostingDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 고용24 채용공고 동기화 스케줄러 (2단계)
 *
 * <p>하루 4회 (00:00 / 06:00 / 12:00 / 18:00) 고용24 API를 일괄 호출하여
 * JOB_POSTING 테이블을 최신 상태로 유지한다.
 *
 * <ul>
 *   <li>신규 공고: INSERT</li>
 *   <li>변경 공고: UPDATE (MERGE)</li>
 *   <li>마감/삭제 공고: is_active = 0 처리</li>
 * </ul>
 *
 * <p>트래픽 관리: 사용자 요청마다 외부 API를 호출하지 않고 스케줄러가
 * 일괄 수집한 데이터를 자체 DB에서 제공함으로써 고용24 서버 부하를 최소화한다.
 */
@Slf4j
@Component
public class RecruitmentScheduler {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private RecruitmentDAO recruitmentDAO;

    /**
     * 앱 시작 시 JOB_POSTING 테이블이 비어있으면 즉시 동기화
     * → 첫 배포 또는 테이블 재생성 후 데이터 없이 빈 화면이 되는 문제 방지
     * DB 연결 안정화를 위해 3초 대기 후 백그라운드 스레드에서 실행
     */
    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartupIfEmpty() {
        new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            try {
                RecruitmentSearchDTO probe = new RecruitmentSearchDTO();
                int count = recruitmentDAO.countPostings(probe);
                if (count == 0) {
                    log.info("[Scheduler] JOB_POSTING 테이블 비어있음 → 앱 시작 초기 동기화 실행");
                    syncJobPostings();
                } else {
                    log.info("[Scheduler] JOB_POSTING 테이블 {}건 확인 — 앱 시작 동기화 생략", count);
                }
            } catch (Exception e) {
                log.warn("[Scheduler] 앱 시작 초기 동기화 확인 실패 (테이블 미생성 또는 DB 오류): {}", e.getMessage());
            }
        }, "startup-job-sync").start();
    }

    /**
     * 채용공고 동기화 — 매일 00:00 / 06:00 / 12:00 / 18:00 실행
     * cron 표현식: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * *")
//    @Scheduled(cron = "0 * * * * *") //Test용
    public void syncJobPostings() {
        String syncDtm = LocalDateTime.now().format(FMT);
        log.info("[Scheduler] 채용공고 동기화 시작 — syncDtm={}", syncDtm);

        try {
            // 1) 고용24 API 전체 페이지 호출
            List<RecruitmentPostingDTO> postings = recruitmentService.fetchAllForSync();
            log.info("[Scheduler] API 수집 완료: {}건", postings.size());

            if (postings.isEmpty()) {
                log.warn("[Scheduler] 수집된 공고 없음 — DB 업데이트 생략");
                return;
            }

            // 2) 각 공고에 syncDtm 설정 후 UPSERT
            postings.forEach(p -> p.setSyncDtm(syncDtm));
            recruitmentDAO.upsertBatch(postings);
            log.info("[Scheduler] DB UPSERT 완료: {}건", postings.size());

            // 3) 현재 동기화에서 갱신되지 않은 공고 삭제
            //    (syncDtm < 현재 syncDtm = API에서 사라진 마감 공고)
            int deleted = recruitmentDAO.deleteOldPostings(syncDtm);
            log.info("[Scheduler] 마감 공고 삭제: {}건", deleted);

            // 4) 신규 공고(detail_fetched=0)만 상세 API 호출 후 DB 업데이트
            recruitmentService.fetchDetailForNewPostings();

            log.info("[Scheduler] 채용공고 동기화 완료 — syncDtm={}", syncDtm);

        } catch (Exception e) {
            log.error("[Scheduler] 채용공고 동기화 실패: {}", e.getMessage(), e);
            // 실패 시 기존 DB 데이터 유지 (deactivate 실행하지 않음)
        }
    }
}