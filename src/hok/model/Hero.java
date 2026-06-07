package hok.model;

import hok.enums.HeroType;
import hok.interfacepkg.Reportable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a playable hero character.
 * Each hero has base stats, a type, and a list of compatible equipment.
 * Demonstrates association with Equipment and interface implementation.
 */
public class Hero implements Reportable {
    private final String id;
    private String name;
    private HeroType heroType;
    private int hp;
    private int attack;
    private int defense;
    private int speed;
    private List<Equipment> compatibleEquipment;

    public Hero(String id, String name, HeroType heroType,
                int hp, int attack, int defense, int speed) {
        this.id = id;
        this.name = name;
        this.heroType = heroType;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.compatibleEquipment = new ArrayList<>();
    }

    // === Getters ===

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HeroType getHeroType() {
        return heroType;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    /**
     * Returns a defensive copy to protect encapsulation.
     */
    public List<Equipment> getCompatibleEquipment() {
        return new ArrayList<>(compatibleEquipment);
    }

    // === Setters ===

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // === Equipment management (association) ===

    public void addCompatibleEquipment(Equipment eq) {
        if (eq != null && !compatibleEquipment.contains(eq)) {
            compatibleEquipment.add(eq);
        }
    }

    public boolean removeCompatibleEquipment(String eqId) {
        return compatibleEquipment.removeIf(e -> e.getId().equals(eqId));
    }

    // === Reportable implementation ===

    @Override
    public String getSummary() {
        return "ID: " + id + " | Name: " + name + " | Type: " + heroType;
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Hero Details =====\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Type: ").append(heroType).append("\n");
        sb.append("HP: ").append(hp).append("\n");
        sb.append("Attack: ").append(attack).append("\n");
        sb.append("Defense: ").append(defense).append("\n");
        sb.append("Speed: ").append(speed).append("\n");

        sb.append("Compatible Equipment (").append(compatibleEquipment.size()).append("):\n");
        for (Equipment e : compatibleEquipment) {
            sb.append("  - ").append(e.getName())
              .append(" (").append(e.getEquipmentType()).append(")\n");
        }
        return sb.toString();
    }
}
