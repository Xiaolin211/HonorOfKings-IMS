package hok.service;

import hok.enums.*;
import hok.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameDataManager.
 * Verifies cascade delete safety, ID lookup, and defensive copies.
 */
class GameDataManagerTest {

    private GameDataManager dm;

    @BeforeEach
    void setUp() {
        dm = new GameDataManager();
        dm.initializeData();
    }

    // --- Dataset minimums ---

    @Test
    @DisplayName("Dataset meets all minimum count requirements")
    void testDatasetMinimums() {
        assertEquals(15, dm.getAllPlayers().size());
        assertEquals(15, dm.getAllHeroes().size());
        assertEquals(20, dm.getAllEquipment().size());
        assertEquals(3, dm.getAllTeams().size());
        assertEquals(10, dm.getAllMatchRecords().size());
        assertEquals(1, dm.getAllAdmins().size());
    }

    // --- Cascade delete: Hero removed from all players ---

    @Test
    @DisplayName("deleteHero removes hero from all owning players")
    void testDeleteHeroCascade() {
        Hero hero = dm.findHeroById("h01"); // Xiang Yu
        assertNotNull(hero);

        // Count how many players own this hero before deletion
        int ownersBefore = 0;
        for (Player p : dm.getAllPlayers()) {
            if (p.getHeroes().contains(hero)) ownersBefore++;
        }
        assertTrue(ownersBefore > 0, "At least one player should own h01");

        dm.deleteHero("h01");

        // After deletion, no player should own this hero
        for (Player p : dm.getAllPlayers()) {
            assertFalse(p.getHeroes().contains(hero),
                    "Player " + p.getName() + " should no longer own deleted hero h01");
        }
        assertNull(dm.findHeroById("h01"));
    }

    // --- Cascade delete: Player removed from team ---

    @Test
    @DisplayName("deletePlayer removes player from their team roster")
    void testDeletePlayerCascade() {
        Player player = dm.findPlayerById("p001");
        assertNotNull(player);
        Team team = player.getTeam();
        assertNotNull(team);

        int teamSizeBefore = team.getPlayers().size();
        dm.deletePlayer("p001");

        assertEquals(teamSizeBefore - 1, team.getPlayers().size());
        assertNull(dm.findPlayerById("p001"));
    }

    // --- Cascade delete: Equipment removed from all heroes ---

    @Test
    @DisplayName("deleteEquipment removes equipment from all hero compatible lists")
    void testDeleteEquipmentCascade() {
        Equipment eq = dm.findEquipmentById("e01"); // Infinity Blade
        assertNotNull(eq);

        dm.deleteEquipment("e01");

        for (Hero h : dm.getAllHeroes()) {
            assertFalse(h.getCompatibleEquipment().contains(eq),
                    "Hero " + h.getName() + " should no longer have deleted equipment e01");
        }
        assertNull(dm.findEquipmentById("e01"));
    }

    // --- Cascade delete: Team sets all members' team to null ---

    @Test
    @DisplayName("deleteTeam sets all member player team references to null")
    void testDeleteTeamCascade() {
        Team team = dm.findTeamById("t01");
        assertNotNull(team);
        int memberCount = team.getPlayers().size();
        assertTrue(memberCount > 0);

        dm.deleteTeam("t01");

        for (Player p : dm.getAllPlayers()) {
            if (p.getId().startsWith("p00") && Integer.parseInt(p.getId().substring(3)) <= 5) {
                // First 5 players were in t01
                assertNull(p.getTeam(),
                        "Player " + p.getName() + " should have null team after t01 deletion");
            }
        }
        assertNull(dm.findTeamById("t01"));
    }

    // --- Defensive copies ---

    @Test
    @DisplayName("getAll methods return defensive copies")
    void testGetAllReturnsDefensiveCopy() {
        List<Player> external = dm.getAllPlayers();
        int originalSize = external.size();
        external.clear(); // try to mutate the copy

        // Original data must be unchanged
        assertEquals(originalSize, dm.getAllPlayers().size());
    }

    // --- Lookup: find by non-existent ID returns null ---

    @Test
    @DisplayName("find methods return null for non-existent IDs (no exceptions)")
    void testFindNonExistentReturnsNull() {
        assertNull(dm.findPlayerById("p999"));
        assertNull(dm.findHeroById("h999"));
        assertNull(dm.findEquipmentById("e999"));
        assertNull(dm.findTeamById("t999"));
        assertNull(dm.findMatchRecordById("m999"));
    }

    // --- Duplicate ID rejection ---

    @Test
    @DisplayName("addPlayer rejects duplicate ID")
    void testAddPlayerDuplicateIdRejected() {
        Player dup = new Player("p001", "Copy", "pass");
        assertFalse(dm.addPlayer(dup), "Should reject duplicate player ID");
        assertEquals(15, dm.getAllPlayers().size()); // still 15, not 16
    }
}
