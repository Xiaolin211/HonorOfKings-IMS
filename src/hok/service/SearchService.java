package hok.service;

import hok.model.Equipment;
import hok.model.Hero;
import hok.model.MatchRecord;
import hok.model.Player;
import hok.model.Team;
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

        // Show total matches for this team
        List<MatchRecord> matches = dm.findMatchesByTeam(team.getId());
        sb.append("Total Matches: ").append(matches.size()).append("\n\n");

        // Show recent matches for this team
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
     * Returns formatted match history as a string, including win/loss record
     * and hero pick rate statistics.
     */
    public String formatMatchHistory(String query, int limit) {
        // Resolve query to a team (for W/L perspective + hero pick rate)
        Team queriedTeam = dm.findTeamById(query);
        if (queriedTeam == null) queriedTeam = dm.findTeamByName(query);
        if (queriedTeam == null) {
            Player p = dm.findPlayerById(query);
            if (p == null) p = dm.findPlayerByName(query);
            if (p != null) queriedTeam = p.getTeam();
        }

        List<MatchRecord> records;
        if (queriedTeam != null) {
            records = dm.findMatchesByTeam(queriedTeam.getId());
        } else {
            records = new ArrayList<>();
        }

        if (records.isEmpty()) {
            return "No matches found for: " + query;
        }

        List<MatchRecord> shown = records.subList(0, Math.min(limit, records.size()));

        StringBuilder sb = new StringBuilder();
        sb.append("===== Match History (").append(shown.size())
          .append(" of ").append(records.size()).append(" total) =====\n");

        // --- Win/Loss Record Summary ---
        if (queriedTeam != null) {
            int wins = 0, losses = 0, draws = 0;
            for (MatchRecord m : records) {
                if (m.getTeamA().equals(queriedTeam)) {
                    if (m.getResult() == hok.enums.MatchResult.WIN) wins++;
                    else if (m.getResult() == hok.enums.MatchResult.LOSE) losses++;
                    else draws++;
                } else if (m.getTeamB().equals(queriedTeam)) {
                    if (m.getResult() == hok.enums.MatchResult.LOSE) wins++;
                    else if (m.getResult() == hok.enums.MatchResult.WIN) losses++;
                    else draws++;
                }
            }
            sb.append("Win/Loss Record: ").append(wins).append("W / ")
              .append(losses).append("L / ").append(draws).append("D")
              .append("  (Win Rate: ").append(String.format("%.1f%%",
                      records.isEmpty() ? 0 : (double) wins / records.size() * 100))
              .append(")\n\n");
        }

        // --- Match List ---
        for (MatchRecord m : shown) {
            sb.append(m.toString()).append("\n");
        }

        // --- Hero Pick Rate Statistics ---
        if (!records.isEmpty()) {
            sb.append("\n--- Hero Pick Rate (all ").append(records.size())
              .append(" matches) ---\n");
            java.util.Map<String, Integer> heroCount = new java.util.LinkedHashMap<>();
            int totalPickSlots = 0;
            for (MatchRecord m : records) {
                String[] picksA = m.getHeroPicksA().isEmpty() ? new String[0]
                        : m.getHeroPicksA().split(",\\s*");
                String[] picksB = m.getHeroPicksB().isEmpty() ? new String[0]
                        : m.getHeroPicksB().split(",\\s*");
                for (String name : picksA) {
                    heroCount.put(name, heroCount.getOrDefault(name, 0) + 1);
                    totalPickSlots++;
                }
                for (String name : picksB) {
                    heroCount.put(name, heroCount.getOrDefault(name, 0) + 1);
                    totalPickSlots++;
                }
            }
            // Sort by count descending
            java.util.List<java.util.Map.Entry<String, Integer>> sorted = new java.util.ArrayList<>(heroCount.entrySet());
            sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
            sb.append(String.format("%-18s %6s %8s\n", "Hero", "Picks", "Pick Rate"));
            sb.append("-----------------------------------\n");
            for (java.util.Map.Entry<String, Integer> e : sorted) {
                double rate = totalPickSlots > 0 ? (double) e.getValue() / totalPickSlots * 100 : 0;
                sb.append(String.format("%-18s %6d %7.1f%%\n",
                        e.getKey(), e.getValue(), rate));
            }
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
