package me.astri.idleBot.GameBot.entities.upgrade.unconditional;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.upgrade.Upgrade;

public abstract class UnconditionalUpgrade extends Upgrade {
    private final String[] args;

    public UnconditionalUpgrade(String name, String icon, BigNumber price, String[] args) {
        super(name, icon, price);
        this.args = args;
    }
}
