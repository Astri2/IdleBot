package me.astri.idleBot.GameBot.Entities.upgrade;

public class PlayerUpgrades {
    private final UpgradeBundle boughtUpgrades;
    private final UpgradeBundle availableUpgrades;
    private final UpgradeBundle unboughtUpgrades;

    public PlayerUpgrades() {
        boughtUpgrades = new UpgradeBundle(false);
        availableUpgrades = new UpgradeBundle(false);
        unboughtUpgrades = new UpgradeBundle(true);
    }

    public UpgradeBundle getBought() {
        return boughtUpgrades;
    }

    public UpgradeBundle getAvailable() {
        return availableUpgrades;
    }

    public UpgradeBundle getUnbought() {
        return unboughtUpgrades;
    }
}
