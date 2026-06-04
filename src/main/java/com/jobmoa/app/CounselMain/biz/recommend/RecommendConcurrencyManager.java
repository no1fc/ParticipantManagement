package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gemini AI 추천 요청의 동시성 제어 관리자.
 *
 * <p>사용자별 동시 추천 요청 수를 {@code MAX_CONCURRENT}(기본 5)로 제한하여
 * 과도한 API 호출을 방지한다. CAS(Compare-And-Set) 기반의 원자적 카운터를 사용하여
 * 스레드 안전성을 보장한다.
 */
@Slf4j
@Component
public class RecommendConcurrencyManager {

    private static final int MAX_CONCURRENT = 5;
    private final ConcurrentHashMap<String, AtomicInteger> activeCountMap = new ConcurrentHashMap<>();

    /**
     * 지정 사용자의 동시 추천 슬롯 획득을 시도한다.
     *
     * @param memberUserID 사용자 ID
     * @return 슬롯 획득 성공 시 {@code true}, 한도 초과 시 {@code false}
     */
    public boolean tryAcquire(String memberUserID) {
        AtomicInteger count = activeCountMap.computeIfAbsent(memberUserID, k -> new AtomicInteger(0));
        while (true) {
            int current = count.get();
            if (current >= MAX_CONCURRENT) {
                log.warn("[ConcurrencyManager] 동시 추천 한도 초과 user={}, current={}", memberUserID, current);
                return false;
            }
            if (count.compareAndSet(current, current + 1)) {
                log.info("[ConcurrencyManager] 슬롯 획득 user={}, active={}/{}", memberUserID, current + 1, MAX_CONCURRENT);
                return true;
            }
        }
    }

    /**
     * 지정 사용자의 동시 추천 슬롯을 반환한다.
     *
     * @param memberUserID 사용자 ID
     */
    public void release(String memberUserID) {
        AtomicInteger count = activeCountMap.get(memberUserID);
        if (count != null) {
            int newVal = count.updateAndGet(v -> Math.max(0, v - 1));
            log.info("[ConcurrencyManager] 슬롯 해제 user={}, active={}/{}", memberUserID, newVal, MAX_CONCURRENT);
        }
    }

    /**
     * 지정 사용자의 현재 활성 추천 요청 수를 반환한다.
     *
     * @param memberUserID 사용자 ID
     * @return 활성 요청 수
     */
    public int getActiveCount(String memberUserID) {
        AtomicInteger count = activeCountMap.get(memberUserID);
        return count != null ? count.get() : 0;
    }

    /**
     * 지정 사용자의 잔여 동시 추천 슬롯 수를 반환한다.
     *
     * @param memberUserID 사용자 ID
     * @return 사용 가능한 잔여 슬롯 수
     */
    public int getRemainingSlots(String memberUserID) {
        return MAX_CONCURRENT - getActiveCount(memberUserID);
    }
}
