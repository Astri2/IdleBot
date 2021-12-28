package me.astri.idleBot.GameBot.Entities.upgrade;

import me.astri.idleBot.GameBot.Entities.Number;
import me.astri.idleBot.GameBot.Entities.equipments.Equipment;
import me.astri.idleBot.GameBot.Entities.player.Player;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerUpgrades {
    private final UpgradeBundle boughtUpgrades;
    private final List<String> availableUpgrades;
    private final UpgradeBundle unboughtUpgrades;

    public PlayerUpgrades() {
        boughtUpgrades = new UpgradeBundle(false);
        availableUpgrades = new ArrayList<>();
        unboughtUpgrades = new UpgradeBundle(true);
    }

    public UpgradeBundle getBought() {
        return boughtUpgrades;
    }

    public List<String> getAvailable() {
        return availableUpgrades;
    }

    public UpgradeBundle getUnbought() {
        return unboughtUpgrades;
    }

    public List<Upgrade> getAvailableSortedUpgrades() {
        return this.getAvailable().stream().map(eqName -> UpgradeManager.getUpgrades().get(eqName))
                .sorted(new UpgradeManager.PriceComparator())
                .collect(Collectors.toList());
    }

    public void buy(Player p, Upgrade upgrade, List<Upgrade> availableSortedUpgrades) {
        p.editCoins(BigDecimal.valueOf(upgrade.getPrice().toDouble()).negate());
        if (upgrade instanceof EquipmentUpgrade) {
            EquipmentUpgrade up = (EquipmentUpgrade)upgrade;
            this.getBought().getEq().get(up.getEq()).add(up.getName());
            availableUpgrades.remove(up.getName());
            availableSortedUpgrades.remove(up);
        }
    }

    public void buyAll(Player p, List<Upgrade> availableSortedUpgrades) {
        while(availableUpgrades.size() > 0 && p.getCoins().compareTo(BigDecimal.valueOf(availableSortedUpgrades.get(0).getPrice().toDouble())) >= 0) {
            Upgrade upgrade = availableSortedUpgrades.get(0);
            buy(p, upgrade, availableSortedUpgrades);
        }
    }
}
