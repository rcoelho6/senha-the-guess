package com.senhadeguess.backend.persistence.infrastructure.postgres;

import com.senhadeguess.backend.persistence.domain.PersistedGameRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRecordRepository extends JpaRepository<PersistedGameRecord, Long> {
}