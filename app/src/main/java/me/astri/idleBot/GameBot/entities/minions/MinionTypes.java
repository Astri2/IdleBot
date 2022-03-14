package me.astri.idleBot.GameBot.entities.minions;

enum MinionTypes {
    SKELETON(999999,10800, 0, 0, 1),
    KNIGHT(999999,10800, 0, 0, 1), //10800
    GOO(999999,16200, 0, 0, 2), //16200
    WIZARD(999999,23400, 0, 0, 2), //23400
    GOLEM(999999,82800, 0, 0, 4); //82800

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
