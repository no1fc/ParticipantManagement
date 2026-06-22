package com.jobmoa.app.CounselMain.biz.linkagePopup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link LinkagePopupService} 구현체.
 * <p>혼합 기준 산식: 목표=목표분자÷지점인원, 평균=전체연계÷전체인원, 기준값=평균×배수.
 * 6단계 분기 후 단계 문구의 토큰을 치환해 반환한다.</p>
 */
@Slf4j
@Service("linkagePopupService")
public class LinkagePopupServiceImpl implements LinkagePopupService {

    @Autowired
    private LinkagePopupDAO linkagePopupDAO;

    @Override
    public LinkagePopupResultDTO selectPopupSummary(String account, String counselorName, String branch) {
        LinkagePopupResultDTO result = new LinkagePopupResultDTO();

        LinkagePopupSettingDTO setting = linkagePopupDAO.selectSetting();
        if (setting == null || !setting.isDisplayEnabled()) {
            result.setShow(false);
            return result;
        }
        if (account == null || account.isBlank()) {
            result.setShow(false);
            return result;
        }

        // 평균 분모 N: 오버라이드 우선, 없으면 자동 COUNT
        int totalStaff = setting.getStaffCountOverride() != null
                ? setting.getStaffCountOverride()
                : linkagePopupDAO.selectTotalStaffCount();
        if (totalStaff <= 0) {
            result.setShow(false);
            return result;
        }

        // 목표 분모: 본인 지점 인원(0이면 전체 인원으로 폴백)
        int branchStaff = (branch == null || branch.isBlank()) ? 0 : linkagePopupDAO.selectBranchStaffCount(branch);
        int targetDenom = branchStaff > 0 ? branchStaff : totalStaff;

        String start = setting.getStartDate();
        String end = setting.getEndDate();
        int total = linkagePopupDAO.selectTotalLinkageCount(start, end);
        int my = linkagePopupDAO.selectMyLinkageCount(account, start, end);

        // 1인당 목표 = 목표분자 ÷ 지점인원, 소수점은 무조건 올림(ceil)
        int target = (int) Math.ceil((double) setting.getTargetNumerator() / targetDenom);
        if (target < 1) {
            target = 1; // 최소 목표 1건 보장
        }
        int average = (int) Math.round((double) total / totalStaff);
        // 카드1 '전체 상담사 평균(상향 기준값)' = 전체 평균 × 평균배수(반올림)
        int benchmark = (int) Math.round(average * setting.getAverageMultiplier());
        int remaining = Math.max(0, target - my);

        int stage = decideStage(my, target, average);
        String message = substitute(pickMessage(setting, stage), counselorName, my, target, average, remaining);

        result.setShow(true);
        result.setDisplayIntervalDays(Math.max(1, setting.getDisplayIntervalDays()));
        result.setStage(stage);
        result.setCounselorName(counselorName);
        result.setMyCount(my);
        result.setTargetCount(target);
        result.setAverageCount(average);
        result.setBenchmarkCount(benchmark);
        result.setRemainingCount(remaining);
        result.setMaxPointThreshold(setting.getTargetNumerator());
        result.setMessage(message);
        result.setPeriodLabel(start + " ~ " + end);
        return result;
    }

    /**
     * 본인 연계건수를 목표·평균과 비교해 6단계를 판정한다(상위부터 순차, 정수 기준).
     * <p>0건은 항상 6단계로 처리하며, 평균이 0인 경계에서도 0건이 3단계로 빠지지 않도록 한다.</p>
     */
    private int decideStage(int my, int target, int average) {
        if (my >= target) return 1;          // 목표 달성
        if (my == 0) return 6;               // 0건
        if (my > average) return 2;          // 목표미달 + 평균초과
        if (my == average) return 3;         // 평균 동일
        if (my * 2 >= average) return 4;     // 평균미만 + 평균 50%이상
        return 5;                            // 평균 50% 미만
    }

    /** 단계 번호에 해당하는 문구 템플릿을 선택한다. */
    private String pickMessage(LinkagePopupSettingDTO s, int stage) {
        switch (stage) {
            case 1: return s.getStage1Message();
            case 2: return s.getStage2Message();
            case 3: return s.getStage3Message();
            case 4: return s.getStage4Message();
            case 5: return s.getStage5Message();
            default: return s.getStage6Message();
        }
    }

    /** 문구 토큰을 실제 값으로 치환한다. */
    private String substitute(String template, String name, int my, int target, int average, int remaining) {
        if (template == null) return "";
        return template
                .replace("{상담사명}", name == null ? "" : name)
                .replace("{본인건수}", String.valueOf(my))
                .replace("{목표건수}", String.valueOf(target))
                .replace("{평균건수}", String.valueOf(average))
                .replace("{잔여건수}", String.valueOf(remaining));
    }
}
