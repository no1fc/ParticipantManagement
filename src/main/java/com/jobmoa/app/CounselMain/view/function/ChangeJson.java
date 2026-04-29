package com.jobmoa.app.CounselMain.view.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//@Slf4j
//@Component
//public class ChangeJson {
//    /**
//     * 주어진 리스트를 JSON 배열 형식의 문자열로 변환하는 유틸리티 메서드입니다.
//     * 사용 방법
//     *changeJson.convertListToJsonArray(datas, item -> {
//     *             ExDTO dto = (ExDTO) item;  // 객체 캐스팅
//     *             return "{\"jsonID\":\"" + dto.jsonDATA + "\","
//     *                     + "\"jsonID\":\"" + dto.jsonDATA + "\"}";
//     *
//     * @param list 변환할 객체 리스트
//     * @param getJsonString 변환할 개별 아이템을 JSON 문자열로 만드는 람다 함수
//     * @return 변환된 JSON 배열 문자열
//     */
//
//    @Autowired
//    private ObjectMapper objectMapper;  // JSON 변환을 위한 ObjectMapper 추가
//
//    public String convertListToJsonArray(List<?> list, Function<Object, String> getJsonString) {
//        // 리스트가 null이거나 비어있으면 빈 배열을 반환
//        if (list == null || list.isEmpty()) {
//            return "[]";
//        }
//
//        // JSON 배열을 상태적으로 생성하기 위한 StringBuilder 사용
//        StringBuilder jsonArrayBuilder = new StringBuilder("[");
//
//        // 리스트의 각 아이템을 JSON 형식으로 변환하여 추가
//        for (Object item : list) {
//            jsonArrayBuilder.append(getJsonString.apply(item)).append(",");
//        }
//
//        // 마지막에 추가된 ','(쉼표)를 제거하고 배열 닫기
//        if (jsonArrayBuilder.length() > 1) {
//            jsonArrayBuilder.setLength(jsonArrayBuilder.length() - 1);
//        }
//        jsonArrayBuilder.append("]");
//
//        // 로그에 변환 결과 출력
//        //log.info("Converted JSON Array: [{}]", jsonArrayBuilder);
//
//        // 최종 JSON 배열 문자열 반환
//        return jsonArrayBuilder.toString();
//    }
//}


@Slf4j
@Component  // 유틸 클래스이므로 @Component가 더 적절
public class ChangeJson {

    @Autowired
    private ObjectMapper objectMapper;  // JSON 변환을 위한 ObjectMapper 추가

    /**
     * 기존 메서드 - 주어진 리스트를 JSON 배열 형식의 문자열로 변환
     */
    public String convertListToJsonArray(List<?> list, Function<Object, String> getJsonString) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }

        StringBuilder jsonArrayBuilder = new StringBuilder("[");

        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                jsonArrayBuilder.append(",");
            }
            jsonArrayBuilder.append(getJsonString.apply(list.get(i)));
        }
        jsonArrayBuilder.append("]");

        return jsonArrayBuilder.toString();
    }

    /**
     * 객체를 JSON 문자열로 변환 (ObjectMapper 사용)
     */
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON 변환 실패: ", e);
            return "{}";
        }
    }

    /**
     * 배열을 JSON 문자열로 변환
     */
    public String arrayToJson(int[] array) {
        try {
            return objectMapper.writeValueAsString(array);
        } catch (Exception e) {
            log.error("배열 JSON 변환 실패: ", e);
            return "[]";
        }
    }

    /**
     * Map을 JSON 문자열로 변환
     */
    public String mapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Map JSON 변환 실패: ", e);
            return "{}";
        }
    }
}
