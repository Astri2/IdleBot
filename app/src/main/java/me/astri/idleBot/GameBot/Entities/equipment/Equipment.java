package me.astri.idleBot.GameBot.entities.equipment;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.upgrade.EquipmentUpgrade;
import me.astri.idleBot.GameBot.entities.upgrade.PlayerUpgrades;
import me.astri.idleBot.GameBot.entities.upgrade.UpgradeManager;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class Equipment implements Serializable {
    private static final BigNumber PRICE_MULTIPLIER = new BigNumber(1.3);

    private final String id;
    private final boolean unlocked;
    private final BigNumber baseProduction;

    private long level;
    private final BigNumber price;
    private String currentIcon;
    private String currentUpgrade;
    private int currentWeight;
    private int booster;

    public Equipment(JSONObject jsonEquipment) {
        level = 0;
        this.id = jsonEquipment.getString("id");
        this.unlocked = jsonEquipment.getBoolean("unlocked");
        this.price = new BigNumber(jsonEquipment.getString("basePrice"));
        this.baseProduction = new BigNumber(jsonEquipment.getString("baseProduction"));
        this.currentIcon = jsonEquipment.getString("baseIcon");
        currentUpgrade = this.id;
        currentWeight = 0;
    }

    public void levelUp(int levels, PlayerUpgrades upgrades) {
        level+=levels;
        price.multiply(BigNumber.pow(PRICE_MULTIPLIER,new BigNumber(levels)));
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

    public BigNumber getPrice() {
        return this.price;
    }

    public BigNumber getPrice(int levelToUp) {
        BigNumber sum = new BigNumber(0);
        BigNumber tmp_price = new BigNumber(this.price);
        for(int i = 0 ; i <= levelToUp-1 ; i++) { //-1 cause 1.15^0 = current price
            sum.add(tmp_price);
            tmp_price = tmp_price.multiply(PRICE_MULTIPLIER);
        }
        return sum;
    }

    public BigNumber getProduction() {
        return BigNumber.multiply(new BigNumber(level * (1 + this.getBooster())),baseProduction);
    }

    private long getBooster() {
        return booster;
    }

    public void increaseBooster(int boost) {
        booster+=boost;
    }
}