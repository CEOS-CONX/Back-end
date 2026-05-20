package com.conx.server.user.repository;

import com.conx.server.user.domain.User;
import com.conx.server.user.domain.types.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndStatusIn(String email, Collection<UserStatus> statuses);
}
