package me.astri.idleBot.GameBot.entities.equipments;

import me.astri.idleBot.GameBot.entities.upgrade.EquipmentUpgrade;
import me.astri.idleBot.GameBot.entities.upgrade.PlayerUpgrades;
import me.astri.idleBot.GameBot.entities.upgrade.UpgradeManager;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Equipment implements Serializable {
    private final String id;
    private final boolean unlocked;
    private final double baseProduction;

    private long level;
    private BigDecimal price;
    private String currentIcon;
    private String currentUpgrade;
    private int currentWeight;
    private int booster;

    public Equipment(JSONObject jsonEquipment, PlayerUpgrades upgrades) throws Exception {
        level = 0;
        this.id = jsonEquipment.getString("id");
        this.unlocked = jsonEquipment.getBoolean("unlocked");
        this.price = BigDecimal.valueOf(jsonEquipment.getLong("basePrice"));
        this.baseProduction = jsonEquipment.getDouble("baseProduction");
        this.currentIcon = jsonEquipment.getString("baseIcon");
        currentUpgrade = this.id;
        currentWeight = 0;
    }

    public void levelUp(int levels, PlayerUpgrades upgrades) {
        level+=levels;
        price = price.multiply(BigDecimal.valueOf(Math.pow(1.3,levels)));
        queryLevelUpgrades(upgrades);
    }

    private void queryLevelUpgrades(PlayerUpgrades p) {
        List<String> upgradesByEq = p.getUnbought().getEq().get(this.id);
        if(upgradesByEq.isEmpty()) return; //you already own all upgrades of that eq
        String str_upgrade = upgradesByEq.get(0);
        if(UpgradeManager.getEqUpgrades().get(this.id).get(str_upgrade).meetUnlockCondition(this)) {
            p.getAvailable().add(str_upgrade);
            upgradesByEq.remove(str_upgrade);
            queryLevelUpgrades(p); //loop until you get all of them or you don't have the level requirement
        }
    }

    public void updateCurrentUpgrade(EquipmentUpgrade newUpgrade) {
        if(currentWeight < newUpgrade.getWeight()) {
            currentUpgrade = newUpgrade.getName();
            currentIcon = newUpgrade.getIcon();
            currentWeight = newUpgrade.getWeight();
        }
    }

    public boolean isUnlocked() { return unlocked; }

    public long getLevel() { return level; }

    public String getName() { return currentUpgrade; }

    public String getEmote() { return currentIcon; }

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
        return BigDecimal.valueOf(level * baseProduction * (1 + getBooster()));
    }

    private long getBooster() {
        return booster;
    }

    public void increaseBooster(int boost) {
        booster+=boost;
    }
}