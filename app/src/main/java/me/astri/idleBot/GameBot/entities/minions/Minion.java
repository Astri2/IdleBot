package me.astri.idleBot.GameBot.entities.minions;

import me.astri.idleBot.GameBot.entities.player.Player;

public class Minion {

    private final MinionTypes type;
    private int level;
    private boolean idle;
    private long endTime;

    private boolean bought;

    Minion(MinionTypes type) {
        this.type = type;
        this.level = 0;
        this.idle = true;

        this.bought = false;
    }

    public void endMission() {
        this.idle = true;
        if(this.level <= this.type.getMaxLevel())
            this.level++;
    }

    public void startMission(PlayerMinions minions) {
        long endTime = Math.round(System.currentTimeMillis()/1000. + (this.getType().getDuration() * minions.getTimeFactor()));

        idle = false;
        this.endTime = endTime;
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

    public int getCPSBonusPerLevel() {
        return this.type.getBoost();
    }

    public int getCPSBonus() {
        return this.level * this.type.getBoost();
    }

    public long getDuration(Player p) {
        return (long) (type.getDuration() * p.getMinions().getTimeFactor());
    }

    public int getPrice(Player p) {
        return (int) (type.getCost() * p.getMinions().getPriceFactor());
    }

    public int getReward(Player p) {
        return (int) (type.getReward() * p.getMinions().getRewardFactor());
    }

    public boolean isBought() {
        return this.bought;
    }

    public void setBought(boolean b) {
        this.bought = b;
    }

    @Override
    public String toString() {
        return this.type.name();
    }
}
