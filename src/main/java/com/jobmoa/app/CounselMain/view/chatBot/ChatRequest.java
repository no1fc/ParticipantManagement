package com.jobmoa.app.CounselMain.view.chatBot;

import lombok.Data;

/**
 * ChatGPT 대화 요청 정보를 담는 DTO.
 * <p>
 * 사용자 메시지, OpenAI 스레드 ID, 어시스턴트 ID, Run ID를 포함하며,
 * ChatGPT Assistants API와의 대화 흐름에서 사용된다.
 * </p>
 */
@Data
public class ChatRequest {
    private String userMessage;
    private String threadId;
    private String assistantsId;
    private String runId;

}
