package me.astri.idleBot.GameBot.entities.upgrade.management;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.entities.upgrade.conditional.EquipmentUpgrade;
import me.astri.idleBot.GameBot.entities.upgrade.Upgrade;
import me.astri.idleBot.GameBot.entities.upgrade.unconditional.MinionUpgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerUpgrades {
    private final UpgradeBundle boughtUpgrades;
    private final List<String> availableUpgrades;
    private final UpgradeBundle unboughtUpgrades;

    public PlayerUpgrades() {
        boughtUpgrades = new UpgradeBundle(false);
        unboughtUpgrades = new UpgradeBundle(true);
        availableUpgrades = new ArrayList<>();

        availableUpgrades.addAll(unboughtUpgrades.getUncond());
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
        return this.getAvailable().stream().map(upName -> UpgradeManager.getUpgrades().get(upName))
                .sorted(new UpgradeManager.PriceComparator())
                .collect(Collectors.toList());
    }

    public void buy(Player p, Upgrade upgrade, List<Upgrade> availableSortedUpgrades) {
        p.editCoins(BigNumber.negate(upgrade.getPrice()));
        availableUpgrades.remove(upgrade.getName());
        availableSortedUpgrades.remove(upgrade);

        if (upgrade instanceof EquipmentUpgrade up) {
            up.action(p.getEquipment().get(up.getEq()));
            this.getBought().getEq().get(up.getEq()).add(up.getName());
        } else if (upgrade instanceof MinionUpgrade up) {
            up.action(p.getMinions());
            this.getBought().getUncond().add(up.getName());
        }
    }

    public void buyAll(Player p, List<Upgrade> availableSortedUpgrades) {
        while(availableUpgrades.size() > 0 && p.getCoins().compareTo(availableSortedUpgrades.get(0).getPrice()) >= 0) {
            Upgrade upgrade = availableSortedUpgrades.get(0);
            buy(p, upgrade, availableSortedUpgrades);
        }
    }
}
