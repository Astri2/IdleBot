package me.astri.idleBot.GameBot.entities.upgrade;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.player.Player;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Upgrade {

    protected final String name;
    protected final String icon;
    protected final BigNumber price;
    protected Predicate<Object> condition;
    protected Consumer<Object> action;

    public Upgrade(String name, String icon, BigNumber price) {
        this.name = name;
        this.icon = icon;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    public BigNumber getPrice() {
        return this.price;
    }

    public boolean meetUnlockCondition(Object t) {
        return condition.test(t);
    }

    public void action(Object t) {
        action.accept(t);
    }

    public MessageEmbed.Field getUpgradeField(Player p, boolean current, boolean canAfford) {
        return new MessageEmbed.Field(getUpgradeTitle(p,current),getUpgradeDesc(p,canAfford),true);
    }

    protected abstract String getUpgradeTitle(Player p, boolean current);

    protected abstract String getUpgradeDesc(Player p, boolean canAfford);
}