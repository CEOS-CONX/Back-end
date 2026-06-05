package com.conx.server.user.repository;

import com.conx.server.user.domain.email.EmailViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailViewLogRepository extends JpaRepository<EmailViewLog, Long> {
}