package com.conx.server.User.Repository;

import com.conx.server.User.Domain.User;
import com.conx.server.User.Domain.Enum.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndStatusIn(String email, Collection<UserStatus> statuses);
}
