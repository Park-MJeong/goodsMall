package com.goodsmall.common.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomCodeUtil {
    //    인증코드 생성
    @Bean
    public String createRandomCode(){
        int startLimit = 48; // number 0~9 => 48~57
        int endLimit = 122; // alphabet 'A~Z' => 65~90 , 'a~z' =>97~122
        int targetStringLength = 7;
        Random random = new Random();

        return random.ints(startLimit, endLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 | i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
