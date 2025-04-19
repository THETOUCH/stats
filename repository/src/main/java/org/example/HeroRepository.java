package org.example;

import org.example.entities.HeroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeroRepository extends JpaRepository<HeroEntity, Integer> {
}
