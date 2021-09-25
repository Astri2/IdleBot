package me.astri.idleBot.GameBot.Entities.player;

import me.astri.idleBot.GameBot.Entities.equipments.Equipment;
import me.astri.idleBot.GameBot.main.Config;
import me.astri.idleBot.GameBot.main.Lang;
import me.astri.idleBot.GameBot.main.Utils;
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
        coins = new BigDecimal(6);
        lastUpdateTime = System.currentTimeMillis();
        initEquipments();
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

    public void editCoins(BigDecimal coins) {
        this.coins = this.coins.add(coins);
    }

    public void update() {
        long newTime = System.currentTimeMillis();
        coins = coins.add(getProduction().multiply(new BigDecimal((newTime - lastUpdateTime)/1000)));
        lastUpdateTime = System.currentTimeMillis();
    }
}