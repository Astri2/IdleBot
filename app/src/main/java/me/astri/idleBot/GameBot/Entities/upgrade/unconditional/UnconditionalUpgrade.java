package me.astri.idleBot.GameBot.entities.upgrade.unconditional;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.upgrade.Upgrade;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public abstract class UnconditionalUpgrade extends Upgrade {
    protected final String[] args;

    public UnconditionalUpgrade(String name, String icon, BigNumber price, String[] args) {
        super(name, icon, price);
        this.args = args;
    }

    public static void init(JSONArray JSONUcondUpgrades, HashMap<String, UnconditionalUpgrade> uncondUpg, HashMap<String, Upgrade> upgrades ) {
        for(int i = 0 ; i < JSONUcondUpgrades.length() ; i++) {
            JSONObject up = JSONUcondUpgrades.getJSONObject(i);
            switch(up.getString("type")) {
                case "minion" -> {
                    MinionUpgrade mUp = new MinionUpgrade(
                            up.getString("name"),
                            up.getString("icon"),
                            new BigNumber(up.getString("price")),
                            up.getJSONArray("args").toList().stream().map(Object::toString).toArray(String[]::new)
                    );
                    uncondUpg.put(mUp.getName(),mUp);
                    upgrades.put(mUp.getName(),mUp);
                }
                default -> {
                }
            }
        }
    }
}
