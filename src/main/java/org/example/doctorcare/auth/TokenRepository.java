package org.example.doctorcare.auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    /**
     * Xóa tất cả các refresh token của một user.
     * Sử dụng @Modifying và @Query để tối ưu hiệu suất,
     * tránh việc phải load entity vào Persistence Context trước khi xóa.
     * @param userId ID của người dùng cần xóa token
     */
    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.id = :userId")
    void deleteByUserId(Long userId);
}