package me.astri.idleBot.GameBot.Entities.equipments;

import me.astri.idleBot.GameBot.Entities.upgrade.EquipmentUpgrade;
import me.astri.idleBot.GameBot.Entities.upgrade.PlayerUpgrades;
import me.astri.idleBot.GameBot.Entities.upgrade.UpgradeManager;
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
    private EquipmentUpgrade currentUpgrade;
    private int booster;

    public Equipment(JSONObject jsonEquipment, PlayerUpgrades upgrades) {
        level = 0;
        this.id = jsonEquipment.getString("id");
        this.unlocked = jsonEquipment.getBoolean("unlocked");
        this.price = BigDecimal.valueOf(jsonEquipment.getLong("basePrice"));
        this.baseProduction = jsonEquipment.getDouble("baseProduction");
        queryLevelUpgrades(upgrades);
    }

    public void levelUp(int levels, PlayerUpgrades upgrades) {
        level+=levels;
        price = price.multiply(BigDecimal.valueOf(Math.pow(1.3,levels)));
        queryLevelUpgrades(upgrades);
    }

    private void queryLevelUpgrades(PlayerUpgrades p) {
        List<String> upgradesByEq = p.getUnbought().getEq().get(this.id);
        if(upgradesByEq.isEmpty()) return;
        String str_upgrade = upgradesByEq.get(0);
        if(UpgradeManager.getEqUpgrades().get(this.id).get(str_upgrade).meetUnlockCondition(this)) {
            p.getBought().getEq().get(this.id).add(str_upgrade);
            upgradesByEq.remove(str_upgrade);

            EquipmentUpgrade eqUpgrade = UpgradeManager.getEqUpgrades().get(this.id).get(str_upgrade);
            currentUpgrade = (currentUpgrade != null && currentUpgrade.getWeight() > eqUpgrade.getWeight()) ? currentUpgrade : eqUpgrade;
            currentUpgrade.action(this); //apply the booster
            queryLevelUpgrades(p); //loop until you get all of them or you don't have the level requirement
        }
    }

    public boolean isUnlocked() { return unlocked; }

    public long getLevel() { return level; }

    public String getName() { return currentUpgrade.getName(); }

    public String getEmote() { return currentUpgrade.getIcon(); }

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