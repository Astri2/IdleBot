package me.astri.idleBot.GameBot.entities.player;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.PlayerChestHunt;
import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.minions.PlayerMinions;
import me.astri.idleBot.GameBot.entities.upgrade.management.PlayerUpgrades;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.utils.Lang;
import me.astri.idleBot.GameBot.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Player extends BotUser {
    private static JSONArray JsonEquipments = null;

    private BigNumber coins;
    private BigNumber sp;
    private BigNumber souls;
    private BigNumber spPrice;
    private long lastUpdateTime;

    public final LinkedHashMap<String, Equipment> equipment = new LinkedHashMap<>();
    private final PlayerUpgrades upgrades;
    private final PlayerMinions minions;
    private final PlayerChestHunt chestHunt;

    public Player(String id, Lang lang, boolean scNotation, String ephemeral) {
        super(id, lang, scNotation, ephemeral);
        coins = new BigNumber(6);
        lastUpdateTime = System.currentTimeMillis();
        upgrades = new PlayerUpgrades(); //has to be done before equipment
        minions = new PlayerMinions();
        chestHunt = new PlayerChestHunt();
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

    public PlayerMinions getMinions() {
        return minions;
    }

    public PlayerChestHunt getChestHunt() {
        return chestHunt;
    }

    public BigNumber getProduction() { //TODO fix
        BigNumber prod = new BigNumber(0);
        getEquipment().values().forEach(eq -> prod.add(eq.getProduction()));
        return BigNumber.multiply(prod,getBoost());
    }

    public BigNumber getCoins() {
        return this.coins;
    } //TODO fix

    public void editCoins(BigNumber coins) {
        this.coins = this.coins.add(coins);
    } //TODO fix

    public void update() {
        long newTime = System.currentTimeMillis();
        BigNumber eqProduction = BigNumber.multiply(this.getProduction(), new BigNumber((newTime - lastUpdateTime)/1000));
        coins.add(eqProduction);//TODO fix
        lastUpdateTime = System.currentTimeMillis();
    }

    public BigNumber getBoost() {
        return BigNumber.add(new BigNumber(1),this.minions.getBoost());
    }
}