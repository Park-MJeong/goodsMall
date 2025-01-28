package com.hanghae.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomErrorDecoder implements ErrorDecoder {
    ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String responseMethod = response.request().method(); //HTTP 메서드
            String responseUrl = response.request().url(); //요청 URL
            String responseBody = new String(response.body().asInputStream().readAllBytes());

            log.error("Error Response Body: {}", responseBody);

            // JSON 파싱 ,body안의 에러메세지 추출
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode errorNode = rootNode.path("error");
            String errorMessage = errorNode.path("message").asText();

            String path = "["+responseMethod+"]"+ responseUrl;

            if (!errorMessage.isEmpty()) {
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put("message", errorMessage); //BusinessException message
                errorMap.put("path", path); //에러 경로

                log.info("Extracted Error Message: {}", errorMap);

                return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, errorMap.toString());
            }

            // 기본 에러 디코더 사용
            return defaultErrorDecoder.decode(methodKey, response);

        } catch (Exception e) {
            log.error("Error decoding response", e);
            return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}