package org.example.doctorcare.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * Service này quản lý việc thêm và kiểm tra token trong danh sách đen (blacklist) trên Redis.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    // Inject RedisTemplate đã được cấu hình ở RedisConfig
    private final RedisTemplate<String, String> redisTemplate;


    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";

    /**
     * Thêm một JWT token vào blacklist.
     * @param token Chuỗi token cần vô hiệu hóa.
     * @param expirationTimeInMillis Thời gian sống còn lại của token (tính bằng mili giây),
     *                               sẽ được dùng làm TTL cho key trên Redis.
     */
    public void blacklistToken(String token, long expirationTimeInMillis) {
        if (token == null || expirationTimeInMillis <= 0) {
            return;
        }
        String key = BLACKLIST_KEY_PREFIX + token;
        // Giá trị "blacklisted" chỉ là placeholder, sự tồn tại của key mới là quan trọng.
        try {
            redisTemplate.opsForValue().set(key, "blacklisted", expirationTimeInMillis, TimeUnit.MILLISECONDS);
            log.info("Token blacklisted successfully. Key: {}", key);
        } catch (Exception e) {
            log.error("Error while blacklisting token. Key: {}", key, e);
        }
    }

    /**
     * Kiểm tra xem một token có nằm trong blacklist hay không.
     * @param token Chuỗi token cần kiểm tra.
     * @return true nếu token có trong blacklist, ngược lại false.
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null) {
            return false;
        }
        String key = BLACKLIST_KEY_PREFIX + token;
        try {
            // hasKey là một thao tác rất nhanh trong Redis (O(1))
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error while checking token in blacklist. Key: {}", key, e);
            // Best Practice: Nếu Redis gặp lỗi, mặc định coi như token không bị blacklist để hệ thống không bị gián đoạn.
            // Cần có cơ chế theo dõi (monitoring) cho Redis để xử lý các lỗi này.
            return false;
        }
    }
}