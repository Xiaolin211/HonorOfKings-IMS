package hok.model;

import hok.enums.EquipmentType;

/**
 * Represents an equipment item that heroes can use.
 * Each equipment provides stat bonuses and has a rating.
 */
public class Equipment {
    private final String id;
    private String name;
    private EquipmentType equipmentType;
    private int attackBonus;
    private int defenseBonus;
    private int hpBonus;
    private int speedBonus;
    private double rating;
    private int usageCount;

    public Equipment(String id, String name, EquipmentType equipmentType,
                     int attackBonus, int defenseBonus,
                     int hpBonus, int speedBonus, double rating) {
        this.id = id;
        this.name = name;
        this.equipmentType = equipmentType;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.hpBonus = hpBonus;
        this.speedBonus = speedBonus;
        this.rating = rating;
        this.usageCount = 0;
    }

    // === Getters ===

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getHpBonus() {
        return hpBonus;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }

    public double getRating() {
        return rating;
    }

    public int getUsageCount() {
        return usageCount;
    }

    // === Setters ===

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    // === Utility ===

    /** Increments the usage count by 1. */
    public void incrementUsage() {
        this.usageCount++;
    }

    /** Returns the sum of all stat bonuses. */
    public int getTotalBonus() {
        return attackBonus + defenseBonus + hpBonus + speedBonus;
    }

    @Override
    public String toString() {
        return id + " - " + name + " [" + equipmentType + "] Rating: " + rating;
    }
}
