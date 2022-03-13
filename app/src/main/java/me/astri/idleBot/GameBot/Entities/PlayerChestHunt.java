package me.astri.idleBot.GameBot.entities;

public class PlayerChestHunt {
    private boolean unlocked;
    private long lastTime;
    private int streak;

    public PlayerChestHunt() {
        unlocked = true;
        lastTime = 0;
        streak = 0;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
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
