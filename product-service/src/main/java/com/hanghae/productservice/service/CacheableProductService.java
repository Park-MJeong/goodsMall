package com.hanghae.productservice.service;


import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CacheableProductService {
    private final Map<Long, CachedProduct> staticCache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRATION_TIME = 60 * 1000;
    private final ProductRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 공통 :제품정보 제공 (상태 예외처리 없이 전달)
     */
    @Transactional(readOnly = true)
    public Product getProductAll(Long id){
        CachedProduct cachedProduct = staticCache.get(id);
        if (cachedProduct != null && (System.currentTimeMillis() - cachedProduct.getTimestamp()) < CACHE_EXPIRATION_TIME) {
            return cachedProduct.getProduct();
        }
        Product product = repository.findProductById(id).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        staticCache.put(id,new CachedProduct(product, System.currentTimeMillis()));
        return  product;
    }

    private static class CachedProduct {
        private final Product product;
        private final long timestamp;

        public CachedProduct(Product product, long timestamp) {
            this.product = product;
            this.timestamp = timestamp;
        }

        public Product getProduct() {
            return product;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

//    public Product getProductAll(Long id){
//
//        String key = "product:" + id;
//        Map<Object, Object> product = redisTemplate.opsForHash().entries(key);
//        if (product.isEmpty()) {
//            // 캐시에서 값이 없으면 데이터베이스에서 조회
//            Product newProduct = repository.findProductById(id).orElseThrow(
//                    ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
//            cacheProductAsHash(newProduct);  // 조회한 데이터를 캐시
//            return newProduct;
//        }
//        return new Product(
//                Long.valueOf((Integer)product.get("id")),
//                (String) product.get("productName"),
//                (String) product.get("description"),
//                BigDecimal.valueOf((double) product.get("productPrice")),
//                LocalDateTime.parse((String) product.get("openDate"), DATE_TIME_FORMATTER),
//                (Integer) product.get("quantity"),
//                (String) product.get("status")
//        );
//    }
//
//    private void cacheProductAsHash(Product product) {
//        String key = "product:" + product.getId();  // 제품 ID를 key로 사용
//        Map<String, Object> productMap = new HashMap<>();
//        productMap.put("id", product.getId());
//        productMap.put("productName", product.getProductName());
//        productMap.put("description", product.getDescription());
//        productMap.put("productPrice", product.getProductPrice());
//        productMap.put("openDate", product.getOpenDate().format(DATE_TIME_FORMATTER));
//        productMap.put("quantity", product.getQuantity());
//        productMap.put("status", product.getStatus());
//        redisTemplate.opsForHash().putAll(key, productMap);  // Hash로 저장
//    }



}
