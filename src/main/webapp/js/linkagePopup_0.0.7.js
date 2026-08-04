/**
 * 국취 연계실적 독려 팝업 (임시기능 ~2026-10-31) — 시안 picture.jpg 반영.
 * - 대시보드 진입 시 /linkage-popup/summary 비동기 조회(비차단)로 렌더.
 * - 숨김 규칙: "오늘 하루 보지 않기" 체크 후 닫아야만 다음 날(자정 이후)까지 미노출.
 * - 카드1=상향 기준값(benchmark=평균×1.5), 카드2=1인당 목표, 카드3=본인, 카드4=잔여.
 * - 게이지=목표 대비 달성률(현재 ÷ 목표)만 % 표기. 건수 끝 라벨/마커 라벨은 제거.
 */
(function () {
    "use strict";

    const STORAGE_KEY = "linkagePopup_lastShown";
    const API_URL = "/linkage-popup/summary";

    let activeIntervalDays = 1;

    // 단계별 헤드라인/부제(디자인 카피 — DB 아닌 프론트 고정). 본문은 DB message 사용.
    const STAGE_COPY = {
        1: { headline: "목표를 달성했어요!", sub: "(최고예요 🎉)" },
        2: { headline: "조금만 더 힘내세요!", sub: "(평균보다 높아요)" },
        3: { headline: "평균 수준이에요", sub: "(한 걸음 더!)" },
        4: { headline: "거의 다 왔어요!", sub: "(평균에 근접했어요)" },
        5: { headline: "시작이 중요해요", sub: "(평균 절반 미만)" },
        6: { headline: "첫 연계를 시작해요", sub: "(아직 0건이에요)" }
    };

    function todayStr() {
        const d = new Date();
        const m = String(d.getMonth() + 1).padStart(2, "0");
        const day = String(d.getDate()).padStart(2, "0");
        return d.getFullYear() + "-" + m + "-" + day;
    }

    function daysBetween(fromStr, toStr) {
        const ms = new Date(toStr).getTime() - new Date(fromStr).getTime();
        return Math.floor(ms / 86400000);
    }

    function readState() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEY) || "null");
        } catch (e) {
            return null;
        }
    }

    function markSuppressed(intervalDays) {
        try {
            const interval = intervalDays && intervalDays > 0 ? intervalDays : 1;
            localStorage.setItem(STORAGE_KEY, JSON.stringify({ date: todayStr(), interval: interval }));
        } catch (e) {
            /* 저장 실패 무시 */
        }
    }

    function shouldSkip() {
        const st = readState();
        if (!st || !st.date) return false;
        const interval = st.interval && st.interval > 0 ? st.interval : 1;
        return daysBetween(st.date, todayStr()) < interval;
    }

    function setText(id, text) {
        const el = document.getElementById(id);
        if (el) el.textContent = text;
    }

    function escapeHtml(s) {
        return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
    }

    // 멀티라인 본문 렌더 — 마지막 줄을 강조(주황).
    function renderMessage(text) {
        const el = document.getElementById("lpMessage");
        if (!el) return;
        const lines = (text || "").replace(/\r/g, "").split("\n");
        const html = lines.map(function (line, i) {
            const safe = escapeHtml(line);
            return (i === lines.length - 1 && line.trim() !== "")
                ? '<span class="lp-msg-em">' + safe + "</span>"
                : safe;
        }).join("<br>");
        el.innerHTML = html;
    }

    // 게이지: 목표 대비 본인 달성률(현재 ÷ 목표, 0~100% clamp).
    // 끝 라벨(평균/목표 N건)·본인 마커는 참고용으로 유지하고, 달성률 %를 별도 표기.
    function applyGauge(data) {
        const target = data.targetCount;
        const my = data.myCount;

        const pos = target > 0 ? my / target : 0;
        const clamped = Math.max(0, Math.min(1, pos));
        const pct = (clamped * 100).toFixed(1) + "%";

        const fill = document.getElementById("lpGaugeFill");
        const marker = document.getElementById("lpGaugeMarker");
        if (fill) fill.style.width = pct;
        if (marker) marker.style.left = pct;

        setText("lpGaugePct", Math.round(clamped * 100) + "%"); // 달성률 % (건수 라벨 제거)
    }

    function closePopup() {
        const dontShow = document.getElementById("lpDontShowToday");
        if (dontShow && dontShow.checked) {
            markSuppressed(activeIntervalDays);
        }
        const overlay = document.getElementById("linkagePopupOverlay");
        if (overlay) overlay.style.display = "none";
    }

    function render(data) {
        const overlay = document.getElementById("linkagePopupOverlay");
        const modal = document.getElementById("linkagePopupModal");
        if (!overlay || !modal) return;

        activeIntervalDays = data.displayIntervalDays && data.displayIntervalDays > 0 ? data.displayIntervalDays : 1;

        modal.className = "lp-modal lp-stage-" + data.stage;

        const name = data.counselorName || "";
        setText("lpNameLabel", name);
        setText("lpNameMy", name);

        // 좌측 카드 값 (숫자만 — 단위는 JSP의 .lp-unit)
        setText("lpBenchmark", data.benchmarkCount);   // 카드1: 상향 기준값(평균×1.5)
        setText("lpTarget", data.targetCount);          // 카드2: 1인당 목표
        setText("lpMy", data.myCount);                  // 카드3: 본인
        setText("lpRemaining", data.remainingCount);    // 카드4: 잔여
        setText("lpTargetBase", data.maxPointThreshold); // 카드2 분자(목표분자)

        // 우측 단계 패널 (단계 색은 modal 클래스 --lp-accent로만 반영, "N단계" 표기 제거)
        const copy = STAGE_COPY[data.stage] || { headline: "", sub: "" };
        setText("lpStageHeadline", copy.headline);
        setText("lpStageSub", copy.sub);
        renderMessage(data.message);
        applyGauge(data);

        overlay.style.display = "flex"; // 노출만 — 기록은 체크+닫기 시에만

        const closeBtn = document.getElementById("lpCloseBtn");
        const footerClose = document.getElementById("lpFooterClose");
        if (closeBtn) closeBtn.addEventListener("click", closePopup);
        if (footerClose) footerClose.addEventListener("click", closePopup);
        overlay.addEventListener("click", function (e) {
            if (e.target === overlay) closePopup();
        });
    }

    function init() {
        if (shouldSkip()) return;
        $.ajax({
            url: API_URL,
            type: "GET",
            dataType: "json"
        }).done(function (data) {
            if (!data || !data.show) return;
            render(data);
        }).fail(function () {
            /* 조용히 무시 — 대시보드 진입을 방해하지 않음 */
        });
    }

    $(function () {
        init();
    });
})();
