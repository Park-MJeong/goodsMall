package com.goodsmall.common.security.Token;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TokenBlackListService {

    void addTokenToList(String value);              // Redis key-value 형태로 리스트 추가

    boolean isContainToken(String value);           // Redis key 기반으로 리스트 조회

    List<Object> getTokenBlackList();               // Redis Key 기반으로 BlackList를 조회합니다.

    void removeToken(String value);                 // Redis Key 기반으로 리스트 내 요소 제거
}