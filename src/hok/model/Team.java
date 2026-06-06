package hok.model;

import hok.interfacepkg.Reportable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a team of players.
 * Demonstrates aggregation: Team contains Players, but Players
 * can exist independently (survive team deletion).
 */
public class Team implements Reportable {
    private String id;
    private String name;
    private List<Player> players;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
        this.players = new ArrayList<>();
    }

    // === Getters ===

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a defensive copy of the player list.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    // === Setters ===

    public void setName(String name) {
        this.name = name;
    }

    // === Player management (aggregation) ===

    /**
     * Adds a player to this team and sets the player's team reference.
     */
    public void addPlayer(Player player) {
        if (player != null && !players.contains(player)) {
            players.add(player);
            player.setTeam(this);
        }
    }

    /**
     * Removes a player from this team and clears the player's team reference.
     */
    public boolean removePlayer(String playerId) {
        for (Player p : players) {
            if (p.getId().equals(playerId)) {
                p.setTeam(null);
                players.remove(p);
                return true;
            }
        }
        return false;
    }

    // === Computed statistics ===

    public double getAverageLevel() {
        if (players.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (Player p : players) {
            total += p.getLevel();
        }
        return (double) total / players.size();
    }

    public double getWinRate() {
        if (players.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Player p : players) {
            total += p.getWinRate();
        }
        return total / players.size();
    }

    /**
     * Returns the player with the highest win rate on this team.
     */
    public Player getTopPlayer() {
        if (players.isEmpty()) {
            return null;
        }
        Player top = players.get(0);
        for (Player p : players) {
            if (p.getWinRate() > top.getWinRate()) {
                top = p;
            }
        }
        return top;
    }

    public int getPlayerCount() {
        return players.size();
    }

    // === Reportable implementation ===

    @Override
    public String getSummary() {
        return "ID: " + id + " | Name: " + name
                + " | Members: " + players.size();
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Team Details =====\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Members: ").append(players.size()).append("\n");
        sb.append("Average Level: ").append(String.format("%.1f", getAverageLevel())).append("\n");
        sb.append("Team Win Rate: ").append(String.format("%.1f%%", getWinRate() * 100)).append("\n");

        Player top = getTopPlayer();
        sb.append("Top Player: ");
        if (top != null) {
            sb.append(top.getName()).append(" (Win Rate: ")
              .append(String.format("%.1f%%", top.getWinRate() * 100)).append(")");
        } else {
            sb.append("None");
        }
        sb.append("\n\nRoster:\n");
        for (Player p : players) {
            sb.append("  ").append(p.getSummary()).append("\n");
        }
        return sb.toString();
    }
}
