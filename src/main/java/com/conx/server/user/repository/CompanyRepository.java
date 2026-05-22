package com.conx.server.user.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.types.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByEmail(String email);

    Optional<Company> findByEmail(String email);

    Optional<Company> findByEmailAndStatus(String email, UserStatus status);

    boolean existsByEmailAndStatus(String email, UserStatus status);
}
