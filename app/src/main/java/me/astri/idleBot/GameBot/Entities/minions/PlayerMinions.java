package me.astri.idleBot.GameBot.entities.minions;

import me.astri.idleBot.GameBot.entities.BigNumber;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerMinions {
    public LinkedHashMap<String,Minion> minions; //TODO private final

    private float timeFactor;
    private float rewardFactor;
    private float costFactor;

    private String lastMessageid;

    public PlayerMinions() {
        minions = initMinions();

        this.timeFactor = 1;
        this.rewardFactor = 1;
        this.costFactor = 1;
    }

    public static LinkedHashMap<String,Minion> initMinions() {
        LinkedHashMap<String,Minion> minions = new LinkedHashMap<>();
        for(MinionTypes type : MinionTypes.values())
            minions.put(type.name(),new Minion(type));
        return minions;
    }

    public LinkedHashMap<String,Minion> get() {
        return this.minions;
    }

    public float getTimeFactor() {
        return this.timeFactor;
    }

    public float getPriceFactor() {
        return this.costFactor;
    }

    public float getRewardFactor() {
        return this.rewardFactor;
    }

    public String getLastMessageid() {
        return this.lastMessageid;
    }

    public void setLastMessageid(String id) {
        this.lastMessageid=id;
    }

    public BigNumber getBoost() {
        AtomicInteger boost = new AtomicInteger(0);
        minions.values().forEach(m -> {
            if(m.isBought())
                boost.addAndGet(m.getCPSBonus());
        });
        return new BigNumber(boost.get()/100.);
    }
}
