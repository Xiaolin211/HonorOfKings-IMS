package hok;

import hok.service.GameDataManager;
import hok.util.InputHelper;

/**
 * Entry point for the Honor of Kings Management System.
 * Acts as a console menu ROUTER only — no business logic, no data manipulation.
 * All operations delegate to service classes.
 *
 * Note: Some menu options show "coming soon" until their services are implemented
 * in later prompts (SearchService, RankingService, AuthenticationService, etc.).
 */
public class Main {

    private static GameDataManager dataManager;
    private static boolean running = true;

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("  Honor of Kings Information Management");
        System.out.println("  AI-Assisted System v1.0");
        System.out.println("============================================");

        // Initialize data
        dataManager = new GameDataManager();
        dataManager.initializeData();
        System.out.println("Data loaded: " + dataManager.getAllPlayers().size() + " players, "
                + dataManager.getAllHeroes().size() + " heroes, "
                + dataManager.getAllEquipment().size() + " equipment, "
                + dataManager.getAllTeams().size() + " teams, "
                + dataManager.getAllMatchRecords().size() + " matches.");

        // Main loop
        while (running) {
            showMainMenu();
            int choice = InputHelper.readInt("Enter your choice: ", 0, 11);

            switch (choice) {
                case 1: handlePlayerLookup(); break;
                case 2: handleTeamOverview(); break;
                case 3: handleHeroDetails(); break;
                case 4: handleEquipmentStats(); break;
                case 5: handleMatchHistory(); break;
                case 6: handleLeaderboard(); break;
                case 7: handleAdminManagement(); break;
                case 8: handleSaveData(); break;
                case 9: handleLoadData(); break;
                case 10: handleLogin(); break;
                case 11: handleExit(); break;
                case 0: handleExit(); break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println(" 1. Player Lookup");
        System.out.println(" 2. Team Overview");
        System.out.println(" 3. Hero Details");
        System.out.println(" 4. Equipment Statistics");
        System.out.println(" 5. Match History");
        System.out.println(" 6. Leaderboard");
        System.out.println(" 7. Admin Data Management");
        System.out.println(" 8. Save Data");
        System.out.println(" 9. Load Data");
        System.out.println("10. Login");
        System.out.println("11. Exit");
        System.out.println("===============================");
    }

    // ==================== Menu Handlers ====================

    private static void handlePlayerLookup() {
        System.out.println("\n--- Player Lookup ---");
        String query = InputHelper.readString("Enter player ID or name: ");
        PlayerLookupStub.lookup(dataManager, query);
    }

    private static void handleTeamOverview() {
        System.out.println("\n--- Team Overview ---");
        String query = InputHelper.readString("Enter team ID or name: ");
        TeamOverviewStub.lookup(dataManager, query);
    }

    private static void handleHeroDetails() {
        System.out.println("\n--- Hero Details ---");
        String query = InputHelper.readString("Enter hero name: ");
        HeroDetailsStub.lookup(dataManager, query);
    }

    private static void handleEquipmentStats() {
        System.out.println("\n--- Equipment Statistics ---");
        System.out.println("Feature coming in Prompt 08 (RankingService).");
    }

    private static void handleMatchHistory() {
        System.out.println("\n--- Match History ---");
        int limit = InputHelper.readInt("How many recent matches to show? ", 1, 50);
        System.out.println("Feature coming in Prompt 07 (SearchService).");
    }

    private static void handleLeaderboard() {
        System.out.println("\n--- Leaderboard ---");
        System.out.println("1. Top by Win Rate");
        System.out.println("2. Top by Level");
        System.out.println("3. Top by Matches Played");
        System.out.println("4. Top by Custom Score");
        int choice = InputHelper.readInt("Choose ranking: ", 1, 4);
        System.out.println("Feature coming in Prompt 08 (RankingService).");
    }

    private static void handleAdminManagement() {
        System.out.println("\n--- Admin Data Management ---");
        System.out.println("Feature coming in Prompt 10 (AdminService).");
        System.out.println("Requires login as Admin first (Prompt 09).");
    }

    private static void handleSaveData() {
        System.out.println("\n--- Save Data ---");
        System.out.println("Feature coming in Prompt 11 (FileStorageService).");
    }

    private static void handleLoadData() {
        System.out.println("\n--- Load Data ---");
        System.out.println("Feature coming in Prompt 11 (FileStorageService).");
    }

    private static void handleLogin() {
        System.out.println("\n--- Login ---");
        System.out.println("Feature coming in Prompt 09 (AuthenticationService).");
    }

    private static void handleExit() {
        System.out.println("\nGoodbye!");
        running = false;
    }

    // ==================== Temporary stubs (will be replaced by real services) ====================

    /**
     * Temporary stub for Player Lookup.
     * Will be replaced by SearchService in Prompt 07.
     */
    private static class PlayerLookupStub {
        static void lookup(GameDataManager dm, String query) {
            // Try by ID first
            var player = dm.findPlayerById(query);
            if (player == null) {
                player = dm.findPlayerByName(query);
            }
            if (player != null) {
                System.out.println(player.getDetailedInfo());
            } else {
                System.out.println("Player not found: " + query);
            }
        }
    }

    /**
     * Temporary stub for Team Overview.
     * Will be replaced by SearchService in Prompt 07.
     */
    private static class TeamOverviewStub {
        static void lookup(GameDataManager dm, String query) {
            var team = dm.findTeamById(query);
            if (team == null) {
                team = dm.findTeamByName(query);
            }
            if (team != null) {
                System.out.println(team.getDetailedInfo());
            } else {
                System.out.println("Team not found: " + query);
            }
        }
    }

    /**
     * Temporary stub for Hero Details.
     * Will be replaced by SearchService in Prompt 07.
     */
    private static class HeroDetailsStub {
        static void lookup(GameDataManager dm, String query) {
            var hero = dm.findHeroByName(query);
            if (hero != null) {
                System.out.println(hero.getDetailedInfo());
                // Also show who owns this hero
                var owners = dm.findPlayersOwningHero(hero.getId());
                System.out.println("\nOwned by " + owners.size() + " player(s):");
                for (var p : owners) {
                    System.out.println("  " + p.getSummary());
                }
            } else {
                System.out.println("Hero not found: " + query);
            }
        }
    }
}
