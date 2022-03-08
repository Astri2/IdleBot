package me.astri.idleBot.GameBot.entities.minions;

import java.util.ArrayList;
import java.util.List;

public class Minion {

    private final MinionTypes type;
    private int level;
    private boolean idle;
    private long endTime;

    private float timeFactor;
    private float rewardFactor;
    private float costFactor;

    private boolean bought;

    Minion(MinionTypes type) {
        this.type = type;
        this.level = 0;
        this.idle = true;

        this.timeFactor = 1;
        this.rewardFactor = 1;
        this.costFactor = 1;

        this.bought = true;
    }

    public void startMission() {
        this.idle = false;
        this.endTime = (System.currentTimeMillis())/1000 + this.getDuration();
    }

    public void endMission() {
        this.idle = true;
        this.level++;
    }

    public static ArrayList<Minion> initMinions() {
        ArrayList<Minion> minions = new ArrayList<>();
        for(MinionTypes type : MinionTypes.values())
            minions.add(new Minion(type));
        return minions;
    }

    public MinionTypes getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isIdle() {
        return this.idle;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public int getCPSBonus() {
        return this.level * this.type.getBoost();
    }

    public long getDuration() {
        return (long) (type.getDuration() * timeFactor);
    }

    public int getCost() {
        return (int) (type.getCost() * this.costFactor);
    }

    public int getReward() {
        return (int) (type.getReward() * this.rewardFactor);
    }

    public boolean isBought() {
        return this.bought;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
