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

    public MatchRecord(String id, Team teamA, Team teamB,
                       MatchResult result, String matchDate, String matchType) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.result = result;
        this.matchDate = matchDate;
        this.matchType = matchType;
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
        return id + " | " + matchDate + " | " + teamA.getName()
                + " vs " + teamB.getName() + " | " + result
                + " | " + matchType;
    }
}
