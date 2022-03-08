package me.astri.idleBot.GameBot.entities.upgrade.conditional;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.upgrade.Upgrade;

import java.util.function.Predicate;

public abstract class ConditionalUpgrade extends Upgrade {

    protected Predicate<Object> condition;

    public ConditionalUpgrade(String name, String icon, BigNumber price) {
        super(name, icon, price);
    }

    protected abstract Predicate<Object> getCondition();

    public boolean meetUnlockCondition(Object t) {
        return condition.test(t);
    }

}
