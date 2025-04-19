package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlayerMatchStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "match_id")
    private MatchEntity match;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private PlayerEntity player;

    @ManyToOne
    @JoinColumn(name = "hero_id")
    private HeroEntity hero;

    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer playerSlot;
    private Boolean win;
}
