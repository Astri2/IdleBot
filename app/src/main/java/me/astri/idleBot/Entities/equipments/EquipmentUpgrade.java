package me.astri.idleBot.Entities.equipments;

import java.io.Serializable;

public class EquipmentUpgrade implements Serializable {
    private final String name;
    private final int weight;
    private final int minLevel;
    private final int boost;
    private final String emote;

    public EquipmentUpgrade(String name, int weight, int minLevel, int boost, String emote) {
        this.name = name;
        this.weight = weight;
        this.minLevel = minLevel;
        this.boost = boost;
        this.emote = emote;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getBoost() {
        return boost/100;
    }

    public String getEmote() {
        return emote;
    }

}
