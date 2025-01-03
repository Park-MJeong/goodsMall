package com.hanghae.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CustomErrorDecoder implements ErrorDecoder {
    ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorDecoder defaultErrorDecoder = new Default();

//    에러에서 path추출
    private static final Pattern pattern = Pattern.compile("(?<=during\\s)\\[[^\\]]+\\]\\s+to\\s+\\[[^\\]]+\\]");

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String responseMethod = response.request().method();
            String responseUrl = response.request().url();
            String responseBody = new String(response.body().asInputStream().readAllBytes());

            log.error("Error Response Body: {}", responseBody);

            // JSON 파싱
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode errorNode = rootNode.path("error");
            String errorMessage = errorNode.path("message").asText();

            String path = "["+responseMethod+"]"+ responseUrl;

            if (!errorMessage.isEmpty()) {
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put("message", errorMessage);
                errorMap.put("path", path);

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