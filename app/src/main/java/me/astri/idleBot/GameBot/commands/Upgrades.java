package me.astri.idleBot.GameBot.commands;

import me.astri.idleBot.GameBot.Entities.Number;
import me.astri.idleBot.GameBot.Entities.player.Player;
import me.astri.idleBot.GameBot.Entities.upgrade.Upgrade;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.main.BotGame;
import me.astri.idleBot.GameBot.main.Emotes;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Upgrades implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("upgrades","displays your available upgrades");
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) throws Exception {
        Player player = GameUtils.getUser(hook, e.getUser());
        if(player == null)
            return;

        AtomicReference<List<Upgrade>> upgrades = new AtomicReference<>(player.getUpgrades().getAvailableSortedUpgrades());
        AtomicInteger max = new AtomicInteger(Math.min(upgrades.get().size(), 12));
        AtomicInteger min = new AtomicInteger(0);
        AtomicInteger current = new AtomicInteger(0);

        hook.sendMessageEmbeds(getUpgradeDisplay(player, upgrades.get(),min.get(),max.get(),current.get()))
                .addActionRows(getButtons(upgrades.get(),current.get(),min.get(),max.get(),player,false))
                .queue(msg -> {
            Waiter<ButtonClickEvent> waiter = new Waiter<ButtonClickEvent>()
                .setEventType(ButtonClickEvent.class)
                .setExpirationTime(1, TimeUnit.MINUTES)
                .setConditions(event -> event.getMessage().equals(msg) && event.getInteraction().getUser().equals(e.getUser()))
                .setTimeoutAction(() -> {
                    msg.editMessageComponents(getButtons(upgrades.get(), current.get(), min.get(), max.get(), player, true)).queue();
                    msg.editMessage(player.getLang().get("expired")).queue();
                })
                .setAutoRemove(false)
                .setAction(ctx -> {
                    ctx.resetTimer();
                    switch(ctx.getEvent().getInteraction().getButton().getId()){ //TODO pagination
                        case "left" -> current.addAndGet(-1);
                        case "right" -> current.addAndGet(1);
                        case "buy" -> {
                            player.getUpgrades().buy(player, upgrades.get().get(current.get()),upgrades.get());
                            while(current.get() >= upgrades.get().size()) current.addAndGet(-1);
                            max.set(Math.min(upgrades.get().size(), 12));
                        }
                        case "buy_all" -> {
                            player.getUpgrades().buyAll(player, upgrades.get());
                            while(current.get() >= upgrades.get().size()) current.addAndGet(-1);
                            max.set(Math.min(upgrades.get().size(), 12));
                        }
                    }
                    ctx.getEvent().editMessageEmbeds(getUpgradeDisplay(player, upgrades.get(),min.get(),max.get(),current.get()))
                            .setActionRows(getButtons(upgrades.get(),current.get(),min.get(),max.get(),player,false)).queue();
                });
            waiter.register("upgradeMenu_"+player.getId());
        });
    }

    private MessageEmbed getUpgradeDisplay(Player p, List<Upgrade> upgrades, int min, int max, int current) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Available Upgrades of " + BotGame.jda.getUserById(p.getId()).getName(),null, BotGame.jda.getUserById(p.getId()).getEffectiveAvatarUrl());
        p.update();
        eb.setDescription(GameUtils.getNumber(p.getCoins(),p) + " " + Emotes.getEmote("coin") + " " + p.getLang().get("coins"));
        for(int i = min ; i < max ; i++) {
            Upgrade upg = upgrades.get(i);
            eb.addField(upg.getUpgradeField(p,i==current,p.getCoins().compareTo(BigDecimal.valueOf(upg.getPrice().toDouble()))>=0));
        }

        if(upgrades.isEmpty()) eb.appendDescription("\n\n" + Emotes.getEmote("no")+ " - " + p.getLang().get("no_available_upgrade"));

        return eb.build();
    }

    private Collection<ActionRow> getButtons(List<Upgrade> upgrades, int current, int min, int max, Player p, boolean allDisable) {
        if(!allDisable) allDisable = upgrades.isEmpty();
        return Collections.singletonList(ActionRow.of(
                Button.secondary("left", "<=").withDisabled(allDisable || current == min),
                Button.secondary("right", "=>").withDisabled(allDisable || current == max),
                Button.secondary("buy", "BUY").withDisabled(allDisable || p.getCoins().compareTo(
                        BigDecimal.valueOf(upgrades.get(current).getPrice().toDouble())) < 0),
                Button.secondary("buy_all", "BUY ALL").withDisabled(allDisable || p.getCoins().compareTo(
                        BigDecimal.valueOf(upgrades.get(0).getPrice().toDouble())) < 0)
        ));
    }
}
