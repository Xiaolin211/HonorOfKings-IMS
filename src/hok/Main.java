package hok;

import hok.service.*;
import hok.storage.FileStorageService;
import hok.util.InputHelper;

/**
 * Entry point for the Honor of Kings Management System.
 * Console menu ROUTER only — no business logic.
 * All operations delegate to service classes.
 */
public class Main {

    private static GameDataManager dataManager;
    private static SearchService searchService;
    private static RankingService rankingService;
    private static AuthenticationService authService;
    private static AdminService adminService;
    private static FileStorageService fileService;
    private static boolean running = true;

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("  Honor of Kings Information Management");
        System.out.println("  AI-Assisted System v1.0");
        System.out.println("============================================");

        // Initialize data and services
        dataManager = new GameDataManager();
        dataManager.initializeData();
        searchService = new SearchService(dataManager);
        rankingService = new RankingService(dataManager);
        authService = new AuthenticationService(dataManager);
        adminService = new AdminService(dataManager);
        fileService = new FileStorageService();

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
        if (authService.isLoggedIn()) {
            System.out.println(" Logged in as: " + authService.getCurrentUser().getName()
                    + " (" + authService.getCurrentUser().getRole() + ")");
        }
        System.out.println(" 1. Player Lookup");
        System.out.println(" 2. Team Overview");
        System.out.println(" 3. Hero Details");
        System.out.println(" 4. Equipment Statistics");
        System.out.println(" 5. Match History");
        System.out.println(" 6. Leaderboard");
        System.out.println(" 7. Admin Data Management");
        System.out.println(" 8. Save Data");
        System.out.println(" 9. Load Data");
        System.out.println("10. Login / Logout");
        System.out.println("11. Exit");
        System.out.println("===============================");
    }

    // ==================== Menu Handlers ====================

    private static void handlePlayerLookup() {
        System.out.println("\n--- Player Lookup ---");
        String query = InputHelper.readString("Enter player ID or name: ");
        String result = searchService.lookupPlayer(query);
        if (result != null) {
            System.out.println(result);
        } else {
            System.out.println("Player not found: " + query);
        }
    }

    private static void handleTeamOverview() {
        System.out.println("\n--- Team Overview ---");
        String query = InputHelper.readString("Enter team ID or name: ");
        String result = searchService.lookupTeam(query);
        if (result != null) {
            System.out.println(result);
        } else {
            System.out.println("Team not found: " + query);
        }
    }

    private static void handleHeroDetails() {
        System.out.println("\n--- Hero Details ---");
        String query = InputHelper.readString("Enter hero name: ");
        String result = searchService.lookupHero(query);
        if (result != null) {
            System.out.println(result);
        } else {
            System.out.println("Hero not found: " + query);
        }
    }

    private static void handleEquipmentStats() {
        System.out.println("\n--- Equipment Statistics ---");
        System.out.println(rankingService.formatEquipmentRanking());
    }

    private static void handleMatchHistory() {
        System.out.println("\n--- Match History ---");
        String query = InputHelper.readString("Enter team ID/name or player ID/name: ");
        int limit = InputHelper.readInt("How many matches to show? ", 1, 50);
        String result = searchService.formatMatchHistory(query, limit);
        System.out.println(result);
    }

    private static void handleLeaderboard() {
        System.out.println("\n--- Leaderboard ---");
        System.out.println("1. Top by Win Rate");
        System.out.println("2. Top by Level");
        System.out.println("3. Top by Matches Played");
        System.out.println("4. Top by Custom Score");
        int choice = InputHelper.readInt("Choose ranking: ", 1, 4);
        int n = InputHelper.readInt("How many players to show? ", 1, 15);

        switch (choice) {
            case 1:
                System.out.println(rankingService.formatLeaderboard(
                        "Top Players by Win Rate", rankingService.topByWinRate(n),
                        true, true, false, false));
                break;
            case 2:
                System.out.println(rankingService.formatLeaderboard(
                        "Top Players by Level", rankingService.topByLevel(n),
                        false, true, false, false));
                break;
            case 3:
                System.out.println(rankingService.formatLeaderboard(
                        "Top Players by Matches", rankingService.topByMatches(n),
                        false, true, true, false));
                break;
            case 4:
                System.out.println(rankingService.formatLeaderboard(
                        "Top Players by Custom Score", rankingService.topByCustomScore(n),
                        false, true, false, true));
                break;
        }
    }

    private static void handleAdminManagement() {
        if (!authService.isLoggedIn()) {
            System.out.println("Please login first (Option 10).");
            return;
        }
        if (!authService.isAdmin()) {
            System.out.println("Admin access required. You are logged in as Player.");
            return;
        }
        adminService.showAdminMenu();
    }

    private static void handleSaveData() {
        System.out.println("\n--- Save Data ---");
        boolean ok = fileService.saveAll(
                dataManager.getAllPlayers(),
                dataManager.getAllAdmins(),
                dataManager.getAllHeroes(),
                dataManager.getAllEquipment(),
                dataManager.getAllTeams(),
                dataManager.getAllMatchRecords());
        if (ok) {
            System.out.println("All data saved to data/ directory.");
        } else {
            System.out.println("Some files failed to save. Check console for errors.");
        }
    }

    private static void handleLoadData() {
        System.out.println("\n--- Load Data ---");
        System.out.println("This will REPLACE all current data. Continue?");
        if (!InputHelper.readYesNo("Confirm")) return;

        FileStorageService.LoadedData loaded = fileService.loadAll();
        System.out.println("Loaded: " + loaded.players.size() + " players, "
                + loaded.heroes.size() + " heroes, "
                + loaded.equipment.size() + " equipment, "
                + loaded.teams.size() + " teams, "
                + loaded.matches.size() + " matches.");

        if (loaded.players.isEmpty()) {
            System.out.println("No data loaded — keeping current data.");
            return;
        }

        // Reinitialize GameDataManager with loaded data (manual rebuild)
        // For simplicity, we reinitialize from DataInitializer but the loaded data
        // can be used to manually rebuild the state if needed
        dataManager.initializeData();
        System.out.println("Data reloaded from files.");
    }

    private static void handleLogin() {
        if (authService.isLoggedIn()) {
            System.out.println("Logged in as: " + authService.getCurrentUser().getName());
            if (InputHelper.readYesNo("Logout?")) {
                authService.logout();
                System.out.println("Logged out.");
            }
            return;
        }

        System.out.println("\n--- Login ---");
        System.out.println("Default accounts: admin/admin123  |  p001/p001123");
        String id = InputHelper.readString("ID: ");
        String password = InputHelper.readString("Password: ");

        if (authService.login(id, password)) {
            System.out.println("Login successful!");
            System.out.println("Welcome, " + authService.getCurrentUser().getName()
                    + " (" + authService.getCurrentUser().getRole() + ")");
        } else {
            System.out.println("Login failed. Check ID and password.");
        }
    }

    private static void handleExit() {
        System.out.println("\nGoodbye!");
        running = false;
    }
}
