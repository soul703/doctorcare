package org.example.doctorcare.cache;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Lấy giá trị từ cache theo key.
     * @param key Khóa để truy xuất giá trị từ cache.
     * @return Giá trị tương ứng với key, hoặc null nếu không tìm thấy hoặc có lỗi.
     */

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("CACHE: Error getting cache for key '{}'. Error: {}", key, e.getMessage());
            return null;
        }
    }
    /**
     * Lưu giá trị vào cache với TTL (Time To Live).
     * @param key Khóa để lưu giá trị.
     * @param value Giá trị cần lưu.
     * @param ttl Thời gian sống của cache.
     * @param timeUnit Đơn vị thời gian cho TTL (ví dụ: giây, phút, giờ).
     */

    public void set(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
            log.info("CACHE: Data cached successfully for key '{}' with TTL {} {}.", key, ttl, timeUnit.name());
        } catch (Exception e) {
            log.error("CACHE: Error setting cache for key '{}'. Error: {}", key, e.getMessage());
        }
    }

    public void evict(String key) {
        try {
            redisTemplate.delete(key);
            log.info("CACHE: Evicted key '{}'.", key);
        } catch (Exception e) {
            log.error("CACHE: Error evicting cache for key '{}'. Error: {}", key, e.getMessage());
        }
    }
}
