package com.hanghae.common.util;

import jdk.jfr.Unsigned;

@Unsigned
public class RedisKeyUtil {
    private static final String REDIS_STOCK_KEY = "product:stock:%s";
    private static final String REDIS_PRODUCT_KEY = "product:%s";

    private static final String REDIS_Payment_KEY = "product:payment:%s";

    public static String getStockKey(Long productId) { return String.format(REDIS_STOCK_KEY, productId); }
    public static String getProductKey(Long productId) { return String.format(REDIS_PRODUCT_KEY, productId); }

    public static String getPaymentKey(Long paymentId) { return String.format(REDIS_Payment_KEY, paymentId); }
}
