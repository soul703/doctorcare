package org.example.doctorcare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Lớp này chịu trách nhiệm tạo và xác thực JWT tokens.
 * Nó sử dụng thư viện jjwt để tạo và phân tích JWT.
 * Các token được sử dụng để xác thực người dùng trong hệ thống.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private SecretKey key;
    @Value("${app.jwt.access-token-expiration-ms}")
    private long jwtAccessTokenExpirationMs;
    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long jwtRefreshTokenExpirationMs;

    @PostConstruct
    public void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // tạo key an toàn, đủ độ dài
    } // Mặc định, có thể lấy từ application.properties

    // Tạo Access Token
    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtAccessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }


    // Tạo Refresh Token
    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Subject là email
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Các phương thức validateToken, getUsernameFromJWT...
    // ...


    /**
     * Lấy Claims từ token. Claims là phần payload của JWT.
     *
     * @param token JWT token
     * @return Claims object
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    /**
     * Lấy ngày hết hạn từ token.
     *
     * @param token JWT token
     * @return Date object là ngày hết hạn
     */
    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    /**
     * Tính toán thời gian sống còn lại của token theo mili giây.
     * Rất quan trọng để set TTL cho key trên Redis.
     *
     * @param token JWT token
     * @return Thời gian còn lại (ms)
     */
    public long getRemainingExpirationInMillis(String token) {
        try {
            Date expirationDate = getExpirationDateFromToken(token);
            long remaining = expirationDate.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining); // Trả về 0 nếu đã hết hạn
        } catch (Exception e) {
            // Token không hợp lệ hoặc đã hết hạn
            return 0;
        }
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject(); // Subject là email
        } catch (Exception e) {
            log.error("Could not extract username from token", e);
            return null; // Hoặc ném ra một exception tùy ý
        }
    }

    // Phương thức mới
    public String generateAccessTokenFromUserDetails(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtAccessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("roles", userDetails.getAuthorities()) // Best practice: Thêm roles vào claims
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // Kiểm tra token có hợp lệ không
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false; // Token không hợp lệ
        }
    }
}