package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RecommendConcurrencyManager {

    private static final int MAX_CONCURRENT = 5;
    private final ConcurrentHashMap<String, AtomicInteger> activeCountMap = new ConcurrentHashMap<>();

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

    public void release(String memberUserID) {
        AtomicInteger count = activeCountMap.get(memberUserID);
        if (count != null) {
            int newVal = count.updateAndGet(v -> Math.max(0, v - 1));
            log.info("[ConcurrencyManager] 슬롯 해제 user={}, active={}/{}", memberUserID, newVal, MAX_CONCURRENT);
        }
    }

    public int getActiveCount(String memberUserID) {
        AtomicInteger count = activeCountMap.get(memberUserID);
        return count != null ? count.get() : 0;
    }

    public int getRemainingSlots(String memberUserID) {
        return MAX_CONCURRENT - getActiveCount(memberUserID);
    }
}
