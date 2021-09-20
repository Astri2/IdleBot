package me.astri.idleBot.Entities.equipments;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Equipment {

    private final boolean unlocked;
    private final double baseProduction;

    private final CopyOnWriteArrayList<EquipmentUpgrade> unboughtUpgrades;
    private final ArrayList<EquipmentUpgrade> ownedUpgrades;

    private long level;
    private BigDecimal price;
    private EquipmentUpgrade currentUpgrade;

    public Equipment(JSONObject jsonEquipment) {
        unboughtUpgrades = new CopyOnWriteArrayList<>(initUpgrades(jsonEquipment));
        ownedUpgrades = new ArrayList<>();
        level = 0;
        this.unlocked = jsonEquipment.getBoolean("unlocked");
        this.price = BigDecimal.valueOf(jsonEquipment.getLong("basePrice"));
        this.baseProduction = jsonEquipment.getDouble("baseProduction");
        queryLevelUpgrades();
    }

    public Equipment levelUp(int levels) {
        level+=levels;
        price = price.multiply(BigDecimal.valueOf(Math.pow(1.15,levels)));
        queryLevelUpgrades();
        return this;
    }

    private void queryLevelUpgrades() {
        for(EquipmentUpgrade upgrade : unboughtUpgrades) {
            if(level >= upgrade.getMinLevel()) {
                addUpgrade(upgrade);
            }
        }
    }

    public Equipment addUpgrade(EquipmentUpgrade upgrade) {
        ownedUpgrades.add(upgrade);
        unboughtUpgrades.remove(upgrade);
        if(currentUpgrade == null || upgrade.getWeight() > currentUpgrade.getWeight()) {
            currentUpgrade = upgrade;
        }
        return this;
    }

    public boolean isUnlocked() { return unlocked; }

    public long getLevel() { return level; }

    public String getName() { return currentUpgrade.getName(); }

    public String getEmote() { return currentUpgrade.getEmote(); }

    public BigDecimal getPrice() {
        return getPrice(1);
    }
    public BigDecimal getPrice(int levelToUp) {
        return price.multiply(BigDecimal.valueOf(Math.pow(1.15,levelToUp-1)));
    }

    public BigDecimal getProduction() {
        return BigDecimal.valueOf(level * baseProduction * (1 + getBooster()));
    }

    private long getBooster() {
        return ownedUpgrades.stream().mapToLong(EquipmentUpgrade::getBoost).sum();
    }

    private ArrayList<EquipmentUpgrade> initUpgrades(JSONObject jsonEquipment) {
        ArrayList<EquipmentUpgrade> upgrades = new ArrayList<>();
        JSONArray jsonUpgrades = jsonEquipment.getJSONArray("upgrades");
        for(int i = 0 ; i < jsonUpgrades.length() ; i++) {
            JSONObject jsonUpgrade = jsonUpgrades.getJSONObject(i);
            upgrades.add(new EquipmentUpgrade(
                  jsonUpgrade.getString("name"),
                  jsonUpgrade.getInt("weight"),
                  jsonUpgrade.getInt("minLevel"),
                  jsonUpgrade.getInt("boost"),
                  jsonUpgrade.getString("emote")
            ));
        }
        return upgrades;
    }
}