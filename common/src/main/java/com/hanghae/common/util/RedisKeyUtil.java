package com.hanghae.common.util;

import jdk.jfr.Unsigned;

@Unsigned
public class RedisKeyUtil {
    private static final String REDIS_STOCK_KEY = "product:stock:%s";

    public static String getStockKey(Long productId) { return String.format(REDIS_STOCK_KEY, productId); }
}
