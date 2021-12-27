package me.astri.idleBot.GameBot.Entities.upgrade;

import me.astri.idleBot.GameBot.Entities.Number;
import me.astri.idleBot.GameBot.Entities.player.Player;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Upgrade {
    protected enum Type {
        EQUIPMENT
    }

    protected final String name;
    protected final String icon;
    protected final Number price;
    protected final Type type;
    protected final Predicate<Object> condition;
    protected final Consumer<Object> action;

    public Upgrade(String name, String icon, Number price, Type type, Predicate<Object> condition, Consumer<Object> action) {
        this.name = name;
        this.icon = icon;
        this.price = price;
        this.type = type;
        this.condition = condition;
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    public Number getPrice() {
        return this.price;
    }

    public boolean meetUnlockCondition(Object t) {
        return condition.test(t);
    }

    public void action(Object t) {
        action.accept(t);
    }

    public abstract MessageEmbed.Field getUpgradeField(Player p, boolean current, boolean canAfford);
}