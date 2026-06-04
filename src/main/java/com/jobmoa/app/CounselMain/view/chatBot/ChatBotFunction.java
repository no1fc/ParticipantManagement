package com.jobmoa.app.CounselMain.view.chatBot;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * OpenAI API 호출을 담당하는 서비스 클래스.
 * <p>
 * OkHttp 클라이언트를 사용하여 OpenAI Assistants API v2에 HTTP 요청을 보내고
 * 응답을 반환한다. 싱글톤 OkHttpClient로 관리된다.
 * </p>
 */
@Slf4j
@Service
public class ChatBotFunction {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1";

    //TODO FIXME openAI 에서 발급된 API 키 입력
    @Value("")
    private String openAiApiKey;

    // 싱글톤으로 관리
    private final OkHttpClient client = new OkHttpClient();

    /**
     * OpenAI API에 JSON 요청을 보내고 응답 본문을 반환한다.
     *
     * @param requestBodyJson 요청 본문 JSON 문자열
     * @param requestHttp     API 엔드포인트 경로 (예: {@code /threads/xxx/messages})
     * @param isPost          POST 요청이면 {@code true}, GET 요청이면 {@code false}
     * @return API 응답 본문 문자열
     * @throws Exception API 호출 실패 시
     */
    public String chatBotRequestJson(String requestBodyJson, String requestHttp, boolean isPost) throws Exception{
        Request request = isRequestPost(requestBodyJson, requestHttp, isPost);

        // API 호출 및 응답 처리
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new Exception("Failed: " + response.message());
            }
            //log.info("response : [{}]",response);
            String bodystate = response.body().string();
            //log.info("response body String : [{}]",bodystate);
            return bodystate; // 응답 반환
        }
    }

    /**
     * HTTP 메서드(GET/POST)에 따라 OkHttp Request 객체를 생성한다.
     *
     * @param requestBodyJson 요청 본문 JSON 문자열
     * @param requestHttp     API 엔드포인트 경로
     * @param isPost          POST 요청 여부
     * @return 생성된 OkHttp {@link Request} 객체
     */
    private Request isRequestPost(String requestBodyJson, String requestHttp, boolean isPost){
        String url = OPENAI_API_URL+requestHttp;
        log.info("chatBotRequestJson url : [{}]",url);
        //log.info("chatBotRequestJson openAiApiKey : [{}]",openAiApiKey);

        // HTTP 요청 생성
        RequestBody body = RequestBody.create(
                requestBodyJson, MediaType.get("application/json; charset=utf-8")
        );

        if(isPost) {
            return new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + openAiApiKey)
                    .addHeader("OpenAI-Beta", "assistants=v2")
                    .post(body)
                    .build();
        }
        else {
            return new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + openAiApiKey)
                    .addHeader("OpenAI-Beta", "assistants=v2")
                    .get()
                    .build();
        }

    }
}
