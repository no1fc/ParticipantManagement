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


/**
 * JSON 변환 유틸리티 컴포넌트.
 * <p>리스트, 객체, 배열, Map 등을 JSON 문자열로 변환하는 공통 기능을 제공한다.
 * ObjectMapper를 이용한 직렬화와 커스텀 람다 기반 변환을 모두 지원한다.</p>
 */
@Slf4j
@Component  // 유틸 클래스이므로 @Component가 더 적절
public class ChangeJson {

    @Autowired
    private ObjectMapper objectMapper;  // JSON 변환을 위한 ObjectMapper 추가

    /**
     * 주어진 리스트를 JSON 배열 형식의 문자열로 변환한다.
     * <p>각 항목의 JSON 변환 방식은 호출자가 람다로 지정한다.</p>
     * @param list 변환할 객체 리스트
     * @param getJsonString 개별 항목을 JSON 문자열로 변환하는 람다 함수
     * @return JSON 배열 문자열 (빈 리스트인 경우 "[]")
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
     * 객체를 JSON 문자열로 변환한다 (ObjectMapper 사용).
     * @param obj 변환할 객체
     * @return JSON 문자열 (변환 실패 시 "{}")
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
     * 정수 배열을 JSON 문자열로 변환한다.
     * @param array 변환할 정수 배열
     * @return JSON 배열 문자열 (변환 실패 시 "[]")
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
     * Map을 JSON 문자열로 변환한다.
     * @param map 변환할 Map 객체
     * @return JSON 문자열 (변환 실패 시 "{}")
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
