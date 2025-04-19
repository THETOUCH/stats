package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HeroEntity {
    @Id
    private Integer id;
    private String name;
    private String localizedName;

    @ManyToMany(mappedBy = "heroes")
    private Set<PlayerEntity> players = new HashSet<>();

    public HeroEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.localizedName = name.replace("npc_dota_hero_", "");
    }
}
