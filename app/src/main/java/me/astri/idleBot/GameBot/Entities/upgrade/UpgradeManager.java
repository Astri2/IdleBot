package me.astri.idleBot.GameBot.Entities.upgrade;

import me.astri.idleBot.GameBot.main.Config;
import me.astri.idleBot.GameBot.main.Utils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class UpgradeManager { //TODO verifier que le type sert tjr a qqch
    private static final HashMap<String,LinkedHashMap<String,EquipmentUpgrade>> eqUpgrades = new HashMap<>(); //eq.id -> upgrade.name -> upgrade

    public static void initUpgrades() throws Exception {
        JSONObject JSONUpgrades = new JSONObject(Utils.readFile(Config.get("CONFIG_PATH")+ "upgrades.json"));
        EquipmentUpgrade.init(JSONUpgrades.getJSONObject("EQUIPMENT"),eqUpgrades);
    }

    public static HashMap<String,LinkedHashMap<String,EquipmentUpgrade>> getEqUpgrades() {
        return eqUpgrades;
    }
}
