package hok.storage;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CSV file persistence for all entity types.
 * Files are stored in the data/ directory.
 * All load methods handle missing files gracefully — returning empty lists,
 * never crashing the application.
 */
public class FileStorageService {

    private static final String DATA_DIR = "data";
    private static final String SEPARATOR = ",";

    // ==================== Save All ====================

    /**
     * Saves all entities to CSV files. Returns true if all operations succeed.
     */
    public boolean saveAll(List<Player> players, List<Admin> admins,
                           List<Hero> heroes, List<Equipment> equipment,
                           List<Team> teams, List<MatchRecord> matches) {
        ensureDataDir();
        boolean ok = true;
        ok &= savePlayers(players);
        ok &= saveAdmins(admins);
        ok &= saveHeroes(heroes);
        ok &= saveEquipment(equipment);
        ok &= saveTeams(teams);
        ok &= saveMatchRecords(matches);
        return ok;
    }

    // ==================== Load All ====================

    /**
     * Container for all loaded data. Fields are public for convenience.
     */
    public static class LoadedData {
        public List<Player> players = new ArrayList<>();
        public List<Admin> admins = new ArrayList<>();
        public List<Hero> heroes = new ArrayList<>();
        public List<Equipment> equipment = new ArrayList<>();
        public List<Team> teams = new ArrayList<>();
        public List<MatchRecord> matches = new ArrayList<>();
    }

    /**
     * Loads all entities from CSV files. Missing files are handled gracefully.
     */
    public LoadedData loadAll() {
        LoadedData data = new LoadedData();
        data.players = loadPlayers();
        data.admins = loadAdmins();
        data.heroes = loadHeroes();
        data.equipment = loadEquipment();
        data.teams = loadTeams(data.players);
        data.matches = loadMatchRecords(data.teams);
        // Restore Player→Hero and Hero→Equipment relationships
        restorePlayerHeroRelationships(data.players, data.heroes);
        restoreHeroEquipmentRelationships(data.heroes, data.equipment);
        return data;
    }

    // ==================== Players CSV ====================
    // Format: id,name,role,level,winRate,totalMatches,teamId,heroIds,password

    private boolean savePlayers(List<Player> players) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + "/players.csv"))) {
            pw.println("id,name,role,level,winRate,totalMatches,teamId,heroIds,password");
            for (Player p : players) {
                StringBuilder heroIds = new StringBuilder();
                for (Hero h : p.getHeroes()) {
                    if (heroIds.length() > 0) heroIds.append(";");
                    heroIds.append(h.getId());
                }
                String teamId = (p.getTeam() != null) ? p.getTeam().getId() : "";
                pw.println(p.getId() + SEPARATOR + p.getName() + SEPARATOR + p.getRole()
                        + SEPARATOR + p.getLevel() + SEPARATOR + p.getWinRate()
                        + SEPARATOR + p.getTotalMatches() + SEPARATOR + teamId
                        + SEPARATOR + heroIds + SEPARATOR + p.getPassword());
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving players: " + e.getMessage());
            return false;
        }
    }

    private List<Player> loadPlayers() {
        List<Player> players = new ArrayList<>();
        File file = new File(DATA_DIR + "/players.csv");
        if (!file.exists()) {
            System.out.println("players.csv not found — skipping.");
            return players;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length < 9) continue;
                Player p = new Player(parts[0], parts[1], parts[8]);
                p.setLevel(Integer.parseInt(parts[3]));
                p.setWinRate(Double.parseDouble(parts[4]));
                p.setTotalMatches(Integer.parseInt(parts[5]));
                // teamId[6] and heroIds[7] are restored after loading teams and heroes
                players.add(p);
            }
        } catch (IOException e) {
            System.out.println("Error loading players: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing player data: " + e.getMessage());
        }
        return players;
    }

    // ==================== Admins CSV ====================
    // Format: id,name,role,permissionLevel,password

    private boolean saveAdmins(List<Admin> admins) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + "/admins.csv"))) {
            pw.println("id,name,role,permissionLevel,password");
            for (Admin a : admins) {
                pw.println(a.getId() + SEPARATOR + a.getName() + SEPARATOR + a.getRole()
                        + SEPARATOR + a.getPermissionLevel() + SEPARATOR + a.getPassword());
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving admins: " + e.getMessage());
            return false;
        }
    }

    private List<Admin> loadAdmins() {
        List<Admin> admins = new ArrayList<>();
        File file = new File(DATA_DIR + "/admins.csv");
        if (!file.exists()) return admins;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length < 5) continue;
                Admin a = new Admin(parts[0], parts[1], parts[4]);
                a.setPermissionLevel(Integer.parseInt(parts[3]));
                admins.add(a);
            }
        } catch (IOException e) {
            System.out.println("Error loading admins: " + e.getMessage());
        }
        return admins;
    }

    // ==================== Heroes CSV ====================
    // Format: id,name,heroType,hp,attack,defense,speed,compatibleEquipmentIds

    private boolean saveHeroes(List<Hero> heroes) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + "/heroes.csv"))) {
            pw.println("id,name,heroType,hp,attack,defense,speed,compatibleEquipmentIds");
            for (Hero h : heroes) {
                StringBuilder eqIds = new StringBuilder();
                for (Equipment e : h.getCompatibleEquipment()) {
                    if (eqIds.length() > 0) eqIds.append(";");
                    eqIds.append(e.getId());
                }
                pw.println(h.getId() + SEPARATOR + h.getName() + SEPARATOR + h.getHeroType()
                        + SEPARATOR + h.getHp() + SEPARATOR + h.getAttack()
                        + SEPARATOR + h.getDefense() + SEPARATOR + h.getSpeed()
                        + SEPARATOR + eqIds);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving heroes: " + e.getMessage());
            return false;
        }
    }

    private List<Hero> loadHeroes() {
        List<Hero> heroes = new ArrayList<>();
        File file = new File(DATA_DIR + "/heroes.csv");
        if (!file.exists()) return heroes;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length < 8) continue;
                HeroType type = HeroType.valueOf(parts[2]);
                Hero h = new Hero(parts[0], parts[1], type,
                        Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
                // compatibleEquipmentIds[7] restored later
                heroes.add(h);
            }
        } catch (IOException e) {
            System.out.println("Error loading heroes: " + e.getMessage());
        }
        return heroes;
    }

    // ==================== Equipment CSV ====================
    // Format: id,name,equipmentType,attackBonus,defenseBonus,hpBonus,speedBonus,rating,usageCount

    private boolean saveEquipment(List<Equipment> eqs) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + "/equipment.csv"))) {
            pw.println("id,name,equipmentType,attackBonus,defenseBonus,hpBonus,speedBonus,rating,usageCount");
            for (Equipment e : eqs) {
                pw.println(e.getId() + SEPARATOR + e.getName() + SEPARATOR + e.getEquipmentType()
                        + SEPARATOR + e.getAttackBonus() + SEPARATOR + e.getDefenseBonus()
                        + SEPARATOR + e.getHpBonus() + SEPARATOR + e.getSpeedBonus()
                        + SEPARATOR + e.getRating() + SEPARATOR + e.getUsageCount());
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving equipment: " + e.getMessage());
            return false;
        }
    }

    private List<Equipment> loadEquipment() {
        List<Equipment> eqs = new ArrayList<>();
        File file = new File(DATA_DIR + "/equipment.csv");
        if (!file.exists()) return eqs;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length < 9) continue;
                EquipmentType type = EquipmentType.valueOf(parts[2]);
                Equipment e = new Equipment(parts[0], parts[1], type,
                        Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                        Double.parseDouble(parts[7]));
                e.setUsageCount(Integer.parseInt(parts[8]));
                eqs.add(e);
            }
        } catch (IOException ex) {
            System.out.println("Error loading equipment: " + ex.getMessage());
        }
        return eqs;
    }

    // ==================== Teams CSV ====================
    // Format: id,name,playerIds

    private boolean saveTeams(List<Team> teams) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + "/teams.csv"))) {
            pw.println("id,name,playerIds");
            for (Team t : teams) {
                StringBuilder pIds = new StringBuilder();
                for (Player p : t.getPlayers()) {
                    if (pIds.length() > 0) pIds.append(";");
                    pIds.append(p.getId());
                }
                pw.println(t.getId() + SEPARATOR + t.getName() + SEPARATOR + pIds);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving teams: " + e.getMessage());
            return false;
        }
    }

    private List<Team> loadTeams(List<Player> players) {
        List<Team> teams = new ArrayList<>();
        File file = new File(DATA_DIR + "/teams.csv");
        if (!file.exists()) return teams;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length < 3) continue;
                Team t = new Team(parts[0], parts[1]);
                // Restore player references
                if (parts.length > 2 && !parts[2].isEmpty()) {
                    String[] pIds = parts[2].split(";");
                    for (String pid : pIds) {
                        for (Player p : players) {
                            if (p.getId().equals(pid)) {
                                t.addPlayer(p);
                                break;
                            }
                        }
                    }
                }
                teams.add(t);
            }
        } catch (IOException e) {
            System.out.println("Error loading teams: " + e.getMessage());
        }
        return teams;
    }

    // ==================== Match Records CSV ====================
    // Format: id,teamAId,teamBId,result,matchDate,matchType,heroPicksA,heroPicksB

    private boolean saveMatchRecords(List<MatchRecord> records) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + "/matchrecords.csv"))) {
            pw.println("id,teamAId,teamBId,result,matchDate,matchType,heroPicksA,heroPicksB");
            for (MatchRecord m : records) {
                String picksA = (m.getHeroPicksA() != null) ? m.getHeroPicksA() : "";
                String picksB = (m.getHeroPicksB() != null) ? m.getHeroPicksB() : "";
                pw.println(m.getId() + SEPARATOR + m.getTeamA().getId()
                        + SEPARATOR + m.getTeamB().getId() + SEPARATOR + m.getResult()
                        + SEPARATOR + m.getMatchDateString() + SEPARATOR + m.getMatchType()
                        + SEPARATOR + picksA + SEPARATOR + picksB);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving match records: " + e.getMessage());
            return false;
        }
    }

    private List<MatchRecord> loadMatchRecords(List<Team> teams) {
        List<MatchRecord> records = new ArrayList<>();
        File file = new File(DATA_DIR + "/matchrecords.csv");
        if (!file.exists()) return records;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length < 6) continue;
                Team teamA = findTeamById(teams, parts[1]);
                Team teamB = findTeamById(teams, parts[2]);
                if (teamA == null || teamB == null) continue;
                MatchResult result = MatchResult.valueOf(parts[3]);
                String picksA = (parts.length > 6) ? parts[6] : "";
                String picksB = (parts.length > 7) ? parts[7] : "";
                MatchRecord m = new MatchRecord(parts[0], teamA, teamB, result, LocalDate.parse(parts[4]), parts[5], picksA, picksB);
                records.add(m);
            }
        } catch (IOException e) {
            System.out.println("Error loading match records: " + e.getMessage());
        }
        return records;
    }

    private Team findTeamById(List<Team> teams, String id) {
        for (Team t : teams) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    private Hero findHeroById(List<Hero> heroes, String id) {
        for (Hero h : heroes) {
            if (h.getId().equals(id)) return h;
        }
        return null;
    }

    private Equipment findEquipmentById(List<Equipment> equipments, String id) {
        for (Equipment e : equipments) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    /**
     * Restores Player→Hero relationships after loading all data.
     */
    private void restorePlayerHeroRelationships(List<Player> players, List<Hero> heroes) {
        File file = new File(DATA_DIR + "/players.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length < 8) continue;
                String playerId = parts[0];
                String heroIds = parts[7];
                if (heroIds.isEmpty()) continue;
                
                // Find the player
                Player player = null;
                for (Player p : players) {
                    if (p.getId().equals(playerId)) {
                        player = p;
                        break;
                    }
                }
                if (player == null) continue;
                
                // Restore hero references
                String[] ids = heroIds.split(";");
                for (String hid : ids) {
                    Hero hero = findHeroById(heroes, hid);
                    if (hero != null) {
                        player.addHero(hero);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error restoring player-hero relationships: " + e.getMessage());
        }
    }

    /**
     * Restores Hero→Equipment relationships after loading all data.
     */
    private void restoreHeroEquipmentRelationships(List<Hero> heroes, List<Equipment> equipments) {
        File file = new File(DATA_DIR + "/heroes.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length < 8) continue;
                String heroId = parts[0];
                String eqIds = parts[7];
                if (eqIds.isEmpty()) continue;
                
                // Find the hero
                Hero hero = null;
                for (Hero h : heroes) {
                    if (h.getId().equals(heroId)) {
                        hero = h;
                        break;
                    }
                }
                if (hero == null) continue;
                
                // Restore equipment references
                String[] ids = eqIds.split(";");
                for (String eid : ids) {
                    Equipment eq = findEquipmentById(equipments, eid);
                    if (eq != null) {
                        hero.addCompatibleEquipment(eq);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error restoring hero-equipment relationships: " + e.getMessage());
        }
    }

    // ==================== Utility ====================

    private void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
}
