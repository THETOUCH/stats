package org.example.dto;

import org.example.entities.PlayerMatchStats;

public class MatchStatsDto {
    private Long matchId;
    private String heroName;
    private Integer heroId;
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private String duration;
    private Boolean win;

    public MatchStatsDto(PlayerMatchStats stats) {
        this.matchId = stats.getMatch().getMatchId();
        this.heroName = stats.getHero().getLocalizedName();
        this.heroId = stats.getHero().getId();
        this.kills = stats.getKills();
        this.deaths = stats.getDeaths();
        this.assists = stats.getAssists();
        this.duration = formatDuration(stats.getMatch().getDurationInSecs());
        this.win = stats.getWin();
    }

    private String formatDuration(Integer seconds) {
        if (seconds == null) return "0:00";
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    public Long getMatchId() { return matchId; }
    public String getHeroName() { return heroName; }
    public Integer getHeroId() { return heroId; }
    public Integer getKills() { return kills; }
    public Integer getDeaths() { return deaths; }
    public Integer getAssists() { return assists; }
    public String getDuration() { return duration; }
    public Boolean getWin() { return win; }
    public boolean isWin() { return win != null && win; }
}
