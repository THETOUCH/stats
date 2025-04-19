package org.example;

import org.example.entities.PlayerMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStats, Long> {
    Optional<PlayerMatchStats> findByPlayerAccountIdAndMatchMatchId(long l, long l1);
}
