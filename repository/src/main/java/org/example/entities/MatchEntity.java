package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MatchEntity {
    @Id
    private Long matchId;
    private Integer durationInSecs;
    private Instant startTime;
    private Boolean radiantWin;

    @OneToMany(mappedBy = "match")
    private List<PlayerMatchStats> playerStats = new ArrayList<>();
}
