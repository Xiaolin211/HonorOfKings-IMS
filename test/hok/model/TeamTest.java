package hok.model;

import hok.enums.HeroType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Team class.
 * Verifies aggregation (bidirectional reference), computed statistics, and Reportable.
 */
class TeamTest {

    private Team team;
    private Player p1, p2, p3;

    @BeforeEach
    void setUp() {
        team = new Team("t01", "Dragon Warriors");
        p1 = new Player("p001", "Alex", "pass1");
        p1.setLevel(20); p1.setWinRate(0.70);
        p2 = new Player("p002", "Blake", "pass2");
        p2.setLevel(15); p2.setWinRate(0.50);
        p3 = new Player("p003", "Celine", "pass3");
        p3.setLevel(25); p3.setWinRate(0.80);
    }

    // --- Aggregation: bidirectional reference ---

    @Test
    @DisplayName("addPlayer sets bidirectional Team↔Player reference")
    void testAddPlayerSetsBidirectionalReference() {
        team.addPlayer(p1);
        assertEquals(team, p1.getTeam());
        assertEquals(1, team.getPlayers().size());
        assertEquals("Alex", team.getPlayers().get(0).getName());
    }

    @Test
    @DisplayName("removePlayer clears bidirectional reference")
    void testRemovePlayerClearsBidirectionalReference() {
        team.addPlayer(p1);
        team.addPlayer(p2);
        assertEquals(2, team.getPlayers().size());

        boolean removed = team.removePlayer("p001");
        assertTrue(removed);
        assertNull(p1.getTeam());          // back-reference cleared
        assertEquals(1, team.getPlayers().size());
        assertEquals("Blake", team.getPlayers().get(0).getName());
    }

    // --- Aggregation: players survive team changes ---

    @Test
    @DisplayName("Players survive removal from team (aggregation, not composition)")
    void testPlayersSurviveTeamRemoval() {
        team.addPlayer(p1);
        Hero h = new Hero("h01", "Xiang Yu", HeroType.TANK, 3500, 180, 250, 340);
        p1.addHero(h);

        team.removePlayer("p001");
        assertNull(p1.getTeam());
        // Player still exists with their data intact
        assertEquals("Alex", p1.getName());
        assertEquals(20, p1.getLevel());
        assertEquals(1, p1.getHeroes().size());
    }

    // --- Computed statistics ---

    @Test
    @DisplayName("getAverageLevel computes correctly")
    void testGetAverageLevel() {
        team.addPlayer(p1); // level 20
        team.addPlayer(p2); // level 15
        team.addPlayer(p3); // level 25
        assertEquals(20.0, team.getAverageLevel(), 0.01);
    }

    @Test
    @DisplayName("getWinRate averages member win rates")
    void testGetWinRate() {
        team.addPlayer(p1); // 0.70
        team.addPlayer(p2); // 0.50
        team.addPlayer(p3); // 0.80
        assertEquals(0.666, team.getWinRate(), 0.01);
    }

    @Test
    @DisplayName("getTopPlayer returns player with highest win rate")
    void testGetTopPlayer() {
        team.addPlayer(p1); // 0.70
        team.addPlayer(p2); // 0.50
        team.addPlayer(p3); // 0.80 ← highest
        assertEquals("Celine", team.getTopPlayer().getName());
        assertEquals(0.80, team.getTopPlayer().getWinRate(), 0.001);
    }

    @Test
    @DisplayName("Empty team returns safe defaults for statistics")
    void testEmptyTeamStatistics() {
        assertEquals(0.0, team.getAverageLevel(), 0.01);
        assertEquals(0.0, team.getWinRate(), 0.01);
        assertNull(team.getTopPlayer());
        assertEquals(0, team.getPlayers().size());
    }
}
