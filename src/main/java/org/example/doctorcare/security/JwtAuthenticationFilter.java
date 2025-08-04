package org.example.doctorcare.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// ...
/**
 * Filter này chịu trách nhiệm chặn các request đến,
 * kiểm tra sự tồn tại và tính hợp lệ của JWT token trong header 'Authorization'.
 * Nếu token hợp lệ, nó sẽ thiết lập thông tin xác thực vào SecurityContext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Lấy JWT token từ request
            String jwt = getJwtFromRequest(request);

            // 2. Kiểm tra token có hợp lệ không và chưa bị blacklist
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                // 2.1. Kiểm tra token có trong blacklist (đã bị logout) không
                if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                    log.warn("Attempted to use a blacklisted token from IP: {}", request.getRemoteAddr());
                    // Không cần gửi lỗi ở đây, vì nếu không set authentication,
                    // các bước sau của Spring Security sẽ tự động từ chối.
                    // Tuy nhiên, việc gửi lỗi rõ ràng cũng là một lựa chọn.
                    // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token đã bị thu hồi.");
                    // return;
                } else {
                    // 3. Lấy username (email) từ token
                    String username = jwtTokenProvider.getUsernameFromJWT(jwt);

                    // 4. Tải thông tin UserDetails từ database
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    // 5. Tạo đối tượng Authentication
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,       // Principal: là đối tượng UserDetails (hoặc CustomUserDetails)
                            null,              // Credentials: null vì chúng ta dùng JWT, không dùng password
                            userDetails.getAuthorities() // Authorities: danh sách quyền
                    );

                    // 6. Gắn thêm các chi tiết của request (như IP, session ID) vào đối tượng Authentication
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. Quan trọng nhất: Thiết lập đối tượng Authentication vào SecurityContext
                    // Từ đây, Spring Security sẽ biết rằng request này đã được xác thực.
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Set Authentication to security context for user '{}'", username);
                }
            }
        } catch (Exception ex) {
            // Ghi log lại lỗi nhưng không ném ra ngoài, để request tiếp tục được xử lý
            // (và sẽ bị từ chối ở các tầng sau nếu không có authentication)
            log.error("Could not set user authentication in security context", ex);
        }

        // 8. Chuyển request và response cho filter tiếp theo trong chuỗi filter
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method để trích xuất JWT token từ header 'Authorization'.
     * @param request HttpServletRequest
     * @return Chuỗi JWT token hoặc null nếu không tìm thấy.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}