package org.example.doctorcare.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String userName);
    Optional<User> findByEmail(String email);

    Page<User> findByRoleId(Integer roleId, Pageable pageable);
    boolean existsByEmail(String email);
    @Override
    @EntityGraph(attributePaths = {"role"})
    Page<User> findAll(Pageable pageable);
}
