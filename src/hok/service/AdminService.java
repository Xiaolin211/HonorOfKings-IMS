package hok.service;

import hok.enums.EquipmentType;
import hok.enums.HeroType;
import hok.enums.MatchResult;
import hok.model.Equipment;
import hok.model.Hero;
import hok.model.MatchRecord;
import hok.model.Player;
import hok.model.Team;
import hok.util.InputHelper;

/**
 * Admin-only data management operations.
 * All methods assume the caller has already verified admin privileges.
 * Provides interactive console menus for CRUD on all entity types.
 */
public class AdminService {

    private GameDataManager dm;

    public AdminService(GameDataManager dm) {
        this.dm = dm;
    }

    /**
     * Main admin menu loop. Returns when user selects "Back".
     */
    public void showAdminMenu() {
        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println("\n===== ADMIN DATA MANAGEMENT =====");
            System.out.println("1. Manage Players");
            System.out.println("2. Manage Heroes");
            System.out.println("3. Manage Equipment");
            System.out.println("4. Manage Teams");
            System.out.println("5. Manage Match Records");
            System.out.println("0. Back to Main Menu");
            System.out.println("=================================");

            int choice = InputHelper.readInt("Enter choice: ", 0, 5);
            switch (choice) {
                case 1: managePlayers(); break;
                case 2: manageHeroes(); break;
                case 3: manageEquipment(); break;
                case 4: manageTeams(); break;
                case 5: manageMatchRecords(); break;
                case 0: inAdmin = false; break;
            }
        }
    }

    // ==================== Player Management ====================

    private void managePlayers() {
        System.out.println("\n--- Manage Players ---");
        System.out.println("1. List All Players");
        System.out.println("2. Add Player");
        System.out.println("3. Edit Player");
        System.out.println("4. Delete Player");
        int choice = InputHelper.readInt("Enter choice: ", 1, 4);

        switch (choice) {
            case 1:
                for (Player p : dm.getAllPlayers()) {
                    System.out.println(p.getSummary());
                }
                break;
            case 2: {
                String id = InputHelper.readString("New player ID (e.g. p016): ");
                String name = InputHelper.readString("Name: ");
                Player p = new Player(id, name, id + "123");
                p.setLevel(InputHelper.readInt("Level (1-30): ", 1, 30));
                p.setWinRate(InputHelper.readDouble("Win Rate (0.0-1.0): "));
                p.setTotalMatches(InputHelper.readInt("Total Matches: ", 0, 9999));
                if (dm.addPlayer(p)) {
                    System.out.println("Player added: " + p.getSummary());
                } else {
                    System.out.println("Failed: duplicate ID or null player.");
                }
                break;
            }
            case 3: {
                String id = InputHelper.readString("Player ID to edit: ");
                Player existing = dm.findPlayerById(id);
                if (existing == null) {
                    System.out.println("Player not found.");
                    break;
                }
                String name = InputHelper.readString("New name [" + existing.getName() + "]: ");
                if (!name.isEmpty()) existing.setName(name);
                existing.setLevel(InputHelper.readInt("New level [" + existing.getLevel() + "]: ", 1, 30));
                System.out.println("Player updated: " + existing.getSummary());
                break;
            }
            case 4: {
                String id = InputHelper.readString("Player ID to delete: ");
                if (dm.deletePlayer(id)) {
                    System.out.println("Player deleted.");
                } else {
                    System.out.println("Player not found.");
                }
                break;
            }
        }
    }

    // ==================== Hero Management ====================

    private void manageHeroes() {
        System.out.println("\n--- Manage Heroes ---");
        System.out.println("1. List All Heroes");
        System.out.println("2. Add Hero");
        System.out.println("3. Delete Hero");
        int choice = InputHelper.readInt("Enter choice: ", 1, 3);

        switch (choice) {
            case 1:
                for (Hero h : dm.getAllHeroes()) {
                    System.out.println(h.getSummary());
                }
                break;
            case 2: {
                String id = InputHelper.readString("New hero ID (e.g. h16): ");
                String name = InputHelper.readString("Name: ");
                System.out.println("Types: 1-TANK 2-WARRIOR 3-ASSASSIN 4-MAGE 5-MARKSMAN 6-SUPPORT");
                int typeChoice = InputHelper.readInt("Type: ", 1, 6);
                HeroType type = HeroType.values()[typeChoice - 1];
                int hp = InputHelper.readInt("HP: ", 1, 9999);
                int atk = InputHelper.readInt("Attack: ", 1, 999);
                int def = InputHelper.readInt("Defense: ", 1, 999);
                int spd = InputHelper.readInt("Speed: ", 1, 999);
                Hero h = new Hero(id, name, type, hp, atk, def, spd);
                if (dm.addHero(h)) {
                    System.out.println("Hero added: " + h.getSummary());
                } else {
                    System.out.println("Failed: duplicate ID.");
                }
                break;
            }
            case 3: {
                String id = InputHelper.readString("Hero ID to delete: ");
                if (dm.deleteHero(id)) {
                    System.out.println("Hero deleted (removed from all players).");
                } else {
                    System.out.println("Hero not found.");
                }
                break;
            }
        }
    }

    // ==================== Equipment Management ====================

    private void manageEquipment() {
        System.out.println("\n--- Manage Equipment ---");
        System.out.println("1. List All Equipment");
        System.out.println("2. Add Equipment");
        System.out.println("3. Edit Equipment Rating");
        System.out.println("4. Delete Equipment");
        int choice = InputHelper.readInt("Enter choice: ", 1, 4);

        switch (choice) {
            case 1:
                for (Equipment e : dm.getAllEquipment()) {
                    System.out.println(e.toString());
                }
                break;
            case 2: {
                String id = InputHelper.readString("New equipment ID (e.g. e21): ");
                String name = InputHelper.readString("Name: ");
                System.out.println("Types: 1-ATTACK 2-DEFENSE 3-MAGIC 4-MOVEMENT 5-JUNGLE");
                int t = InputHelper.readInt("Type: ", 1, 5);
                EquipmentType type = EquipmentType.values()[t - 1];
                int atk = InputHelper.readInt("Attack Bonus: ", 0, 999);
                int def = InputHelper.readInt("Defense Bonus: ", 0, 999);
                int hp = InputHelper.readInt("HP Bonus: ", 0, 999);
                int spd = InputHelper.readInt("Speed Bonus: ", 0, 999);
                double rating = InputHelper.readDouble("Rating (1.0-10.0): ");
                Equipment eq = new Equipment(id, name, type, atk, def, hp, spd, rating);
                if (dm.addEquipment(eq)) {
                    System.out.println("Equipment added: " + eq.toString());
                } else {
                    System.out.println("Failed: duplicate ID.");
                }
                break;
            }
            case 3: {
                String id = InputHelper.readString("Equipment ID to edit: ");
                Equipment eq = dm.findEquipmentById(id);
                if (eq == null) {
                    System.out.println("Equipment not found.");
                    break;
                }
                eq.setRating(InputHelper.readDouble("New rating [" + eq.getRating() + "]: "));
                System.out.println("Updated: " + eq.toString());
                break;
            }
            case 4: {
                String id = InputHelper.readString("Equipment ID to delete: ");
                if (dm.deleteEquipment(id)) {
                    System.out.println("Equipment deleted (removed from all heroes).");
                } else {
                    System.out.println("Equipment not found.");
                }
                break;
            }
        }
    }

    // ==================== Team Management ====================

    private void manageTeams() {
        System.out.println("\n--- Manage Teams ---");
        System.out.println("1. List All Teams");
        System.out.println("2. Add Team");
        System.out.println("3. Add Player to Team");
        System.out.println("4. Remove Player from Team");
        System.out.println("5. Delete Team");
        int choice = InputHelper.readInt("Enter choice: ", 1, 5);

        switch (choice) {
            case 1:
                for (Team t : dm.getAllTeams()) {
                    System.out.println(t.getSummary());
                }
                break;
            case 2: {
                String id = InputHelper.readString("New team ID (e.g. t04): ");
                String name = InputHelper.readString("Team name: ");
                if (dm.addTeam(new Team(id, name))) {
                    System.out.println("Team added.");
                } else {
                    System.out.println("Failed: duplicate ID.");
                }
                break;
            }
            case 3: {
                String teamId = InputHelper.readString("Team ID: ");
                Team team = dm.findTeamById(teamId);
                if (team == null) { System.out.println("Team not found."); break; }
                String playerId = InputHelper.readString("Player ID to add: ");
                Player player = dm.findPlayerById(playerId);
                if (player == null) { System.out.println("Player not found."); break; }
                if (player.getTeam() != null) {
                    System.out.println("Player is already in team: " + player.getTeam().getName());
                    if (!InputHelper.readYesNo("Move to new team?")) break;
                    player.getTeam().removePlayer(playerId);
                }
                team.addPlayer(player);
                System.out.println("Player added to team.");
                break;
            }
            case 4: {
                String teamId = InputHelper.readString("Team ID: ");
                Team team = dm.findTeamById(teamId);
                if (team == null) { System.out.println("Team not found."); break; }
                String playerId = InputHelper.readString("Player ID to remove: ");
                if (team.removePlayer(playerId)) {
                    System.out.println("Player removed from team.");
                } else {
                    System.out.println("Player not in this team.");
                }
                break;
            }
            case 5: {
                String id = InputHelper.readString("Team ID to delete: ");
                if (dm.deleteTeam(id)) {
                    System.out.println("Team deleted (players set to no team).");
                } else {
                    System.out.println("Team not found.");
                }
                break;
            }
        }
    }

    // ==================== Match Record Management ====================

    private void manageMatchRecords() {
        System.out.println("\n--- Manage Match Records ---");
        System.out.println("1. List All Matches");
        System.out.println("2. Add Match Record");
        System.out.println("3. Delete Match Record");
        int choice = InputHelper.readInt("Enter choice: ", 1, 3);

        switch (choice) {
            case 1:
                for (MatchRecord m : dm.getAllMatchRecords()) {
                    System.out.println(m.toString());
                }
                break;
            case 2: {
                String id = InputHelper.readString("New match ID (e.g. m11): ");
                String teamAId = InputHelper.readString("Team A ID: ");
                Team teamA = dm.findTeamById(teamAId);
                if (teamA == null) { System.out.println("Team A not found."); break; }
                String teamBId = InputHelper.readString("Team B ID: ");
                Team teamB = dm.findTeamById(teamBId);
                if (teamB == null) { System.out.println("Team B not found."); break; }
                System.out.println("Result (from Team A's perspective): 1-WIN 2-LOSE 3-DRAW");
                int r = InputHelper.readInt("Result: ", 1, 3);
                MatchResult result = MatchResult.values()[r - 1];
                String date = InputHelper.readString("Date (YYYY-MM-DD): ");
                String type = InputHelper.readString("Match type (e.g. Ranked): ");
                MatchRecord mr = new MatchRecord(id, teamA, teamB, result, date, type);
                if (dm.addMatchRecord(mr)) {
                    System.out.println("Match record added.");
                } else {
                    System.out.println("Failed: duplicate ID.");
                }
                break;
            }
            case 3: {
                String id = InputHelper.readString("Match ID to delete: ");
                if (dm.deleteMatchRecord(id)) {
                    System.out.println("Match record deleted.");
                } else {
                    System.out.println("Match record not found.");
                }
                break;
            }
        }
    }
}
