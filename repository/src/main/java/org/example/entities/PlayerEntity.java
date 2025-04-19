package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlayerEntity {
    @Id
    private Long accountId;
    private String nickname;

    @ManyToMany
    @JoinTable(
            name = "player_hero",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "hero_id")
    )
    private Set<HeroEntity> heroes = new HashSet<>();

    @OneToMany(mappedBy = "player")
    private List<PlayerMatchStats> matchStats = new ArrayList<>();

}
