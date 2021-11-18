package me.astri.idleBot.GameBot.Entities.equipments;

import me.astri.idleBot.GameBot.main.Config;
import me.astri.idleBot.GameBot.main.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

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

    public void levelUp(int levels) {
        level+=levels;
        price = price.multiply(BigDecimal.valueOf(Math.pow(1.3,levels)));
        queryLevelUpgrades();
    }

    private void queryLevelUpgrades() {
        for(EquipmentUpgrade upgrade : upgrades.get(this.id)) {
            if(level >= upgrade.getMinLevel() && !ownedUpgrades.contains(upgrade)) {
                addUpgrade(upgrade);
            }
        }
    }

    public void addUpgrade(EquipmentUpgrade upgrade) {
        ownedUpgrades.add(upgrade);
        if(currentUpgrade == null || upgrade.getWeight() > currentUpgrade.getWeight()) {
            currentUpgrade = upgrade;
        }
    }

    public boolean isUnlocked() { return unlocked; }

    public long getLevel() { return level; }

    public String getName() { return currentUpgrade.getName(); }

    public String getEmote() { return currentUpgrade.getEmote(); }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getPrice(int levelToUp) {
        BigDecimal sum = new BigDecimal(0);
        BigDecimal tmp_price = new BigDecimal(String.valueOf(this.price));
        for(int i = 0 ; i <= levelToUp-1 ; i++) { //-1 cause 1.15^0 = current price
            sum = sum.add(tmp_price);
            tmp_price = tmp_price.multiply(BigDecimal.valueOf(1.3));
        }
        return sum;
    }

    public BigDecimal getProduction() {
        return BigDecimal.valueOf(level * baseProduction * getBooster());
    }

    private long getBooster() {
        int totBooster=1;
        for(EquipmentUpgrade upgrade : ownedUpgrades) {
            totBooster*=upgrade.getBoost();
        }
        return totBooster;
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
        return equipments_upgrades;
    }
}