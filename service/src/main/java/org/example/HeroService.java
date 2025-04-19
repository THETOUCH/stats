package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import org.example.entities.HeroEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HeroService {
    private final HeroRepository heroRepository;
    private final String URI = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v1/?key=D72F21FB6232C2345387CAE612EB2DE8";

    private Map<Integer, HeroEntity> heroesMap;

    HeroService(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    @PostConstruct
    public void init() throws IOException, URISyntaxException {
        loadHeroes();
    }

    private void loadHeroes() throws IOException, URISyntaxException {
        heroesMap = new LinkedHashMap<>();
        JsonObject jsonObject = DataStorage.doGetRequest(URI).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonObject("result")
                .getAsJsonArray("heroes");

        Map<String, String> heroes = new LinkedHashMap<>();

        for (JsonElement json : jsonArray) {
            JsonObject jsonObject1 = json.getAsJsonObject();
            String name = jsonObject1.get("name").getAsString();
            String id = jsonObject1.get("id").getAsString();
            heroes.put(id, name);
        }

        for (Map.Entry<String, String> hero : heroes.entrySet()) {
            HeroEntity heroEntity = new HeroEntity(Integer.parseInt(hero.getKey()), hero.getValue());


            System.out.println(heroEntity);


            heroRepository.save(heroEntity);

            heroesMap.put(heroEntity.getId(), heroEntity);
        }
    }
    public Map<Integer, HeroEntity> getHeroesMap() {
        return heroesMap;
    }
}
