package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

public interface PraHistoryService {
    boolean insertHistory(PraHistoryRequestDTO request, String branch, String writerId);
}
