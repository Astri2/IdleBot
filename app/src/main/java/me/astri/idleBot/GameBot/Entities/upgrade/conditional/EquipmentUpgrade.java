package me.astri.idleBot.GameBot.entities.upgrade.conditional;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.entities.upgrade.Upgrade;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EquipmentUpgrade extends ConditionalUpgrade {

    private final int minLvl;
    private final String eq;
    private final int weight;
    private final int boost;

    public EquipmentUpgrade(String name, String icon, BigNumber price, String eq, int minLvl, int weight, int boost) {
        super(name,icon,price);
        this.minLvl = minLvl;
        this.eq = eq;
        this.weight = weight;
        this.boost = boost;

        this.condition = getCondition();
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

    @Override
    protected Predicate<Object> getCondition() {
        return obj -> {
            Equipment eq = (Equipment)obj;
            return eq.getLevel() >= this.minLvl;
        };
    }

    @Override
    protected Consumer<Object> getAction() {
        return obj -> {
            Equipment eq = (Equipment) obj;
            eq.increaseBooster(this.boost/100);
            eq.updateCurrentUpgrade(this);
        };
    }

    @Override
    protected String getTitleId() {
        return "equipment_upg_title";
    }

    @Override
    protected String getDescId() {
        return "equipment_upg_desc";
    }

    @Override
    protected String[] getDescArgs(Player p) {
        return new String[]{
                p.getLang().get(p.getEquipment().get(this.eq).getName()),
                Integer.toString(this.boost)
        };
    }

    public static void init(JSONObject JSONEqUpgrades, HashMap<String,LinkedHashMap<String,EquipmentUpgrade>> equipmentUpgrades, HashMap<String, Upgrade> upg) {
        JSONArray JSONEqList = JSONEqUpgrades.getJSONArray("eq_list");
        for(int i = 0 ; i < JSONEqList.length() ; i++) {
            String eq = JSONEqList.getString(i);
            LinkedHashMap<String,EquipmentUpgrade> upgrades = new LinkedHashMap<>();
            equipmentUpgrades.put(eq,upgrades);
            JSONArray JSONUpgrades = JSONEqUpgrades.getJSONArray(eq);
            for(int k = 0 ; k < JSONUpgrades.length() ; k++) {
                JSONObject upgrade = JSONUpgrades.getJSONObject(k);
                EquipmentUpgrade eqUp =  new EquipmentUpgrade(
                        upgrade.getString("name"),
                        upgrade.getString("icon"),
                        new BigNumber(upgrade.getString("price")),
                        eq,
                        upgrade.getInt("minLevel"),
                        upgrade.getInt("weight"),
                        upgrade.getInt("boost")
                    );
                upgrades.put(upgrade.getString("name"), eqUp);
                upg.put(upgrade.getString("name"), eqUp);
            }
        }
    }
}