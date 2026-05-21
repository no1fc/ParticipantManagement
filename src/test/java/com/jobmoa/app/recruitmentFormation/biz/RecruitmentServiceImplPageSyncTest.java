package com.jobmoa.app.recruitmentFormation.biz;

import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentPostingDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentSyncResultDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecruitmentServiceImplPageSyncTest {

    @Test
    void syncPostingsByPageSavesEachPageWithoutAccumulatingAllPostings() {
        TestableRecruitmentService service = new TestableRecruitmentService(150);
        service.addPage(1, postings("K1-", 100));
        service.addPage(2, postings("K2-", 50));

        RecruitmentSyncResultDTO result = service.syncPostingsByPage("2026-05-20 12:00:00");

        assertTrue(result.isAllPagesFetched());
        assertEquals(150, result.getTotalCount());
        assertEquals(2, result.getExpectedPages());
        assertEquals(2, result.getCompletedPages());
        assertEquals(150, result.getFetchedCount());
        assertEquals(150, result.getSavedCount());
        assertEquals(List.of(100, 50), service.savedPageSizes);
        assertEquals(100, service.largestSavedBatchSize);
    }

    @Test
    void syncPostingsByPageStopsAndMarksIncompleteWhenMiddlePageFails() {
        TestableRecruitmentService service = new TestableRecruitmentService(250);
        service.addPage(1, postings("K1-", 100));
        service.failPage(2);
        service.addPage(3, postings("K3-", 50));

        RecruitmentSyncResultDTO result = service.syncPostingsByPage("2026-05-20 12:00:00");

        assertFalse(result.isAllPagesFetched());
        assertEquals(250, result.getTotalCount());
        assertEquals(3, result.getExpectedPages());
        assertEquals(1, result.getCompletedPages());
        assertEquals(100, result.getFetchedCount());
        assertEquals(100, result.getSavedCount());
        assertEquals(List.of(100), service.savedPageSizes);
    }

    @Test
    void syncPostingsByPageStopsAndMarksIncompleteWhenSavingPageFails() {
        TestableRecruitmentService service = new TestableRecruitmentService(200);
        service.addPage(1, postings("K1-", 100));
        service.addPage(2, postings("K2-", 100));
        service.failSaveAttempt(2);

        RecruitmentSyncResultDTO result = service.syncPostingsByPage("2026-05-20 12:00:00");

        assertFalse(result.isAllPagesFetched());
        assertEquals(200, result.getTotalCount());
        assertEquals(2, result.getExpectedPages());
        assertEquals(1, result.getCompletedPages());
        assertEquals(100, result.getFetchedCount());
        assertEquals(100, result.getSavedCount());
        assertEquals(List.of(100), service.savedPageSizes);
        assertTrue(result.getFailureMessage().contains("page=2"));
    }

    private static List<RecruitmentPostingDTO> postings(String prefix, int count) {
        List<RecruitmentPostingDTO> postings = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RecruitmentPostingDTO dto = new RecruitmentPostingDTO();
            dto.setWantedAuthNo(prefix + i);
            postings.add(dto);
        }
        return postings;
    }

    private static class TestableRecruitmentService extends RecruitmentServiceImpl {
        private final int totalCount;
        private final Map<Integer, List<RecruitmentPostingDTO>> pages = new HashMap<>();
        private final List<Integer> failedPages = new ArrayList<>();
        private final List<Integer> failedSaveAttempts = new ArrayList<>();
        private final List<Integer> savedPageSizes = new ArrayList<>();
        private int largestSavedBatchSize;
        private int saveAttempts;

        private TestableRecruitmentService(int totalCount) {
            this.totalCount = totalCount;
        }

        private void addPage(int page, List<RecruitmentPostingDTO> postings) {
            pages.put(page, postings);
        }

        private void failPage(int page) {
            failedPages.add(page);
        }

        private void failSaveAttempt(int attempt) {
            failedSaveAttempts.add(attempt);
        }

        @Override
        protected SyncPage fetchSyncPage(int page) {
            if (failedPages.contains(page)) {
                throw new IllegalStateException("page failed");
            }
            return new SyncPage(pages.getOrDefault(page, List.of()), totalCount);
        }

        @Override
        protected void saveSyncPage(List<RecruitmentPostingDTO> postings, String syncDtm) {
            saveAttempts++;
            if (failedSaveAttempts.contains(saveAttempts)) {
                throw new IllegalStateException("save failed");
            }
            for (RecruitmentPostingDTO posting : postings) {
                assertEquals(syncDtm, posting.getSyncDtm());
            }
            savedPageSizes.add(postings.size());
            largestSavedBatchSize = Math.max(largestSavedBatchSize, postings.size());
        }
    }
}
