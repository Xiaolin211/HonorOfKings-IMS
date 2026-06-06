package hok.model;

import hok.enums.MatchResult;

/**
 * Represents a match record between two teams.
 * Demonstrates association: references two Team objects.
 * The result is stored from teamA's perspective.
 */
public class MatchRecord {
    private String id;
    private Team teamA;
    private Team teamB;
    private MatchResult result;
    private String matchDate;
    private String matchType;
    private String heroPicksA;  // hero names used by teamA, e.g. "Li Bai, Luban No.7, Cai Wenji"
    private String heroPicksB;  // hero names used by teamB

    public MatchRecord(String id, Team teamA, Team teamB,
                       MatchResult result, String matchDate, String matchType) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.result = result;
        this.matchDate = matchDate;
        this.matchType = matchType;
        this.heroPicksA = "";
        this.heroPicksB = "";
    }

    public MatchRecord(String id, Team teamA, Team teamB,
                       MatchResult result, String matchDate, String matchType,
                       String heroPicksA, String heroPicksB) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.result = result;
        this.matchDate = matchDate;
        this.matchType = matchType;
        this.heroPicksA = heroPicksA;
        this.heroPicksB = heroPicksB;
    }

    // === Getters ===

    public String getId() {
        return id;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public MatchResult getResult() {
        return result;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public String getMatchType() {
        return matchType;
    }

    public String getHeroPicksA() {
        return heroPicksA;
    }

    public String getHeroPicksB() {
        return heroPicksB;
    }

    // === Setters ===

    public void setHeroPicksA(String heroPicksA) {
        this.heroPicksA = heroPicksA;
    }

    public void setHeroPicksB(String heroPicksB) {
        this.heroPicksB = heroPicksB;
    }

    // === Utility ===

    /**
     * Returns the winning team, or null if the match was a draw.
     */
    public Team getWinner() {
        if (result == MatchResult.WIN) {
            return teamA;
        } else if (result == MatchResult.LOSE) {
            return teamB;
        }
        return null; // DRAW
    }

    /**
     * Returns the losing team, or null if the match was a draw.
     */
    public Team getLoser() {
        if (result == MatchResult.WIN) {
            return teamB;
        } else if (result == MatchResult.LOSE) {
            return teamA;
        }
        return null; // DRAW
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(" | ").append(matchDate).append(" | ")
          .append(teamA.getName()).append(" vs ").append(teamB.getName())
          .append(" | ").append(result).append(" | ").append(matchType);
        if (!heroPicksA.isEmpty()) {
            sb.append(" | ").append(teamA.getName()).append(" picks: ").append(heroPicksA);
        }
        if (!heroPicksB.isEmpty()) {
            sb.append(" | ").append(teamB.getName()).append(" picks: ").append(heroPicksB);
        }
        return sb.toString();
    }
}
