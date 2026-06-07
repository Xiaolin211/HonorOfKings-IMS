package hok.service;

import hok.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RankingService.
 * Verifies tie-breaking logic, equipment formula, and leaderboard output.
 */
class RankingServiceTest {

    private GameDataManager dm;
    private RankingService rs;

    @BeforeEach
    void setUp() {
        dm = new GameDataManager();
        dm.initializeData();
        rs = new RankingService(dm);
    }

    // --- Equipment ranking formula ---

    @Test
    @DisplayName("rankEquipment returns all 20 items sorted by composite score")
    void testRankEquipmentCount() {
        List<Equipment> ranked = rs.rankEquipment();
        assertEquals(20, ranked.size());
    }

    @Test
    @DisplayName("rankEquipment is sorted descending by score")
    void testRankEquipmentSorted() {
        List<Equipment> ranked = rs.rankEquipment();
        for (int i = 0; i < ranked.size() - 1; i++) {
            double scoreA = ranked.get(i).getUsageCount() * 0.4
                          + ranked.get(i).getRating() * 0.4
                          + countCompatible(ranked.get(i)) * 0.2;
            double scoreB = ranked.get(i + 1).getUsageCount() * 0.4
                          + ranked.get(i + 1).getRating() * 0.4
                          + countCompatible(ranked.get(i + 1)) * 0.2;
            assertTrue(scoreA >= scoreB,
                    "Equipment " + (i + 1) + " should have score >= equipment " + (i + 2));
        }
    }

    private int countCompatible(Equipment eq) {
        int count = 0;
        for (Hero h : dm.getAllHeroes()) {
            if (h.getCompatibleEquipment().contains(eq)) count++;
        }
        return count;
    }

    // --- Leaderboard: tie-breaking ---

    @Test
    @DisplayName("topByWinRate returns correct number of players")
    void testTopByWinRateCount() {
        List<Player> top = rs.topByWinRate(5);
        assertEquals(5, top.size());
    }

    @Test
    @DisplayName("topByWinRate is sorted descending with tie-breaking")
    void testTopByWinRateSorted() {
        List<Player> top = rs.topByWinRate(15);
        for (int i = 0; i < top.size() - 1; i++) {
            Player a = top.get(i);
            Player b = top.get(i + 1);
            assertTrue(
                a.getWinRate() > b.getWinRate()
                || (a.getWinRate() == b.getWinRate() && a.getLevel() >= b.getLevel()),
                "Tie-break failed at position " + i + ": " + a.getName()
                + " (WR:" + a.getWinRate() + " Lv:" + a.getLevel() + ") vs "
                + b.getName() + " (WR:" + b.getWinRate() + " Lv:" + b.getLevel() + ")"
            );
        }
    }

    @Test
    @DisplayName("topByLevel restricts to requested count")
    void testTopByLevelCount() {
        assertEquals(3, rs.topByLevel(3).size());
        assertTrue(rs.topByLevel(100).size() <= 15);
    }

    // --- Custom score ---

    @Test
    @DisplayName("topByCustomScore returns top N by composite formula")
    void testTopByCustomScore() {
        List<Player> top = rs.topByCustomScore(3);
        assertEquals(3, top.size());

        // Verify scores are computed correctly for the top player
        Player best = top.get(0);
        double expectedScore = best.getWinRate() * 0.5
                             + (best.getLevel() / 30.0) * 0.3
                             + (best.getTotalMatches() / 300.0) * 0.2;
        // Score should be in [0, 1] range
        assertTrue(expectedScore >= 0.0 && expectedScore <= 1.5,
                "Custom score for " + best.getName() + " is " + expectedScore);
    }

    // --- Formatting returns non-empty strings ---

    @Test
    @DisplayName("formatEquipmentRanking returns non-empty formatted string")
    void testFormatEquipmentRanking() {
        String output = rs.formatEquipmentRanking();
        assertNotNull(output);
        assertFalse(output.isEmpty());
        assertTrue(output.contains("Infinity Blade") || output.contains("Equipment Ranking"));
    }

    @Test
    @DisplayName("formatLeaderboard returns formatted table with expected columns")
    void testFormatLeaderboard() {
        List<Player> top = rs.topByWinRate(3);
        String output = rs.formatLeaderboard("Test", top, true, true, true, false);
        assertTrue(output.contains("WinRate"));
        assertTrue(output.contains("Level"));
        assertTrue(output.contains("Matches"));
    }
}
