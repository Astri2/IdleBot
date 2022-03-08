package me.astri.idleBot.GameBot.entities.upgrade;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.utils.Emotes;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Upgrade {

    protected final String name;
    protected final String icon;
    protected final BigNumber price;
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
        String[] end = {
                canAfford ? Emotes.get("yes") : Emotes.get("no"),
                this.price.getNotation(p.usesScNotation()) + Emotes.get("coin")
        };
        String[] fullList = Stream.concat(Arrays.stream(getDescArgs(p)), Arrays.stream(end)).toArray(String[]::new);
        return p.getLang().get(getDescId(), fullList);
    }

    protected abstract String getDescId();
    protected abstract String[] getDescArgs(Player p);

    protected abstract Consumer<Object> getAction();
}