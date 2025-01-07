package com.hanghae.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    private HttpStatus httpStatus;
    private int code;
    private String message;


    public BusinessException(ErrorCode errorCode) {
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getStatusCode(); //오류번호
        this.message = errorCode.getMessage();
    }
    public BusinessException(ErrorCode errorCode, String message) {
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getStatusCode();
        this.message = message;
    }


}
