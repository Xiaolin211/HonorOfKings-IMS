package hok.model;

import hok.enums.HeroType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Player class.
 * Verifies composition (hero ownership), defensive copies, and Reportable interface.
 */
class PlayerTest {

    private Player player;
    private Hero hero1;
    private Hero hero2;

    @BeforeEach
    void setUp() {
        player = new Player("p001", "Alex", "pass123");
        player.setLevel(20);
        player.setWinRate(0.65);
        player.setTotalMatches(100);
        hero1 = new Hero("h01", "Xiang Yu", HeroType.TANK, 3500, 180, 250, 340);
        hero2 = new Hero("h02", "Li Bai", HeroType.WARRIOR, 2800, 320, 180, 370);
    }

    // --- Composition: Player owns Heroes ---

    @Test
    @DisplayName("addHero adds hero to player's collection")
    void testAddHero() {
        player.addHero(hero1);
        List<Hero> heroes = player.getHeroes();
        assertEquals(1, heroes.size());
        assertTrue(heroes.contains(hero1));
    }

    @Test
    @DisplayName("removeHero removes hero by ID")
    void testRemoveHero() {
        player.addHero(hero1);
        player.addHero(hero2);
        assertEquals(2, player.getHeroes().size());

        boolean removed = player.removeHero("h01");
        assertTrue(removed);
        assertEquals(1, player.getHeroes().size());
        assertFalse(player.getHeroes().contains(hero1));
        assertTrue(player.getHeroes().contains(hero2));
    }

    @Test
    @DisplayName("removeHero returns false for non-existent hero")
    void testRemoveNonExistentHero() {
        player.addHero(hero1);
        boolean removed = player.removeHero("h99");
        assertFalse(removed);
        assertEquals(1, player.getHeroes().size());
    }

    // --- Encapsulation: defensive copy ---

    @Test
    @DisplayName("getHeroes returns defensive copy — external mutation does not affect player")
    void testGetHeroesDefensiveCopy() {
        player.addHero(hero1);
        player.addHero(hero2);

        List<Hero> externalList = player.getHeroes();
        externalList.clear(); // mutate the returned copy

        // Player's internal list must remain unchanged
        assertEquals(2, player.getHeroes().size());
    }

    // --- Reportable interface ---

    @Test
    @DisplayName("getSummary returns formatted one-liner")
    void testGetSummary() {
        String summary = player.getSummary();
        assertTrue(summary.contains("p001"));
        assertTrue(summary.contains("Alex"));
        assertTrue(summary.contains("65.0%"));
    }

    @Test
    @DisplayName("getDetailedInfo returns multi-line details")
    void testGetDetailedInfo() {
        player.addHero(hero1);
        String details = player.getDetailedInfo();
        assertTrue(details.contains("Player Details"));
        assertTrue(details.contains("Alex"));
        assertTrue(details.contains("20"));
        assertTrue(details.contains("65.0%"));
        assertTrue(details.contains("Xiang Yu"));
    }
}
