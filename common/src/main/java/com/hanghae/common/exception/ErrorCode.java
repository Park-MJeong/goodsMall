package com.hanghae.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 인증 관련 에러 코드
    VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "인증 코드가 잘못되었습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다."),
    VERIFICATION_TEST_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "테스트코드실패"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이메일입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),

    // 로그인, 인증 관련 에러 코드
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "로그인 후 이용해주세요."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "로그인에 실패했습니다. 다시 시도해주세요."),
    PASSWORD_FAILED(HttpStatus.BAD_REQUEST, "입력하신 정보가 올바르지 않습니다. 다시 입력해주세요."),

    JWT_INVALID(HttpStatus.BAD_REQUEST, "인증 정보가 올바르지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "인증 정보가 만료되었습니다."),

    // 서버 관련 에러 코드
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),

    //    유저
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당 유저를 찾을 수 없습니다."),

    CART_NOT_FOUND(HttpStatus.BAD_REQUEST,"탈퇴한 유저의 장바구니 입니다."),
    CART_PRODUCT_ALREADY(HttpStatus.BAD_REQUEST,"해당 상품이 이미 장바구니에 존재합니다."),
    CART_PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당 장바구니 상품을 찾을 수 없습니다."),
    QUANTITY_INSUFFICIENT(HttpStatus.BAD_REQUEST,"원하시는 수량보다 재고가 적습니다."),
    CART_NOT_QUANTITY(HttpStatus.NOT_FOUND,"해당 장바구니에는 상품이 없습니다."),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 상품을 찾을 수 없습니다."),
    PRODUCT_SOLD_OUT(HttpStatus.BAD_REQUEST,"품절상품이 포함되어있습니다."),
    PRODUCT_PRE_SALE(HttpStatus.BAD_REQUEST,"아직 판매준비중인 상품입니다."),
    PRODUCT_NOT_ORDER(HttpStatus.BAD_REQUEST,"구매할수 없는 상품입니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
    NOT_YOUR_ORDER(HttpStatus.BAD_REQUEST,"주문정보와 아이디가 일치하지 않습니다."),
    ORDER_CANCELLED_FAILED(HttpStatus.BAD_REQUEST,"주문취소 불가능한 상태입니다."),


    PASSWORD_CURRENT_ERROR(HttpStatus.BAD_REQUEST,"현재 비밀번호와 일치하지 않습니다."),
    NEW_PASSWORD_ERROR(HttpStatus.BAD_REQUEST,"새 비밀번호와 일치하지 않습니다."),
    INVALID_PASSWORD_CHANGE(HttpStatus.BAD_REQUEST,"현재 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),


//    결제 실패
    PAYMENT_ALREADY(HttpStatus.NOT_FOUND, "이미 결제내역이 존재하는 주문입니다."),
    PAYMENT_ALREADY_COMPLETE(HttpStatus.BAD_REQUEST,"이미 결제완료된 주문입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제정보가 존재하지 않습니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.NOT_FOUND, "결제상태가 올바르지 않습니다."),
    FAILED_TIME_PAYMENT(HttpStatus.BAD_REQUEST,"시간초과로 결제가 취소됩니다."),
    FAILED_QUANTITY_PAYMENT(HttpStatus.BAD_REQUEST,"재고부족으로 결제가 취소됩니다."),
    FAILED_PAYMENT(HttpStatus.BAD_REQUEST,"결제 실패하셨습니다."),
    CANCELED_PAYMENT(HttpStatus.BAD_REQUEST,"결제 취소하셨습니다."),

//    레디스
    REDIS_NOT_FOUND(HttpStatus.BAD_REQUEST,"레디스에 저장된 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return httpStatus.value(); //오류번호
    }
}
