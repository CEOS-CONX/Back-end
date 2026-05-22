package com.conx.server.user.repository;

import com.conx.server.user.domain.User;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    boolean existsByEmail(String email);

    Optional<Crew> findByEmail(String email);

    User findByEmailAndStatus(String email, UserStatus status);

    boolean existsByEmailAndStatus(String email, UserStatus status);
}
