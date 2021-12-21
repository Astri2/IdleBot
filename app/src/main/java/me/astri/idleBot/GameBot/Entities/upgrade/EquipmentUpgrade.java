package me.astri.idleBot.GameBot.Entities.upgrade;

import me.astri.idleBot.GameBot.Entities.Number;
import me.astri.idleBot.GameBot.Entities.equipments.Equipment;
import me.astri.idleBot.GameBot.Entities.player.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EquipmentUpgrade extends Upgrade implements Serializable {

    private final int minLvl;
    private final String eq;
    private final int weight;
    private final int boost;

    public EquipmentUpgrade(String name, String icon, Number price, String strType, String eq, int minLvl, int weight, int boost) {
        super(name,icon,price,Type.valueOf(strType),get_condition(minLvl),get_action(eq, boost));
        this.minLvl = minLvl;
        this.eq = eq;
        this.weight = weight;
        this.boost = boost;
    }

    public int getMinLvl() {
        return minLvl;
    }

    public String getEq() {
        return eq;
    }

    public int getWeight() {
        return weight;
    }

    public int getBoost() {
        return boost/100;
    }

    private static Predicate<Object> get_condition(int minLvl) {
        return obj -> {
            Equipment eq = (Equipment)obj;
            return eq.getLevel() >= minLvl;
        };
    }

    private static Consumer<Object> get_action(String strEq, int boost) {
        return obj -> {
            Equipment eq = (Equipment) obj;
            eq.increaseBooster(boost/100);
        };
    }

    public static void init(JSONObject JSONEqUpgrades, HashMap<String,LinkedHashMap<String,EquipmentUpgrade>> equipmentUpgrades) throws Exception {
        JSONArray JSONEqList = JSONEqUpgrades.getJSONArray("eq_list");
        for(int i = 0 ; i < JSONEqList.length() ; i++) {
            String eq = JSONEqList.getString(i);
            LinkedHashMap<String,EquipmentUpgrade> upgrades = new LinkedHashMap<>();
            equipmentUpgrades.put(eq,upgrades);
            JSONArray JSONUpgrades = JSONEqUpgrades.getJSONArray(eq);
            for(int k = 0 ; k < JSONUpgrades.length() ; k++) {
                JSONObject upgrade = JSONUpgrades.getJSONObject(k);
                upgrades.put(upgrade.getString("name"),
                        new EquipmentUpgrade(
                                upgrade.getString("name"),
                                upgrade.getString("icon"),
                                new Number(0),
                                "EQUIPMENT",
                                eq,
                                upgrade.getInt("minLevel"),
                                upgrade.getInt("weight"),
                                upgrade.getInt("boost")
                        )
                );
            }
        }
    }
}