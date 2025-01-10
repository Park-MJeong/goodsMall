package com.hanghae.paymentservice.service.scripts;
import com.hanghae.paymentservice.client.dto.OrderProductStock;
import com.hanghae.paymentservice.client.dto.OrderProductStockList;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hanghae.common.util.RedisKeyUtil.getStockKey;

@Service
@RequiredArgsConstructor
public class RedisStockService {
    private final RedissonClient redissonClient;
    public boolean decreaseStockWithLua(OrderProductStockList orderProductStockList){
        List<String> keys = new ArrayList<>();
        List<Object> quantities = new ArrayList<>();

        // orderProductStockList에서 각 productId와 quantity를 keys와 quantity 리스트에 추가
        for (OrderProductStock orderProductStock : orderProductStockList.getOrderProductStockList()) {
            String key = getStockKey(orderProductStock.getOrderProductId()); // Redis에서 검색에 사용할 키
            keys.add(key);
            quantities.add(orderProductStock.getQuantity());
        }
        String luaScript =
           """
           -- 첫 번째 루프: 모든 재고가 충분한지 확인
           for i=1, #KEYS do
                local stock = tonumber(redis.call('GET', KEYS[i]))
                if stock == nil or stock < tonumber(ARGV[i]) then
                    return -1
                end
            end
            -- 두 번째 루프: 모든 재고를 감소시킴
            for i=1, #KEYS do
                redis.call('DECRBY', KEYS[i], ARGV[i])
            end
        
            return 1
           """;
        // Redis에서 Lua 스크립트를 실행
        Object result = redissonClient.getScript().eval(
                RScript.Mode.READ_WRITE,  // 스크립트가 읽기/쓰기를 수행할 수 있도록 설정
                luaScript,                 // Lua 스크립트
                RScript.ReturnType.INTEGER, // 스크립트 실행 후 반환되는 타입
                Arrays.asList(keys.toArray()),     // Redis 키들
                quantities.toArray()               // 해당 키들에 대해 감소할 수량들
        );
        return result != null && (Long) result == 1;
    }
}
