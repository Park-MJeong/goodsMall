package com.hanghae.productservice.service;


import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import com.hanghae.productservice.dto.CachedProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheableProductService {
    private static final long CACHE_EXPIRATION_TIME =10 * 60 * 1000;
    private final ProductRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PersistenceContext
    private EntityManager entityManager;
    /**
     * 공통 :제품정보 제공 (상태 예외처리 없이 전달)
     */
    @Transactional(readOnly = true)
    @Cacheable(key = "#id",cacheNames = "products")
    public Product getProductAll(Long id){
        return  repository.findProductById(id).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

//
//    @Transactional
//    public void updateProduct(ProductIdAndQuantityDto productIdAndQuantityDto){
//        Long productId = productIdAndQuantityDto.getProductId();
//        repository.updateQuantityAndStatus(productId,productIdAndQuantityDto.getQuantity());
//        Product product = repository.findProductById(productIdAndQuantityDto.getProductId()).orElseThrow(
//                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
//        staticCache.put(productId,new CachedProduct(product, System.currentTimeMillis()));
//    }

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

    /**
     * 캐시 강제 갱신 (optional)
     */
    @CacheEvict(key = "#id", cacheNames = "products")
//    @Transactional
    public void refreshProductCache(Long id) {
        repository.findProductById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        log.info("Cache refreshed for product id: {}", id);
    }

}
