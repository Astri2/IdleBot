package me.astri.idleBot.GameBot.entities.player;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.minions.Minion;
import me.astri.idleBot.GameBot.entities.upgrade.PlayerUpgrades;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.utils.Lang;
import me.astri.idleBot.GameBot.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Player extends BotUser implements Serializable {
    private static JSONArray JsonEquipments = null;

    private BigNumber coins;
    private BigNumber sp;
    private BigNumber souls;
    private BigNumber spPrice;
    private long lastUpdateTime;

    private final LinkedHashMap<String, Equipment> equipment = new LinkedHashMap<>();
    private final PlayerUpgrades upgrades;
    public ArrayList<Minion> minions; //TODO private final

    public Player(String id, Lang lang, boolean scNotation, String ephemeral) {
        super(id, lang, scNotation, ephemeral);
        coins = new BigNumber(6);
        lastUpdateTime = System.currentTimeMillis();
        upgrades = new PlayerUpgrades(); //has to be done before equipment
        minions = Minion.initMinions();
        initEquipments();
    }

    private void initEquipments() {
        if(JsonEquipments == null) //read only once
            JsonEquipments = new JSONObject(Utils.readFile(Config.get("CONFIG_PATH") + "equipment.json")).getJSONArray("equipment");
        for(int i = 0 ; i < JsonEquipments.length() ; i++) {
            JSONObject JsonEquipment = JsonEquipments.getJSONObject(i);
            equipment.put(JsonEquipment.getString("id"),new Equipment(JsonEquipment));
        }
    }

    public HashMap<String, Equipment> getEquipment() {
        return equipment;
    }

    public PlayerUpgrades getUpgrades() {
        return upgrades;
    }

    public ArrayList<Minion> getMinions() {
        return minions;
    }

    public BigNumber getProduction() { //TODO fix
        BigNumber prod = new BigNumber(0);
        getEquipment().values().forEach(eq -> prod.add(eq.getProduction()));
        return prod;
    }

    public BigNumber getCoins() {
        return this.coins;
    } //TODO fix

    public void editCoins(BigNumber coins) {
        this.coins = this.coins.add(coins);
    } //TODO fix

    public void update() {
        long newTime = System.currentTimeMillis();
        coins.add(BigNumber.multiply(this.getProduction(), new BigNumber((newTime - lastUpdateTime)/1000)));//TODO fix
        lastUpdateTime = System.currentTimeMillis();
    }
}