package com.conx.server.User.Repository;

import com.conx.server.User.Domain.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewRepository extends JpaRepository<Crew, Long> {
}
