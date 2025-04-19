package org.example;

import org.example.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {
    Optional<MatchEntity> findByMatchId(long l);
}
