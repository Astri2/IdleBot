package me.astri.idleBot.GameBot.Entities.upgrade;

import me.astri.idleBot.GameBot.Entities.Number;
import me.astri.idleBot.GameBot.Entities.equipments.Equipment;
import me.astri.idleBot.GameBot.Entities.player.Player;
import me.astri.idleBot.GameBot.main.Emotes;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

    public EquipmentUpgrade(String name, String icon, Number price, String strType, String eq, int minLvl, int weight, int boost) {
        super(name,icon,price,Type.valueOf(strType),getCondition(minLvl),getAction(boost));
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

    private static Predicate<Object> getCondition(int minLvl) {
        return obj -> {
            Equipment eq = (Equipment)obj;
            return eq.getLevel() >= minLvl;
        };
    }
    private static Consumer<Object> getAction(int boost) {
        return obj -> {
            Equipment eq = (Equipment) obj;
            eq.increaseBooster(boost/100);
        };
    }

    @Override
    public MessageEmbed.Field getUpgradeField(Player p, boolean current, boolean canAfford) {
        return new MessageEmbed.Field(
                p.getLang().get(this.type.toString() + "_upg_title",current ? "►":"", this.icon, p.getLang().get(this.name)),
                p.getLang().get(this.type.toString() + "_upg_desc", this.eq,
                        Integer.toString(this.boost),
                        canAfford ? Emotes.getEmote("yes") : Emotes.getEmote("no"),
                        this.price.getNotation(p.isUseScNotation())+ Emotes.getEmote("coin")),
                true);
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
                        new Number(upgrade.getInt("price")),
                        "EQUIPMENT",
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