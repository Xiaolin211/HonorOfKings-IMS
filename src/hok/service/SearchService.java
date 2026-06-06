package hok.service;

import hok.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Search and query operations for the application.
 * Provides formatted lookup results for players, teams, heroes, and match history.
 * All methods are read-only and use the GameDataManager as their data source.
 */
public class SearchService {

    private GameDataManager dm;

    public SearchService(GameDataManager dm) {
        this.dm = dm;
    }

    // ==================== Player Lookup ====================

    /**
     * Looks up a player by ID or name and returns formatted details.
     * Returns null if not found.
     */
    public String lookupPlayer(String query) {
        Player player = dm.findPlayerById(query);
        if (player == null) {
            player = dm.findPlayerByName(query);
        }
        if (player == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(player.getDetailedInfo());
        sb.append("\n");

        // Show compatible equipment from owned heroes
        sb.append("Available Equipment (via owned heroes):\n");
        for (Hero h : player.getHeroes()) {
            List<Equipment> eqs = h.getCompatibleEquipment();
            if (!eqs.isEmpty()) {
                sb.append("  From ").append(h.getName()).append(": ");
                for (int i = 0; i < eqs.size(); i++) {
                    sb.append(eqs.get(i).getName());
                    if (i < eqs.size() - 1) sb.append(", ");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    // ==================== Team Overview ====================

    /**
     * Looks up a team by ID or name and returns formatted details.
     * Returns null if not found.
     */
    public String lookupTeam(String query) {
        Team team = dm.findTeamById(query);
        if (team == null) {
            team = dm.findTeamByName(query);
        }
        if (team == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(team.getDetailedInfo());
        sb.append("\n");

        // Show recent matches for this team
        List<MatchRecord> matches = dm.findMatchesByTeam(team.getId());
        int show = Math.min(5, matches.size());
        sb.append("Recent Matches (").append(show).append(" of ").append(matches.size()).append("):\n");
        for (int i = 0; i < show; i++) {
            sb.append("  ").append(matches.get(i).toString()).append("\n");
        }
        return sb.toString();
    }

    // ==================== Hero Details ====================

    /**
     * Looks up a hero by name and returns formatted details.
     * Returns null if not found.
     */
    public String lookupHero(String name) {
        Hero hero = dm.findHeroByName(name);
        if (hero == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(hero.getDetailedInfo());
        sb.append("\n");

        // Show players who own this hero
        List<Player> owners = dm.findPlayersOwningHero(hero.getId());
        sb.append("Owners (").append(owners.size()).append("):\n");
        for (Player p : owners) {
            sb.append("  ").append(p.getSummary()).append("\n");
        }
        return sb.toString();
    }

    // ==================== Match History ====================

    /**
     * Returns the most recent N match records involving a team or player.
     */
    public List<MatchRecord> getMatchHistory(String query, int limit) {
        // Try as team first
        Team team = dm.findTeamById(query);
        if (team == null) {
            team = dm.findTeamByName(query);
        }
        if (team != null) {
            List<MatchRecord> matches = dm.findMatchesByTeam(team.getId());
            return matches.subList(0, Math.min(limit, matches.size()));
        }

        // Try as player — find their team
        Player player = dm.findPlayerById(query);
        if (player == null) {
            player = dm.findPlayerByName(query);
        }
        if (player != null && player.getTeam() != null) {
            List<MatchRecord> matches = dm.findMatchesByTeam(player.getTeam().getId());
            return matches.subList(0, Math.min(limit, matches.size()));
        }

        return new ArrayList<>();
    }

    /**
     * Returns formatted match history as a string.
     */
    public String formatMatchHistory(String query, int limit) {
        List<MatchRecord> records = getMatchHistory(query, limit);
        if (records.isEmpty()) {
            return "No matches found for: " + query;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===== Match History (").append(records.size()).append(") =====\n");
        for (MatchRecord m : records) {
            sb.append(m.toString()).append("\n");
        }
        return sb.toString();
    }

    // ==================== List All (Summary Views) ====================

    /**
     * Returns a summary list of all players.
     */
    public String listAllPlayers() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== All Players (").append(dm.getAllPlayers().size()).append(") =====\n");
        for (Player p : dm.getAllPlayers()) {
            sb.append(p.getSummary()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a summary list of all teams.
     */
    public String listAllTeams() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== All Teams (").append(dm.getAllTeams().size()).append(") =====\n");
        for (Team t : dm.getAllTeams()) {
            sb.append(t.getSummary()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a summary list of all heroes.
     */
    public String listAllHeroes() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== All Heroes (").append(dm.getAllHeroes().size()).append(") =====\n");
        for (Hero h : dm.getAllHeroes()) {
            sb.append(h.getSummary()).append("\n");
        }
        return sb.toString();
    }
}
