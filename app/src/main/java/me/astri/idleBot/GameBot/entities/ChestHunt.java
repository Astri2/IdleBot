package me.astri.idleBot.GameBot.entities;

public class ChestHunt {
    private long lastTime;
    private int streak;

    public ChestHunt() {
        lastTime = 0;
        streak = 0;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void updateLastTime() {
        lastTime = System.currentTimeMillis();
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
}
