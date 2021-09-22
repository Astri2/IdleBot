package me.astri.idleBot.Entities.player;

import me.astri.idleBot.Entities.equipments.Equipment;
import me.astri.idleBot.main.Config;
import me.astri.idleBot.main.Lang;
import me.astri.idleBot.main.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Player extends BotUser implements Serializable {
    private static JSONArray JsonEquipments = null;

    private BigDecimal coins;
    private BigDecimal sp;
    private BigDecimal souls;
    private BigDecimal spPrice;
    private long lastUpdateTime;

    private final LinkedHashMap<String, Equipment> equipments = new LinkedHashMap<>();

    public Player(String id, Lang lang, boolean scNotation, String ephemeral) {
        super(id, lang, scNotation, ephemeral);
        coins = new BigDecimal(120000);
        lastUpdateTime = System.currentTimeMillis();
        initEquipments();
        equipments.get("sword").levelUp(5);
    }

    private void initEquipments() {
        if(JsonEquipments == null) //read only once
            JsonEquipments = new JSONObject(Utils.readFile(Config.get("CONFIG_PATH") + "equipment.json")).getJSONArray("equipment");
        for(int i = 0 ; i < JsonEquipments.length() ; i++) {
            JSONObject JsonEquipment = JsonEquipments.getJSONObject(i);
            equipments.put(JsonEquipment.getString("id"),new Equipment(JsonEquipment));
        }
    }

    public HashMap<String, Equipment> getEquipment() {
        return equipments;
    }

    public BigDecimal getProduction() {
        AtomicReference<BigDecimal> prod = new AtomicReference<>(new BigDecimal(0));
        getEquipment().values().forEach(eq -> prod.set(prod.get().add(eq.getProduction())));
        return prod.get();
    }

    public BigDecimal getCoins() {
        return coins;
    }

    public Player editCoins(BigDecimal coins) {
        this.coins = this.coins.add(coins);
        return this;
    }

    public Player resetCoins() {
        this.coins = new BigDecimal(0);
        return this;
    }

    public Player update() {
        long newTime = System.currentTimeMillis();
        coins = coins.add(getProduction().multiply(new BigDecimal((newTime - lastUpdateTime)/1000)));
        lastUpdateTime = System.currentTimeMillis();
        return this;
    }
}