package com.senhadeguess.backend.persistence.infrastructure.postgres;

import com.senhadeguess.backend.persistence.domain.PlayerRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<PlayerRecord, String> {
}