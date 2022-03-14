package me.astri.idleBot.GameBot.commands.upgrades;

import me.astri.idleBot.GameBot.BotGame;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.entities.upgrade.Upgrade;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import me.astri.idleBot.GameBot.utils.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Upgrades_buy extends ISlashSubcommand {
    @Override
    public SubcommandData getData() {
        return new SubcommandData("buy","displays your available upgrades");
    }

    private final int MAX_DISPLAYED_UPGRADES = 12;
    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Player player = GameUtils.getUser(hook, e.getUser());
        if(player == null)
            return;

        AtomicReference<List<Upgrade>> upgrades = new AtomicReference<>(player.getUpgrades().getAvailableSortedUpgrades());
        AtomicInteger max = new AtomicInteger(Math.min(upgrades.get().size()-1, MAX_DISPLAYED_UPGRADES-1));
        AtomicInteger min = new AtomicInteger(0);
        AtomicInteger current = new AtomicInteger(0);

        hook.sendMessageEmbeds(getUpgradeDisplay(player, upgrades.get(),min.get(),max.get(),current.get(),""))
                .addActionRows(getButtons(upgrades.get(),current.get(), player,false))
                .queue(msg -> {
            Waiter<ButtonClickEvent> waiter = new Waiter<ButtonClickEvent>()
                .setEventType(ButtonClickEvent.class)
                .setExpirationTime(1, TimeUnit.MINUTES)
                .setConditions(event -> event.getMessage().equals(msg) && event.getInteraction().getUser().equals(e.getUser()))
                .setFailureAction(ctx -> {
                    if (ctx.getEvent().getMessage().equals(msg))
                        ctx.getEvent().reply("you can't interact with that!").setEphemeral(true).queue();
                })
                .setTimeoutAction(() ->
                    msg.editMessageEmbeds(getUpgradeDisplay(player, upgrades.get(),min.get(),max.get(),
                                    current.get(), player.getLang().get("message_expired")))
                            .setActionRows(getButtons(upgrades.get(), current.get(), player,true)).queue()
                )
                .setAutoRemove(false)
                .setAction(ctx -> {
                    ctx.resetTimer();
                    String footer = "";
                    switch(ctx.getEvent().getInteraction().getButton().getId()){ //TODO pagination
                        case "left" -> {
                            current.decrementAndGet();
                            if(current.get() < min.get()) {
                                min.decrementAndGet();
                                max.decrementAndGet();
                            }
                        }
                        case "right" -> {
                            current.incrementAndGet();
                            if(current.get() > max.get()) {
                                min.incrementAndGet();
                                max.incrementAndGet();
                            }
                        }
                        case "buy" -> {
                            footer = player.getLang().get(
                                    "upgrade_bought",player.getLang().get(upgrades.get().get(current.get()).getName()));
                            player.getUpgrades().buy(player, upgrades.get().get(current.get()),upgrades.get());
                            current.set(Math.min(upgrades.get().size()-1, current.get()));
                            max.set(Math.min(upgrades.get().size()-1, max.get()));
                            min.set(Math.max(max.get()-MAX_DISPLAYED_UPGRADES,0));
                        }
                        case "buy_all" -> {
                            int upgradeNb = upgrades.get().size();
                            player.getUpgrades().buyAll(player, upgrades.get());
                            current.set(Math.min(upgrades.get().size()-1, current.get()));
                            max.set(Math.min(upgrades.get().size()-1, max.get()));
                            min.set(Math.max(max.get()-MAX_DISPLAYED_UPGRADES,0));
                            footer = player.getLang().get(
                                    "upgrade_bought_all", Integer.toString(upgradeNb-upgrades.get().size()));
                        }
                    }
                    ctx.getEvent().editMessageEmbeds(getUpgradeDisplay(player, upgrades.get(),min.get(),max.get(),current.get(), footer))
                            .setActionRows(getButtons(upgrades.get(), current.get(), player,false)).queue();
                });
            waiter.register("upgradeMenu_"+player.getId());
        });
    }

    private MessageEmbed getUpgradeDisplay(Player p, List<Upgrade> upgrades, int min, int max, int current, String footer) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Available Upgrades of " + BotGame.jda.getUserById(p.getId()).getName(),null, BotGame.jda.getUserById(p.getId()).getEffectiveAvatarUrl());
        p.update();
        eb.setDescription(p.getCoins().getNotation(p.usesScNotation()) + " " + Emotes.get("coin") + " " + p.getLang().get("coins"));
        for(int i = min ; i <= max ; i++) {
            Upgrade upg = upgrades.get(i);
            eb.addField(upg.getUpgradeField(p,i==current,p.getCoins().compareTo(upg.getPrice())>=0));
        }

        if(upgrades.isEmpty()) eb.addField(p.getLang().get("no_available_upgrade"),Emotes.get("no"),false);
        else eb.appendDescription("\n" + p.getLang().get("upgrade_nb_available",Integer.toString(upgrades.size())));

        return eb.setFooter(footer).build();
    }

    private Collection<ActionRow> getButtons(List<Upgrade> upgrades, int current, Player p, boolean allDisable) {
        if(!allDisable) allDisable = upgrades.isEmpty();
        return List.of(ActionRow.of(
                Button.secondary("left", "<=").withDisabled(allDisable || current == 0),
                Button.secondary("right", "=>").withDisabled(allDisable || current == upgrades.size()-1),
                Button.secondary("buy", p.getLang().get("upgrade_buy_button")).withDisabled(allDisable ||
                        p.getCoins().compareTo(upgrades.get(current).getPrice()) < 0),
                Button.secondary("buy_all", p.getLang().get("upgrade_buy_all_button")).withDisabled(allDisable ||
                        p.getCoins().compareTo(upgrades.get(0).getPrice()) < 0)
                ),
                ActionRow.of(Button.secondary("refresh",p.getLang().get("upgrade_refresh_button")).withDisabled(allDisable))
        );
    }
}