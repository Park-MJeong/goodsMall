package com.hanghae.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private HttpStatus httpStatus;
    private int code;
    private String message;


    public BusinessException(ErrorCode errorCode) {
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getStatusCode(); //오류번호
        this.message = errorCode.getMessage();
    }

}
