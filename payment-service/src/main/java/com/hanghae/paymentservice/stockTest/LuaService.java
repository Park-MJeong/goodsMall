package com.hanghae.paymentservice.stockTest;

import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.hanghae.common.util.RedisKeyUtil.getStockKey;

@Service
@Slf4j
@RequiredArgsConstructor
public class LuaService {
    private final RedissonClient redissonClient;
    private final RedisTemplate<String,Object> redisTemplate;

    public void initPayment(OrderEvent orderEvent) {
            try {
                for (OrderRequestDto dto : orderEvent.getOrderRequestDtoList()) {
                    String stockKey = getStockKey(dto.getProductId());
                    boolean success = decreaseStock(stockKey, dto.getQuantity());
                    if (!success) {
                        throw new BusinessException(ErrorCode.FAILED_QUANTITY_PAYMENT);
                    }
                }
            } catch (Exception e) {
                // 롤백 처리
                for (OrderRequestDto dto : orderEvent.getOrderRequestDtoList()) {
                    rollbackStock("stock:" + dto.getProductId(), dto.getQuantity());
                }
                throw e;
            }

    }


    // Lua 스크립트를 사용한 재고 감소
    public boolean decreaseStock(String stockKey, int quantity) {
        RFuture<Long> resultFuture = redissonClient.getScript().evalAsync(
                RScript.Mode.READ_WRITE,
                """
                    local stockKey = KEYS[1]
                    local quantity = tonumber(ARGV[1])
                    local currentStock = redis.call('GET', stockKey)
                    if not currentStock or tonumber(currentStock) < quantity then
                        return -1
                    end
                    return redis.call('DECRBY', stockKey, quantity)
                """,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(stockKey),
                quantity
        );

        try {
            log.info("감소 전 재고: {}",redisTemplate.opsForValue().get(stockKey));
            Long result = resultFuture.toCompletableFuture().get(1, TimeUnit.SECONDS);
            if (result == -1) {
                return false;
            }
            log.info("감소 후 재고: {}",redisTemplate.opsForValue().get(stockKey));

            return true;
        } catch (Exception e) {
            log.error("Redis Lua 스크립트 실행 중 오류 발생", e);
            return false;
        }
    }

    // 롤백
    private void rollbackStock(String stockKey, int quantity) {
        redisTemplate.opsForValue().increment(stockKey, quantity);
    }


//    private boolean checkStock(List<OrderRequestDto> orderRequestDtos) {
//        for (OrderRequestDto orderRequestDto : orderRequestDtos) {
//            Long productId = orderRequestDto.getProductId();
//            int quantity = orderRequestDto.getQuantity();
//            String key = getStockKey(productId);
//            // Lua 스크립트 실행
//            RFuture<Long> resultFuture = redissonClient.getScript().evalAsync(
//                    RScript.Mode.READ_WRITE,
//                    """
//                                  local key = KEYS[1]
//                                   local quantity = tonumber(ARGV[1])
//                                   local currentStock = redis.call('GET', key)
//                                   if not currentStock or tonumber(currentStock) < quantity then
//                                       return -1
//                                   end
//                                   return redis.call('DECRBY', stockKey, quantity)
//                            """
//                    ,
//                    RScript.ReturnType.INTEGER,
//                    Collections.singletonList(key),
//                    quantity);
//            try {
//                Long result = resultFuture.toCompletableFuture().get(1, TimeUnit.SECONDS);
//                return result != -1;
//            } catch (Exception e) {
//                log.error("Redis Lua 스크립트 실행 중 오류 발생", e);
//                return false;
//            }
//        }
//
//        return true;
//    }
}
