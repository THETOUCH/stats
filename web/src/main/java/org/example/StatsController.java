package org.example;

import org.example.dto.MatchStatsDto;
import org.example.entities.PlayerMatchStats;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class StatsController {

    private final DataStorage dataStorage;

    public StatsController(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/stats")
    public String getStats(@RequestParam("id") String id, Model model) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        List<PlayerMatchStats> stats = dataStorage.getAll(id);

        if (stats.isEmpty()) {
            model.addAttribute("error", "Профиль скрыт или не существует.");
        } else {
            List<MatchStatsDto> dtoList = stats.stream()
                    .map(MatchStatsDto::new)
                    .collect(Collectors.toList());
            model.addAttribute("stats", dtoList);
        }

        model.addAttribute("accountId", id);
        return "stats";
    }
}
