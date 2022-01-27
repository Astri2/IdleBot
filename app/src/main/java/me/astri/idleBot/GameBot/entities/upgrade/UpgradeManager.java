package me.astri.idleBot.GameBot.entities.upgrade;

import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.utils.Utils;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class UpgradeManager { //TODO verifier que le type sert tjr a qqch
    private static final HashMap<String,Upgrade> upgrades = new HashMap<>();
    private static final HashMap<String,LinkedHashMap<String, EquipmentUpgrade>> eqUpgrades = new HashMap<>(); //eq.id -> upgrade.name -> upgrade

    public static void initUpgrades() throws Exception {
        JSONObject JSONUpgrades = new JSONObject(Utils.readFile(Config.get("CONFIG_PATH")+ "upgrades.json"));
        EquipmentUpgrade.init(JSONUpgrades.getJSONObject("EQUIPMENT"),eqUpgrades,upgrades);
    }

    public static HashMap<String,LinkedHashMap<String,EquipmentUpgrade>> getEqUpgrades() {
        return eqUpgrades;
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


