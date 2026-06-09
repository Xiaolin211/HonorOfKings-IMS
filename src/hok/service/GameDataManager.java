package hok.service;

import hok.model.Admin;
import hok.model.Equipment;
import hok.model.Hero;
import hok.model.MatchRecord;
import hok.model.Player;
import hok.model.Team;
import hok.storage.FileStorageService;
import hok.util.DataInitializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central in-memory data store for the application.
 * Manages all entities (players, admins, heroes, equipment, teams, match records)
 * and provides CRUD operations with fast HashMap-based lookups.
 *
 * Design: ArrayList for ordered iteration, HashMap for O(1) ID-based access.
 * This is the single source of truth — all services read/write through this class.
 */
public class GameDataManager {

    // === Primary storage (ArrayList — maintains insertion order) ===
    private List<Player> players;
    private List<Admin> admins;
    private List<Hero> heroes;
    private List<Equipment> equipment;
    private List<Team> teams;
    private List<MatchRecord> matchRecords;

    // === Indexes (HashMap — O(1) lookup by ID) ===
    private Map<String, Player> playerIndex;
    private Map<String, Admin> adminIndex;
    private Map<String, Hero> heroIndex;
    private Map<String, Equipment> equipmentIndex;
    private Map<String, Team> teamIndex;
    private Map<String, MatchRecord> matchIndex;

    public GameDataManager() {
        players = new ArrayList<>();
        admins = new ArrayList<>();
        heroes = new ArrayList<>();
        equipment = new ArrayList<>();
        teams = new ArrayList<>();
        matchRecords = new ArrayList<>();

        playerIndex = new HashMap<>();
        adminIndex = new HashMap<>();
        heroIndex = new HashMap<>();
        equipmentIndex = new HashMap<>();
        teamIndex = new HashMap<>();
        matchIndex = new HashMap<>();
    }

    // ==================== Initialization ====================

    /**
     * Populates all data using DataInitializer and rebuilds indexes.
     * Safe to call multiple times — clears existing data first.
     */
    public void initializeData() {
        // Step 1: Create independent entities
        teams = DataInitializer.createTeams();
        heroes = DataInitializer.createHeroes();
        equipment = DataInitializer.createEquipment();

        // Step 2: Wire up hero-equipment compatibility
        DataInitializer.assignCompatibleEquipment(heroes, equipment);

        // Step 3: Create players (wires players ↔ heroes, players ↔ teams)
        players = DataInitializer.createPlayers(teams, heroes);

        // Step 4: Create admin
        admins = new ArrayList<>();
        admins.add(DataInitializer.createAdmin());

        // Step 5: Create match records
        matchRecords = DataInitializer.createMatchRecords(teams);

        // Step 6: Rebuild all indexes
        rebuildAllIndexes();
    }

    /**
     * Replaces all internal data with externally loaded data from CSV files.
     * Preserves team↔player and hero↔equipment relationships by rebuilding indexes.
     */
    public void replaceWithLoadedData(FileStorageService.LoadedData loaded) {
        this.players = new ArrayList<>(loaded.players);
        this.admins = new ArrayList<>(loaded.admins);
        this.heroes = new ArrayList<>(loaded.heroes);
        this.equipment = new ArrayList<>(loaded.equipment);
        this.teams = new ArrayList<>(loaded.teams);
        this.matchRecords = new ArrayList<>(loaded.matches);
        rebuildAllIndexes();
    }

    /**
     * Rebuilds all HashMap indexes from the current ArrayLists.
     * Called automatically by initializeData() and after any data change.
     */
    private void rebuildAllIndexes() {
        playerIndex.clear();
        for (Player p : players) {
            playerIndex.put(p.getId(), p);
        }

        adminIndex.clear();
        for (Admin a : admins) {
            adminIndex.put(a.getId(), a);
        }

        heroIndex.clear();
        for (Hero h : heroes) {
            heroIndex.put(h.getId(), h);
        }

        equipmentIndex.clear();
        for (Equipment e : equipment) {
            equipmentIndex.put(e.getId(), e);
        }

        teamIndex.clear();
        for (Team t : teams) {
            teamIndex.put(t.getId(), t);
        }

        matchIndex.clear();
        for (MatchRecord m : matchRecords) {
            matchIndex.put(m.getId(), m);
        }
    }

    // ==================== Get All ====================

    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }

    public List<Admin> getAllAdmins() {
        return new ArrayList<>(admins);
    }

    public List<Hero> getAllHeroes() {
        return new ArrayList<>(heroes);
    }

    public List<Equipment> getAllEquipment() {
        return new ArrayList<>(equipment);
    }

    public List<Team> getAllTeams() {
        return new ArrayList<>(teams);
    }

    public List<MatchRecord> getAllMatchRecords() {
        return new ArrayList<>(matchRecords);
    }

    // ==================== Find by ID ====================

    public Player findPlayerById(String id) {
        return playerIndex.get(id);
    }

    public Admin findAdminById(String id) {
        return adminIndex.get(id);
    }

    public Hero findHeroById(String id) {
        return heroIndex.get(id);
    }

    public Equipment findEquipmentById(String id) {
        return equipmentIndex.get(id);
    }

    public Team findTeamById(String id) {
        return teamIndex.get(id);
    }

    public MatchRecord findMatchRecordById(String id) {
        return matchIndex.get(id);
    }

    // ==================== Find by Name ====================

    public Player findPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public Team findTeamByName(String name) {
        for (Team t : teams) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    public Hero findHeroByName(String name) {
        // First try exact match
        for (Hero h : heroes) {
            if (h.getName().equalsIgnoreCase(name)) {
                return h;
            }
        }
        // Then try partial match (case-insensitive)
        String lowerName = name.toLowerCase();
        for (Hero h : heroes) {
            if (h.getName().toLowerCase().contains(lowerName)) {
                return h;
            }
        }
        return null;
    }

    public Equipment findEquipmentByName(String name) {
        for (Equipment e : equipment) {
            if (e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    // ==================== Admin: Add ====================

    public boolean addPlayer(Player player) {
        if (player == null || playerIndex.containsKey(player.getId())) {
            return false;
        }
        players.add(player);
        playerIndex.put(player.getId(), player);
        return true;
    }

    public boolean addHero(Hero hero) {
        if (hero == null || heroIndex.containsKey(hero.getId())) {
            return false;
        }
        heroes.add(hero);
        heroIndex.put(hero.getId(), hero);
        return true;
    }

    public boolean addEquipment(Equipment eq) {
        if (eq == null || equipmentIndex.containsKey(eq.getId())) {
            return false;
        }
        equipment.add(eq);
        equipmentIndex.put(eq.getId(), eq);
        return true;
    }

    public boolean addTeam(Team team) {
        if (team == null || teamIndex.containsKey(team.getId())) {
            return false;
        }
        teams.add(team);
        teamIndex.put(team.getId(), team);
        return true;
    }

    public boolean addMatchRecord(MatchRecord record) {
        if (record == null || matchIndex.containsKey(record.getId())) {
            return false;
        }
        matchRecords.add(record);
        matchIndex.put(record.getId(), record);
        return true;
    }

    // ==================== Admin: Delete ====================

    /**
     * Deletes a player. Also removes the player from their team's roster.
     */
    public boolean deletePlayer(String id) {
        Player player = playerIndex.remove(id);
        if (player == null) {
            return false;
        }
        // Remove from team if assigned
        Team team = player.getTeam();
        if (team != null) {
            team.removePlayer(id);
        }
        players.remove(player);
        return true;
    }

    /**
     * Deletes a hero. Also removes the hero from all players who own it.
     */
    public boolean deleteHero(String id) {
        Hero hero = heroIndex.remove(id);
        if (hero == null) {
            return false;
        }
        // Remove from all players' hero lists
        for (Player p : players) {
            p.removeHero(id);
        }
        heroes.remove(hero);
        return true;
    }

    /**
     * Deletes equipment. Also removes it from all heroes' compatible lists.
     */
    public boolean deleteEquipment(String id) {
        Equipment eq = equipmentIndex.remove(id);
        if (eq == null) {
            return false;
        }
        // Remove from all heroes' compatible equipment
        for (Hero h : heroes) {
            h.removeCompatibleEquipment(id);
        }
        equipment.remove(eq);
        return true;
    }

    /**
     * Deletes a team. Sets all member players' team reference to null.
     */
    public boolean deleteTeam(String id) {
        Team team = teamIndex.remove(id);
        if (team == null) {
            return false;
        }
        // Remove team reference from all members
        for (Player p : team.getPlayers()) {
            p.setTeam(null);
        }
        teams.remove(team);
        return true;
    }

    /**
     * Deletes a match record. No cascading needed (association only).
     */
    public boolean deleteMatchRecord(String id) {
        MatchRecord record = matchIndex.remove(id);
        if (record == null) {
            return false;
        }
        matchRecords.remove(record);
        return true;
    }

    // ==================== Admin: Update ====================

    /**
     * Replaces an existing player with updated data.
     * Preserves the player's team and hero relationships.
     */
    public boolean updatePlayer(String id, Player updated) {
        Player existing = playerIndex.get(id);
        if (existing == null || updated == null) {
            return false;
        }
        // Preserve relationships
        updated.setTeam(existing.getTeam());
        for (Hero h : existing.getHeroes()) {
            updated.addHero(h);
        }
        // Replace in list and index
        int idx = players.indexOf(existing);
        players.set(idx, updated);
        playerIndex.put(id, updated);
        return true;
    }

    /**
     * Replaces an existing hero with updated data.
     * Preserves compatible equipment and player ownership.
     */
    public boolean updateHero(String id, Hero updated) {
        Hero existing = heroIndex.get(id);
        if (existing == null || updated == null) {
            return false;
        }
        // Preserve equipment compatibility and player ownership
        for (Equipment e : existing.getCompatibleEquipment()) {
            updated.addCompatibleEquipment(e);
        }
        for (Player p : players) {
            if (p.getHeroes().contains(existing)) {
                p.removeHero(id);
                p.addHero(updated);
            }
        }
        int idx = heroes.indexOf(existing);
        heroes.set(idx, updated);
        heroIndex.put(id, updated);
        return true;
    }

    /**
     * Replaces existing equipment with updated data.
     */
    public boolean updateEquipment(String id, Equipment updated) {
        Equipment existing = equipmentIndex.get(id);
        if (existing == null || updated == null) {
            return false;
        }
        // Preserve hero associations
        for (Hero h : heroes) {
            if (h.getCompatibleEquipment().contains(existing)) {
                h.removeCompatibleEquipment(id);
                h.addCompatibleEquipment(updated);
            }
        }
        int idx = equipment.indexOf(existing);
        equipment.set(idx, updated);
        equipmentIndex.put(id, updated);
        return true;
    }

    /**
     * Replaces an existing team with updated data.
     */
    public boolean updateTeam(String id, Team updated) {
        Team existing = teamIndex.get(id);
        if (existing == null || updated == null) {
            return false;
        }
        // Transfer all players to the updated team
        for (Player p : existing.getPlayers()) {
            updated.addPlayer(p);
        }
        int idx = teams.indexOf(existing);
        teams.set(idx, updated);
        teamIndex.put(id, updated);
        return true;
    }

    /**
     * Replaces an existing match record with updated data.
     */
    public boolean updateMatchRecord(String id, MatchRecord updated) {
        MatchRecord existing = matchIndex.get(id);
        if (existing == null || updated == null) {
            return false;
        }
        int idx = matchRecords.indexOf(existing);
        matchRecords.set(idx, updated);
        matchIndex.put(id, updated);
        return true;
    }

    // ==================== Utility ====================

    /**
     * Returns all players that own a specific hero.
     */
    public List<Player> findPlayersOwningHero(String heroId) {
        List<Player> result = new ArrayList<>();
        Hero hero = findHeroById(heroId);
        if (hero == null) {
            return result;
        }
        for (Player p : players) {
            if (p.getHeroes().contains(hero)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Returns all match records involving a specific team.
     */
    public List<MatchRecord> findMatchesByTeam(String teamId) {
        List<MatchRecord> result = new ArrayList<>();
        Team team = findTeamById(teamId);
        if (team == null) {
            return result;
        }
        for (MatchRecord m : matchRecords) {
            if (m.getTeamA().equals(team) || m.getTeamB().equals(team)) {
                result.add(m);
            }
        }
        return result;
    }
}
