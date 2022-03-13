package me.astri.idleBot.GameBot.entities.upgrade.unconditional;

import me.astri.idleBot.GameBot.entities.PlayerChestHunt;
import me.astri.idleBot.GameBot.entities.minions.PlayerMinions;

import java.util.function.Consumer;

public abstract class UpgradeActions {
    static Consumer<Object> getAction(String id, String[] args) {
        switch(id) {
            case "minionUpgrade" -> {
                return obj -> {
                    PlayerMinions minions = (PlayerMinions) obj;
                    minions.get().get(args[0]).setBought(true);
                };
            }
            case "chestHuntUpgrade" -> {
                return obj -> {
                    PlayerChestHunt chestHunt = (PlayerChestHunt) obj;
                    chestHunt.setUnlocked(true);
                };
            }
            default -> {
                return null;
            }
        }
    }
}
