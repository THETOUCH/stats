package org.example;

import org.example.dto.ApiResponseDto;
import org.example.entities.HeroEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@Service
public class SteamApiService {


    private final WebClient webClient;
    private final HeroRepository heroRepository;

    public SteamApiService(WebClient.Builder webClientBuilder,
                           HeroRepository heroRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.steampowered.com").build();
        this.heroRepository = heroRepository;
    }

    @Transactional
    public Mono<Void> fetchAndUpdateHeroes() {
        return webClient.get()
                .uri("/IEconDOTA2_570/GetHeroes/v1/?key=YOUR_API_KEY")
                .retrieve()
                .bodyToMono(ApiResponseDto.class)
                .flatMapIterable(apiResponse -> apiResponse.getResult().getHeroes())
                .map(heroDto -> new HeroEntity(
                        heroDto.getId(),
                        heroDto.getName()
                ))
                .collectList()
                .doOnNext(heroEntities -> {
                    heroRepository.deleteAll(); // Очищаем старые данные
                    heroRepository.saveAll(heroEntities);
                })
                .then();
    }

    // Метод для получения всех героев из БД
    public List<HeroEntity> getAllHeroes() {
        return heroRepository.findAll();
    }

    // Метод для поиска героя по ID
    public Optional<HeroEntity> findHeroById(Integer id) {
        return heroRepository.findById(id);
    }
}
