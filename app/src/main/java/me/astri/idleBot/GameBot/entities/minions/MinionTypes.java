package me.astri.idleBot.GameBot.entities.minions;

enum MinionTypes {
    SKELETON(999,10800, 1500, 50, 1),
    KNIGHT(999,10800, 10, 1, 1),
    GOO(999,16200, 1500, 50, 2),
    WIZARD(999,23400, 150, 20, 2),
    GOLEM(999,82800, 3000, 1500, 4);

    private final int maxLevel;
    private final long duration;
    private final int cost;
    private final int reward;
    private final int boost;

    MinionTypes(int a, long b, int c, int d, int e) {
        maxLevel = a;
        duration = b;
        cost = c;
        reward = d;
        boost = e;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public long getDuration() {
        return duration;
    }

    public int getCost() {
        return cost;
    }

    public int getReward() {
        return reward;
    }

    public int getBoost() {
        return boost;
    }
}
