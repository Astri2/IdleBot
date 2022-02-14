package me.astri.idleBot.GameBot.entities.upgrade;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.equipments.Equipment;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.utils.Emotes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EquipmentUpgrade extends Upgrade implements Serializable {

    private final int minLvl;
    private final String eq;
    private final int weight;
    private final int boost;

    public EquipmentUpgrade(String name, String icon, BigNumber price, String eq, int minLvl, int weight, int boost) {
        super(name,icon,price);
        this.condition = getCondition(minLvl);
        this.action = getAction(boost);

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

    private Predicate<Object> getCondition(int minLvl) {
        return obj -> {
            Equipment eq = (Equipment)obj;
            return eq.getLevel() >= minLvl;
        };
    }
    private Consumer<Object> getAction(int boost) {
        return obj -> {
            Equipment eq = (Equipment) obj;
            eq.increaseBooster(boost/100);
            eq.updateCurrentUpgrade(this);
        };
    }

    @Override
    protected String getUpgradeTitle(Player p, boolean current) {
        return p.getLang().get("equipment_upg_title",current ? "►":"", this.icon, p.getLang().get(this.name));
    }

    @Override
    protected String getUpgradeDesc(Player p, boolean canAfford) {
        return p.getLang().get("equipment_upg_desc", p.getLang().get(p.getEquipment().get(this.eq).getName()),
                Integer.toString(this.boost),
                canAfford ? Emotes.getEmote("yes") : Emotes.getEmote("no"),
                this.price.getNotation(p.usesScNotation()) + Emotes.getEmote("coin"));
    }

    public static void init(JSONObject JSONEqUpgrades, HashMap<String,LinkedHashMap<String,EquipmentUpgrade>> equipmentUpgrades, HashMap<String,Upgrade> upg) throws Exception {
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