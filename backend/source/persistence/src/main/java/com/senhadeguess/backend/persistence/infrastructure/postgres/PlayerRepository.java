package com.senhadeguess.backend.persistence.infrastructure.postgres;

import com.senhadeguess.backend.persistence.domain.PlayerRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<PlayerRecord, String> {
    Optional<PlayerRecord> findByEmail(String email);
}
