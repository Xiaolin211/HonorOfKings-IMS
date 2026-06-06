package hok.service;

import hok.model.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Provides ranking and leaderboard functionality.
 * Equipment ranking uses a weighted formula; player leaderboards support
 * multiple sort criteria with tie-breaking.
 */
public class RankingService {

    private GameDataManager dm;

    public RankingService(GameDataManager dm) {
        this.dm = dm;
    }

    // ==================== Equipment Ranking ====================

    /**
     * Ranks equipment by a weighted composite score.
     * Formula: score = usageCount * 0.4 + rating * 0.4 + compatibleHeroCount * 0.2
     *
     * compatibleHeroCount = number of heroes that list this equipment as compatible.
     */
    public List<Equipment> rankEquipment() {
        List<Equipment> all = dm.getAllEquipment();

        // Sort by composite score descending
        all.sort(new Comparator<Equipment>() {
            @Override
            public int compare(Equipment a, Equipment b) {
                double scoreA = computeEquipmentScore(a);
                double scoreB = computeEquipmentScore(b);
                return Double.compare(scoreB, scoreA); // descending
            }
        });

        return all;
    }

    /**
     * Returns formatted equipment ranking as a string.
     */
    public String formatEquipmentRanking() {
        List<Equipment> ranked = rankEquipment();
        StringBuilder sb = new StringBuilder();
        sb.append("===== Equipment Ranking =====\n");
        sb.append(String.format("%-4s %-20s %-8s %-12s %-6s %s\n",
                "Rank", "Name", "Score", "Usage Count", "Rating", "Heroes"));
        sb.append("--------------------------------------------------------------\n");

        for (int i = 0; i < ranked.size(); i++) {
            Equipment e = ranked.get(i);
            double score = computeEquipmentScore(e);
            int heroCount = countCompatibleHeroes(e);
            sb.append(String.format("%-4d %-20s %-8.1f %-12d %-6.1f %d\n",
                    i + 1, truncate(e.getName(), 20),
                    score, e.getUsageCount(), e.getRating(), heroCount));
        }
        return sb.toString();
    }

    private double computeEquipmentScore(Equipment e) {
        int heroCount = countCompatibleHeroes(e);
        return e.getUsageCount() * 0.4 + e.getRating() * 0.4 + heroCount * 0.2;
    }

    private int countCompatibleHeroes(Equipment eq) {
        int count = 0;
        for (Hero h : dm.getAllHeroes()) {
            if (h.getCompatibleEquipment().contains(eq)) {
                count++;
            }
        }
        return count;
    }

    // ==================== Player Leaderboard ====================

    /**
     * Returns top N players sorted by win rate (descending).
     * Tie-breaking: level descending, then name ascending.
     */
    public List<Player> topByWinRate(int n) {
        return topN(n, (a, b) -> {
            int cmp = Double.compare(b.getWinRate(), a.getWinRate());
            if (cmp != 0) return cmp;
            cmp = Integer.compare(b.getLevel(), a.getLevel());
            if (cmp != 0) return cmp;
            return a.getName().compareTo(b.getName());
        });
    }

    /**
     * Returns top N players sorted by level (descending).
     * Tie-breaking: win rate descending, then name ascending.
     */
    public List<Player> topByLevel(int n) {
        return topN(n, (a, b) -> {
            int cmp = Integer.compare(b.getLevel(), a.getLevel());
            if (cmp != 0) return cmp;
            cmp = Double.compare(b.getWinRate(), a.getWinRate());
            if (cmp != 0) return cmp;
            return a.getName().compareTo(b.getName());
        });
    }

    /**
     * Returns top N players sorted by total matches (descending).
     * Tie-breaking: level descending, then name ascending.
     */
    public List<Player> topByMatches(int n) {
        return topN(n, (a, b) -> {
            int cmp = Integer.compare(b.getTotalMatches(), a.getTotalMatches());
            if (cmp != 0) return cmp;
            cmp = Integer.compare(b.getLevel(), a.getLevel());
            if (cmp != 0) return cmp;
            return a.getName().compareTo(b.getName());
        });
    }

    /**
     * Returns top N players sorted by a custom composite score.
     * Formula: customScore = winRate * 0.5 + (level / 30.0) * 0.3 + (totalMatches / 300.0) * 0.2
     * Tie-breaking: level descending, then name ascending.
     */
    public List<Player> topByCustomScore(int n) {
        return topN(n, (a, b) -> {
            double scoreA = a.getWinRate() * 0.5 + (a.getLevel() / 30.0) * 0.3 + (a.getTotalMatches() / 300.0) * 0.2;
            double scoreB = b.getWinRate() * 0.5 + (b.getLevel() / 30.0) * 0.3 + (b.getTotalMatches() / 300.0) * 0.2;
            int cmp = Double.compare(scoreB, scoreA);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(b.getLevel(), a.getLevel());
            if (cmp != 0) return cmp;
            return a.getName().compareTo(b.getName());
        });
    }

    /**
     * Helper: takes the top N from all players using a comparator.
     */
    private List<Player> topN(int n, Comparator<Player> comparator) {
        List<Player> all = dm.getAllPlayers();
        all.sort(comparator);
        return all.subList(0, Math.min(n, all.size()));
    }

    /**
     * Formats a player leaderboard as a string.
     */
    public String formatLeaderboard(String title, List<Player> players,
                                     boolean showWinRate, boolean showLevel,
                                     boolean showMatches, boolean showScore) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== ").append(title).append(" =====\n");

        // Build dynamic header
        sb.append(String.format("%-4s %-12s", "Rank", "Name"));
        if (showWinRate) sb.append(String.format("%-10s", "WinRate"));
        if (showLevel) sb.append(String.format("%-8s", "Level"));
        if (showMatches) sb.append(String.format("%-8s", "Matches"));
        if (showScore) sb.append(String.format("%-8s", "Score"));
        sb.append("\n");
        sb.append("----------------------------------------------------\n");

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            sb.append(String.format("%-4d %-12s", i + 1, truncate(p.getName(), 12)));
            if (showWinRate) sb.append(String.format("%-10.1f%%", p.getWinRate() * 100));
            if (showLevel) sb.append(String.format("%-8d", p.getLevel()));
            if (showMatches) sb.append(String.format("%-8d", p.getTotalMatches()));
            if (showScore) {
                double score = p.getWinRate() * 0.5 + (p.getLevel() / 30.0) * 0.3 + (p.getTotalMatches() / 300.0) * 0.2;
                sb.append(String.format("%-8.3f", score));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String truncate(String s, int maxLen) {
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 2) + "..";
    }
}
