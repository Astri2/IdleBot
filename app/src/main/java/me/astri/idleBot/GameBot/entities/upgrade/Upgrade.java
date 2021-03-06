package me.astri.idleBot.GameBot.entities.upgrade;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.utils.Emotes;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.function.Consumer;

public abstract class Upgrade {

    protected final String name;
    protected final String icon;
    protected final BigNumber price;
    protected Consumer<Object> action;

    public Upgrade(String name, String icon, BigNumber price) {
        this.name = name;
        this.icon = icon;
        this.price = price;

        this.action = getAction();
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

    public void action(Object t) {
        action.accept(t);
    }

    public MessageEmbed.Field getUpgradeField(Player p, boolean current, boolean canAfford) {
        return new MessageEmbed.Field(getUpgradeTitle(p,current),getUpgradeDesc(p,canAfford),true);
    }

    protected String getUpgradeTitle(Player p, boolean current) {
        return p.getLang().get(getTitleId(),current ? "►":"", this.icon, p.getLang().get(this.name));
    }

    protected abstract String getTitleId();

    protected String getUpgradeDesc(Player p, boolean canAfford) {
        return p.getLang().get(getDescId(), getDescArgs(p)) + "\n"
                + (canAfford ? Emotes.get("yes") : Emotes.get("no")) + " "
                + p.getLang().get("cost") + ": " + this.price.getNotation(p.usesScNotation()) + Emotes.get("coin");
    }

    protected abstract String getDescId();
    protected abstract String[] getDescArgs(Player p);

    protected abstract Consumer<Object> getAction();
}