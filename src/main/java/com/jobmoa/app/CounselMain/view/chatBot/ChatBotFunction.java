package com.jobmoa.app.CounselMain.view.chatBot;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatBotFunction {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1";

    //TODO FIXME openAI 에서 발급된 API 키 입력
    @Value("")
    private String openAiApiKey;

    // 싱글톤으로 관리
    private final OkHttpClient client = new OkHttpClient();

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
