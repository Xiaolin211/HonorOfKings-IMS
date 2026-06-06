package hok.model;

import hok.enums.Role;
import hok.interfacepkg.Reportable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game player. Extends Person with game-specific attributes.
 * Demonstrates inheritance (extends Person), interface implementation (Reportable),
 * composition (owns Heroes), and aggregation (belongs to Team).
 */
public class Player extends Person implements Reportable {
    private int level;
    private double winRate;
    private int totalMatches;
    private List<Hero> heroes;
    private Team team;
    private String password;

    public Player(String id, String name, String password) {
        super(id, name, Role.PLAYER);
        this.password = password;
        this.level = 1;
        this.winRate = 0.0;
        this.totalMatches = 0;
        this.heroes = new ArrayList<>();
        this.team = null;
    }

    // === Getters ===

    public int getLevel() {
        return level;
    }

    public double getWinRate() {
        return winRate;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    /**
     * Returns a defensive copy of the hero list to preserve encapsulation.
     */
    public List<Hero> getHeroes() {
        return new ArrayList<>(heroes);
    }

    public Team getTeam() {
        return team;
    }

    public String getPassword() {
        return password;
    }

    // === Setters ===

    public void setLevel(int level) {
        this.level = level;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // === Hero management (composition) ===

    public void addHero(Hero hero) {
        if (hero != null && !heroes.contains(hero)) {
            heroes.add(hero);
        }
    }

    public boolean removeHero(String heroId) {
        return heroes.removeIf(h -> h.getId().equals(heroId));
    }

    // === Reportable implementation ===

    @Override
    public String getSummary() {
        return "ID: " + getId() + " | Name: " + getName()
                + " | Level: " + level + " | Win Rate: "
                + String.format("%.1f%%", winRate * 100);
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Player Details =====\n");
        sb.append("ID: ").append(getId()).append("\n");
        sb.append("Name: ").append(getName()).append("\n");
        sb.append("Level: ").append(level).append("\n");
        sb.append("Win Rate: ").append(String.format("%.1f%%", winRate * 100)).append("\n");
        sb.append("Total Matches: ").append(totalMatches).append("\n");

        sb.append("Team: ");
        if (team != null) {
            sb.append(team.getName());
        } else {
            sb.append("None");
        }
        sb.append("\n");

        sb.append("Owned Heroes (").append(heroes.size()).append("):\n");
        for (Hero h : heroes) {
            sb.append("  - ").append(h.getName())
              .append(" (").append(h.getHeroType()).append(")\n");
        }
        return sb.toString();
    }
}
