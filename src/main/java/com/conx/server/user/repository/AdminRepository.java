package com.conx.server.user.repository;

import com.conx.server.user.domain.admin.Admin;
import com.conx.server.user.domain.crew.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
}
