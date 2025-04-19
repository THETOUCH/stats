package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.example.entities.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class DataStorage {
    private final HeroService heroService;
    private final HeroRepository heroRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private static final String steamApiKey = "D72F21FB6232C2345387CAE612EB2DE8";
    private static final Gson GSON = new Gson();
    private static final String URI = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/v1";
    private static final String SEQ_NUMB_URI = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistoryBySequenceNum/v1/?key=" + steamApiKey + "&start_at_match_seq_num=";

    DataStorage(HeroService heroService,
                HeroRepository heroRepository,
                MatchRepository matchRepository,
                PlayerRepository playerRepository,
                PlayerMatchStatsRepository playerMatchStatsRepository) {
        this.heroService = heroService;
        this.heroRepository = heroRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.playerMatchStatsRepository = playerMatchStatsRepository;
    }

    public List<PlayerMatchStats> getAll(String id) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        String string = URI + "?key=" + steamApiKey + "&account_id=" + id;

        JsonObject matchJsonMatchIdAndSeqNum = doGetRequest(string)
                .getAsJsonObject();

        Map<String, String> matchIdAndSeqNum = getMatchesInfo(matchJsonMatchIdAndSeqNum);

        if (matchIdAndSeqNum.isEmpty()) {
            return Collections.emptyList();
        }

        List<PlayerMatchStats> playerMatchStatsList = getDetailedInfoByAllAsync(matchIdAndSeqNum, id);

        System.out.println(playerMatchStatsList);

        return playerMatchStatsList;
    }

    private List<PlayerMatchStats> getDetailedInfoByAllAsync(Map<String, String> map, String id) throws IOException, URISyntaxException, ExecutionException, InterruptedException, ExecutionException {
        List<CompletableFuture<PlayerMatchStats>> futures = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            CompletableFuture<PlayerMatchStats> future = CompletableFuture.supplyAsync(() -> {
                try {
                    String UriPerMatch = SEQ_NUMB_URI + entry.getValue() + "&matches_requested=1";
                    JsonObject jsonObject = doGetRequest(UriPerMatch)
                            .getAsJsonObject()
                            .getAsJsonObject("result")
                            .getAsJsonArray("matches")
                            .get(0)
                            .getAsJsonObject();


                    MatchEntity matchEntity = new MatchEntity();
                    matchEntity.setMatchId(Long.parseLong(entry.getKey()));
                    matchEntity.setDurationInSecs(jsonObject.get("duration").getAsInt());
                    matchEntity.setStartTime(Instant.ofEpochSecond(jsonObject.get("start_time").getAsLong()));
                    matchEntity.setRadiantWin(jsonObject.get("radiant_win").getAsBoolean());

                    JsonArray jsonArray = jsonObject.getAsJsonArray("players");

                    for (JsonElement jsonElement : jsonArray) {
                        JsonObject player = jsonElement.getAsJsonObject();
                        String accountId = player.get("account_id").getAsString();

                        if (accountId.equals(id)) {
                            PlayerMatchStats stats = new PlayerMatchStats();
                            stats.setMatch(matchEntity);

                            PlayerEntity playerEntity = new PlayerEntity();
                            playerEntity.setAccountId(Long.parseLong(accountId));
                            stats.setPlayer(playerEntity);

//                            HeroEntity heroEntity = new HeroEntity();
//                            heroEntity.setId(player.get("hero_id").getAsInt());
//                            stats.setHero(heroEntity);
                            int heroId = player.get("hero_id").getAsInt();
//                            Optional<HeroEntity> heroEntity = heroRepository.findById(heroId);
//                            stats.setHero(heroEntity.get());
                            HeroEntity hero = heroService.getHeroesMap().get(heroId);
                            stats.setHero(hero);

                            stats.setKills(player.get("kills").getAsInt());
                            stats.setDeaths(player.get("deaths").getAsInt());
                            stats.setAssists(player.get("assists").getAsInt());
                            stats.setPlayerSlot(player.get("player_slot").getAsInt());

                            int playerTeam = player.get("team_number").getAsInt();
                            boolean isRadiantWin = matchEntity.getRadiantWin();
                            stats.setWin((isRadiantWin && playerTeam == 0) || (!isRadiantWin && playerTeam == 1));

                            return stats;
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error processing match " + entry.getKey(), e);
                }
                return null;
            });

            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        CompletableFuture<List<PlayerMatchStats>> allStatsFuture = allFutures.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        return allStatsFuture.get();
    }

    private Map<String, String> getMatchesInfo(JsonObject json) {
        JsonObject result = json.getAsJsonObject("result");

        if (result == null || !result.has("matches")) {
            return Collections.emptyMap();
        }

        JsonArray jsonArray = result.getAsJsonArray("matches");

        if (jsonArray == null || jsonArray.size() == 0) {
            return Collections.emptyMap();
        }

        Map<String, String> matchesMap = new LinkedHashMap<>();

        for (JsonElement jsonElement : jsonArray) {
            JsonObject match = jsonElement.getAsJsonObject();
            String matchId = match.get("match_id").getAsString();
            String matchSeqNum = match.get("match_seq_num").getAsString();
            matchesMap.put(matchId, matchSeqNum);
        }
        return matchesMap;
    }

    static JsonElement doGetRequest(String url) throws IOException, URISyntaxException {
        final HttpURLConnection connection = (HttpURLConnection) new URI(url)
                .toURL()
                .openConnection();
        connection.setRequestMethod("GET");

        System.out.println(connection.getResponseCode());
        System.out.println(connection.getResponseMessage());

        final Scanner scanner = new Scanner(connection.getInputStream());

        String response = "";
        while (scanner.hasNext()) {
            response += scanner.nextLine();
        }
        return GSON.fromJson(response.toString(), JsonElement.class);
    }
}
