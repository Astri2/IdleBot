package me.astri.idleBot.GameBot.entities.upgrade.management;

import me.astri.idleBot.GameBot.entities.upgrade.conditional.EquipmentUpgrade;
import me.astri.idleBot.GameBot.entities.upgrade.Upgrade;
import me.astri.idleBot.GameBot.entities.upgrade.unconditional.UnconditionalUpgrade;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.utils.Utils;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class UpgradeManager { //TODO verifier que le type sert tjr a qqch
    private static final HashMap<String, Upgrade> upgrades = new HashMap<>();
    private static final HashMap<String,LinkedHashMap<String, EquipmentUpgrade>> eqUpgrades = new HashMap<>(); //eq.id -> upgrade.name -> upgrade
    private static final HashMap<String, UnconditionalUpgrade> unConditionalUpgrades = new HashMap<>();

    public static void initUpgrades() {
        JSONObject JSONUpgrades = new JSONObject(Utils.readFile(Config.get("CONFIG_PATH")+ "upgrades.json"));
        EquipmentUpgrade.init(JSONUpgrades.getJSONObject("EQUIPMENT"),eqUpgrades,upgrades);
        UnconditionalUpgrade.init(JSONUpgrades.getJSONArray("OTHER"),unConditionalUpgrades,upgrades);
    }

    public static HashMap<String,LinkedHashMap<String,EquipmentUpgrade>> getEqUpgrades() {
        return eqUpgrades;
    }

    public static HashMap<String,UnconditionalUpgrade> getUnConditionalUpgrades() {
        return unConditionalUpgrades;
    }

    public static HashMap<String,Upgrade> getUpgrades() {
        return upgrades;
    }

    public static class PriceComparator implements Comparator<Upgrade> {
        @Override
        public int compare(Upgrade o1, Upgrade o2) {
            return o1.getPrice().compareTo(o2.getPrice());
        }
    }
}


