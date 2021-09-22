package me.astri.idleBot.Entities.equipments;

import me.astri.idleBot.main.Config;
import me.astri.idleBot.main.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Equipment implements Serializable {
    private final static HashMap<String,ArrayList<EquipmentUpgrade>> upgrades = initUpgrades(
            new JSONObject(Utils.readFile(Config.get("CONFIG_PATH") + "equipment.json")).getJSONArray("equipment")
    );

    private final String id;
    private final boolean unlocked;
    private final double baseProduction;

    private final ArrayList<EquipmentUpgrade> ownedUpgrades;

    private long level;
    private BigDecimal price;
    private EquipmentUpgrade currentUpgrade;

    public Equipment(JSONObject jsonEquipment) {
        ownedUpgrades = new ArrayList<>();
        level = 0;
        this.id = jsonEquipment.getString("id");
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
        for(EquipmentUpgrade upgrade : upgrades.get(this.id)) {
            if(level >= upgrade.getMinLevel() && !ownedUpgrades.contains(upgrade)) {
                addUpgrade(upgrade);
            }
        }
    }

    public Equipment addUpgrade(EquipmentUpgrade upgrade) {
        ownedUpgrades.add(upgrade);
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

    private static HashMap<String,ArrayList<EquipmentUpgrade>> initUpgrades(JSONArray jsonEquipments) {
        HashMap<String,ArrayList<EquipmentUpgrade>> equipments_upgrades = new HashMap<>();
        for(int i = 0 ; i < jsonEquipments.length() ; i++) {
            ArrayList<EquipmentUpgrade> upgrades = new ArrayList<>();
            JSONArray jsonUpgrades = jsonEquipments.getJSONObject(i).getJSONArray("upgrades");
            for(int k = 0 ; k < jsonUpgrades.length() ; k++) {
                JSONObject jsonUpgrade = jsonUpgrades.getJSONObject(k);
                upgrades.add(new EquipmentUpgrade(
                        jsonUpgrade.getString("name"),
                        jsonUpgrade.getInt("weight"),
                        jsonUpgrade.getInt("minLevel"),
                        jsonUpgrade.getInt("boost"),
                        jsonUpgrade.getString("emote")
                ));
            }
            equipments_upgrades.put(jsonEquipments.getJSONObject(i).getString("id"),upgrades);
        }

        for(Map.Entry<String, ArrayList<EquipmentUpgrade>> entry : equipments_upgrades.entrySet()) {
            System.out.println(entry.getKey());
            for (EquipmentUpgrade upgrade : entry.getValue()) {
                System.out.println("  " + upgrade.getName());
            }
            System.out.println();
        }


        return equipments_upgrades;
    }
}