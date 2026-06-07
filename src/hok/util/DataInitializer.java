package hok.util;

import hok.enums.EquipmentType;
import hok.enums.HeroType;
import hok.enums.MatchResult;
import hok.model.Admin;
import hok.model.Equipment;
import hok.model.Hero;
import hok.model.MatchRecord;
import hok.model.Player;
import hok.model.Team;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates the initial in-memory dataset for the application.
 * All data is created via static methods that return clean Lists.
 * Called by GameDataManager during initialization.
 *
 * Dataset summary:
 *   - 3 Teams
 *   - 15 Players (5 per team)
 *   - 1 Admin
 *   - 15 Heroes (across all 6 types)
 *   - 20 Equipment items (across all 5 types)
 *   - 10 Match Records
 */
public class DataInitializer {

    // ==================== Teams ====================

    public static List<Team> createTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("t01", "Dragon Warriors"));
        teams.add(new Team("t02", "Phoenix Rise"));
        teams.add(new Team("t03", "Shadow Reapers"));
        return teams;
    }

    // ==================== Players ====================

    public static List<Player> createPlayers(List<Team> teams, List<Hero> heroes) {
        List<Player> players = new ArrayList<>();

        // --- Team 1: Dragon Warriors (5 players) ---
        Player p1 = new Player("p001", "Alex", "p001123");
        p1.setLevel(22); p1.setWinRate(0.68); p1.setTotalMatches(145);
        players.add(p1);

        Player p2 = new Player("p002", "Blake", "p002123");
        p2.setLevel(18); p2.setWinRate(0.55); p2.setTotalMatches(98);
        players.add(p2);

        Player p3 = new Player("p003", "Celine", "p003123");
        p3.setLevel(25); p3.setWinRate(0.72); p3.setTotalMatches(210);
        players.add(p3);

        Player p4 = new Player("p004", "Derek", "p004123");
        p4.setLevel(15); p4.setWinRate(0.45); p4.setTotalMatches(67);
        players.add(p4);

        Player p5 = new Player("p005", "Elena", "p005123");
        p5.setLevel(20); p5.setWinRate(0.61); p5.setTotalMatches(120);
        players.add(p5);

        // --- Team 2: Phoenix Rise (5 players) ---
        Player p6 = new Player("p006", "Felix", "p006123");
        p6.setLevel(27); p6.setWinRate(0.80); p6.setTotalMatches(300);
        players.add(p6);

        Player p7 = new Player("p007", "Grace", "p007123");
        p7.setLevel(16); p7.setWinRate(0.50); p7.setTotalMatches(78);
        players.add(p7);

        Player p8 = new Player("p008", "Hector", "p008123");
        p8.setLevel(21); p8.setWinRate(0.63); p8.setTotalMatches(155);
        players.add(p8);

        Player p9 = new Player("p009", "Iris", "p009123");
        p9.setLevel(19); p9.setWinRate(0.57); p9.setTotalMatches(89);
        players.add(p9);

        Player p10 = new Player("p010", "Jack", "p010123");
        p10.setLevel(24); p10.setWinRate(0.70); p10.setTotalMatches(180);
        players.add(p10);

        // --- Team 3: Shadow Reapers (5 players) ---
        Player p11 = new Player("p011", "Kara", "p011123");
        p11.setLevel(26); p11.setWinRate(0.75); p11.setTotalMatches(250);
        players.add(p11);

        Player p12 = new Player("p012", "Liam", "p012123");
        p12.setLevel(14); p12.setWinRate(0.40); p12.setTotalMatches(55);
        players.add(p12);

        Player p13 = new Player("p013", "Mia", "p013123");
        p13.setLevel(23); p13.setWinRate(0.66); p13.setTotalMatches(165);
        players.add(p13);

        Player p14 = new Player("p014", "Noah", "p014123");
        p14.setLevel(17); p14.setWinRate(0.52); p14.setTotalMatches(72);
        players.add(p14);

        Player p15 = new Player("p015", "Olivia", "p015123");
        p15.setLevel(20); p15.setWinRate(0.59); p15.setTotalMatches(110);
        players.add(p15);

        // Assign heroes to players (at least 3 each)
        // Each index maps to a hero in the heroes list
        int[][] playerHeroIndices = {
            {0, 1, 2, 7},       // p001: 4 heroes
            {3, 4, 5},          // p002: 3 heroes
            {1, 6, 7, 8},       // p003: 4 heroes
            {2, 3, 9},          // p004: 3 heroes
            {0, 5, 10, 11},     // p005: 4 heroes
            {4, 6, 8, 12},      // p006: 4 heroes
            {1, 3, 9},          // p007: 3 heroes
            {7, 10, 13},        // p008: 3 heroes
            {0, 2, 11, 14},     // p009: 4 heroes
            {5, 6, 12},         // p010: 3 heroes
            {3, 8, 10, 13},     // p011: 4 heroes
            {1, 4, 9},          // p012: 3 heroes
            {0, 7, 11, 14},     // p013: 4 heroes
            {2, 5, 12},         // p014: 3 heroes
            {6, 8, 13, 14}      // p015: 4 heroes
        };

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            for (int heroIdx : playerHeroIndices[i]) {
                p.addHero(heroes.get(heroIdx));
            }
        }

        // Assign players to teams (5 per team)
        Team t1 = teams.get(0);
        Team t2 = teams.get(1);
        Team t3 = teams.get(2);

        t1.addPlayer(p1); t1.addPlayer(p2); t1.addPlayer(p3);
        t1.addPlayer(p4); t1.addPlayer(p5);

        t2.addPlayer(p6); t2.addPlayer(p7); t2.addPlayer(p8);
        t2.addPlayer(p9); t2.addPlayer(p10);

        t3.addPlayer(p11); t3.addPlayer(p12); t3.addPlayer(p13);
        t3.addPlayer(p14); t3.addPlayer(p15);

        return players;
    }

    // ==================== Admin ====================

    public static Admin createAdmin() {
        return new Admin("admin", "SystemAdmin", "admin123");
    }

    // ==================== Heroes ====================

    public static List<Hero> createHeroes() {
        List<Hero> heroes = new ArrayList<>();

        // Tanks (3)
        heroes.add(new Hero("h01", "Xiang Yu", HeroType.TANK, 3500, 180, 250, 340));
        heroes.add(new Hero("h02", "Lian Po", HeroType.TANK, 3800, 160, 280, 320));
        heroes.add(new Hero("h03", "Bai Qi", HeroType.TANK, 3600, 170, 270, 330));

        // Warriors (3)
        heroes.add(new Hero("h04", "Li Bai", HeroType.WARRIOR, 2800, 320, 180, 370));
        heroes.add(new Hero("h05", "Hua Mulan", HeroType.WARRIOR, 3000, 290, 200, 360));
        heroes.add(new Hero("h06", "Lu Bu", HeroType.WARRIOR, 3200, 310, 190, 350));

        // Assassins (3)
        heroes.add(new Hero("h07", "A Ke", HeroType.ASSASSIN, 2200, 400, 120, 420));
        heroes.add(new Hero("h08", "Lanling Wang", HeroType.ASSASSIN, 2400, 380, 130, 410));
        heroes.add(new Hero("h09", "Han Xin", HeroType.ASSASSIN, 2300, 390, 125, 430));

        // Mages (2)
        heroes.add(new Hero("h10", "Diao Chan", HeroType.MAGE, 2000, 420, 100, 350));
        heroes.add(new Hero("h11", "Zhuge Liang", HeroType.MAGE, 2100, 400, 110, 340));

        // Marksmen (2)
        heroes.add(new Hero("h12", "Luban No.7", HeroType.MARKSMAN, 2400, 350, 140, 360));
        heroes.add(new Hero("h13", "Hou Yi", HeroType.MARKSMAN, 2500, 340, 150, 350));

        // Supports (2)
        heroes.add(new Hero("h14", "Cai Wenji", HeroType.SUPPORT, 2200, 150, 160, 380));
        heroes.add(new Hero("h15", "Da Qiao", HeroType.SUPPORT, 2100, 160, 150, 390));

        return heroes;
    }

    // ==================== Equipment ====================

    public static List<Equipment> createEquipment() {
        List<Equipment> eqs = new ArrayList<>();

        // Attack items (5)
        eqs.add(new Equipment("e01", "Infinity Blade", EquipmentType.ATTACK, 120, 0, 0, 0, 9.5));
        eqs.add(new Equipment("e02", "Bloodthirsty Bow", EquipmentType.ATTACK, 100, 0, 0, 20, 8.5));
        eqs.add(new Equipment("e03", "Shadow Axe", EquipmentType.ATTACK, 110, 10, 0, 0, 8.0));
        eqs.add(new Equipment("e04", "Lightning Dagger", EquipmentType.ATTACK, 90, 0, 0, 40, 7.5));
        eqs.add(new Equipment("e05", "Doomsday Blade", EquipmentType.ATTACK, 130, 0, 0, -10, 9.0));

        // Defense items (5)
        eqs.add(new Equipment("e06", "Titan Shield", EquipmentType.DEFENSE, 0, 150, 300, 0, 9.0));
        eqs.add(new Equipment("e07", "Guardian Armor", EquipmentType.DEFENSE, 0, 180, 200, -20, 8.5));
        eqs.add(new Equipment("e08", "Immortal Cloak", EquipmentType.DEFENSE, 0, 120, 400, 0, 9.5));
        eqs.add(new Equipment("e09", "Frost Armor", EquipmentType.DEFENSE, 0, 140, 250, 10, 8.0));
        eqs.add(new Equipment("e10", "Thorn Mail", EquipmentType.DEFENSE, 20, 160, 150, 0, 7.5));

        // Magic items (4)
        eqs.add(new Equipment("e11", "Arcane Staff", EquipmentType.MAGIC, 0, 0, 100, 0, 8.5));
        eqs.add(new Equipment("e12", "Void Scepter", EquipmentType.MAGIC, 0, 0, 80, 20, 8.0));
        eqs.add(new Equipment("e13", "Holy Grail", EquipmentType.MAGIC, 0, 0, 120, 10, 9.0));
        eqs.add(new Equipment("e14", "Frozen Wand", EquipmentType.MAGIC, 0, 10, 90, 15, 7.5));

        // Movement items (3)
        eqs.add(new Equipment("e15", "Swift Boots", EquipmentType.MOVEMENT, 10, 10, 50, 80, 7.0));
        eqs.add(new Equipment("e16", "Shadow Cloak", EquipmentType.MOVEMENT, 15, 15, 60, 70, 7.5));
        eqs.add(new Equipment("e17", "Windrider", EquipmentType.MOVEMENT, 5, 5, 40, 100, 8.0));

        // Jungle items (3)
        eqs.add(new Equipment("e18", "Hunter's Machete", EquipmentType.JUNGLE, 60, 10, 0, 20, 7.0));
        eqs.add(new Equipment("e19", "Predator Fang", EquipmentType.JUNGLE, 80, 0, 0, 30, 8.0));
        eqs.add(new Equipment("e20", "Beast Bane", EquipmentType.JUNGLE, 70, 20, 50, 15, 8.5));

        // Set usage counts (simulating some being more popular)
        eqs.get(0).setUsageCount(120);  // Infinity Blade
        eqs.get(1).setUsageCount(95);
        eqs.get(2).setUsageCount(80);
        eqs.get(3).setUsageCount(65);
        eqs.get(4).setUsageCount(45);
        eqs.get(5).setUsageCount(110);  // Titan Shield
        eqs.get(6).setUsageCount(85);
        eqs.get(7).setUsageCount(75);
        eqs.get(8).setUsageCount(55);
        eqs.get(9).setUsageCount(40);
        eqs.get(10).setUsageCount(70);
        eqs.get(11).setUsageCount(60);
        eqs.get(12).setUsageCount(50);
        eqs.get(13).setUsageCount(35);
        eqs.get(14).setUsageCount(90);
        eqs.get(15).setUsageCount(105);
        eqs.get(16).setUsageCount(30);
        eqs.get(17).setUsageCount(25);
        eqs.get(18).setUsageCount(20);
        eqs.get(19).setUsageCount(100);

        return eqs;
    }

    // ==================== Hero-Equipment Compatibility ====================

    /**
     * Assigns compatible equipment to each hero.
     * Each hero gets at least 2 compatible equipment items.
     */
    public static void assignCompatibleEquipment(List<Hero> heroes, List<Equipment> eqs) {
        // Tanks: defense + movement
        assign(heroes.get(0), eqs, 5, 6, 7, 8, 14, 15);    // Xiang Yu
        assign(heroes.get(1), eqs, 5, 6, 9, 14, 16);        // Lian Po
        assign(heroes.get(2), eqs, 7, 8, 10, 15, 17);       // Bai Qi

        // Warriors: attack + defense
        assign(heroes.get(3), eqs, 0, 2, 5, 6, 14);         // Li Bai
        assign(heroes.get(4), eqs, 1, 3, 6, 7, 16);         // Hua Mulan
        assign(heroes.get(5), eqs, 0, 4, 8, 9, 15);         // Lu Bu

        // Assassins: attack + movement + jungle
        assign(heroes.get(6), eqs, 0, 1, 3, 14, 17, 18);    // A Ke
        assign(heroes.get(7), eqs, 2, 4, 15, 18, 19);       // Lanling Wang
        assign(heroes.get(8), eqs, 1, 3, 16, 17, 19);       // Han Xin

        // Mages: magic + movement
        assign(heroes.get(9), eqs, 10, 11, 12, 14, 15);     // Diao Chan
        assign(heroes.get(10), eqs, 10, 12, 13, 16);        // Zhuge Liang

        // Marksmen: attack + movement
        assign(heroes.get(11), eqs, 0, 1, 3, 4, 14, 15);    // Luban No.7
        assign(heroes.get(12), eqs, 0, 2, 4, 15, 16);       // Hou Yi

        // Supports: defense + magic + movement
        assign(heroes.get(13), eqs, 5, 6, 10, 11, 14);      // Cai Wenji
        assign(heroes.get(14), eqs, 7, 8, 12, 13, 15, 16);  // Da Qiao
    }

    /**
     * Helper: assigns equipment items from indices to a hero.
     */
    private static void assign(Hero hero, List<Equipment> eqs, int... indices) {
        for (int i : indices) {
            hero.addCompatibleEquipment(eqs.get(i));
        }
    }

    // ==================== Match Records ====================

    public static List<MatchRecord> createMatchRecords(List<Team> teams) {
        List<MatchRecord> records = new ArrayList<>();
        Team t1 = teams.get(0);
        Team t2 = teams.get(1);
        Team t3 = teams.get(2);

        records.add(new MatchRecord("m01", t1, t2, MatchResult.WIN,  LocalDate.of(2026, 5, 1),  "Ranked", "Xiang Yu, Li Bai, A Ke, Diao Chan, Luban No.7", "Hua Mulan, Lu Bu, Lanling Wang, Zhuge Liang, Hou Yi"));
        records.add(new MatchRecord("m02", t2, t3, MatchResult.WIN,  LocalDate.of(2026, 5, 3),  "Ranked", "Hua Mulan, Lu Bu, Lanling Wang, Zhuge Liang, Hou Yi", "Lian Po, Bai Qi, Han Xin, Cai Wenji, Da Qiao"));
        records.add(new MatchRecord("m03", t1, t3, MatchResult.LOSE, LocalDate.of(2026, 5, 5),  "Ranked", "Xiang Yu, Li Bai, A Ke, Diao Chan, Luban No.7", "Lian Po, Bai Qi, Han Xin, Cai Wenji, Da Qiao"));
        records.add(new MatchRecord("m04", t3, t1, MatchResult.WIN,  LocalDate.of(2026, 5, 7),  "Normal", "Lian Po, Bai Qi, Han Xin, Cai Wenji, Da Qiao", "Xiang Yu, Li Bai, A Ke, Diao Chan, Luban No.7"));
        records.add(new MatchRecord("m05", t2, t1, MatchResult.LOSE, LocalDate.of(2026, 5, 10), "Ranked", "Hua Mulan, Lu Bu, Lanling Wang, Zhuge Liang, Hou Yi", "Xiang Yu, Li Bai, A Ke, Diao Chan, Luban No.7"));
        records.add(new MatchRecord("m06", t3, t2, MatchResult.LOSE, LocalDate.of(2026, 5, 12), "Normal", "Lian Po, Bai Qi, Han Xin, Cai Wenji, Da Qiao", "Hua Mulan, Lu Bu, Lanling Wang, Zhuge Liang, Hou Yi"));
        records.add(new MatchRecord("m07", t1, t2, MatchResult.DRAW, LocalDate.of(2026, 5, 15), "Ranked", "Xiang Yu, Li Bai, A Ke, Diao Chan, Luban No.7", "Hua Mulan, Lu Bu, Lanling Wang, Zhuge Liang, Hou Yi"));
        records.add(new MatchRecord("m08", t2, t3, MatchResult.LOSE, LocalDate.of(2026, 5, 18), "Ranked", "Hua Mulan, Lu Bu, Lanling Wang, Zhuge Liang, Hou Yi", "Lian Po, Bai Qi, Han Xin, Cai Wenji, Da Qiao"));
        records.add(new MatchRecord("m09", t1, t3, MatchResult.WIN,  LocalDate.of(2026, 5, 22), "Normal", "Xiang Yu, Li Bai, A Ke, Diao Chan, Luban No.7", "Lian Po, Bai Qi, Han Xin, Cai Wenji, Da Qiao"));
        records.add(new MatchRecord("m10", t3, t2, MatchResult.WIN,  LocalDate.of(2026, 5, 28), "Ranked", "Lian Po, Bai Qi, Han Xin, Cai Wenji, Da Qiao", "Hua Mulan, Lu Bu, Lanling Wang, Zhuge Liang, Hou Yi"));

        // Update player stats based on match records: increment totalMatches
        // Player stats are pre-set and won't change at this stage

        return records;
    }
}
