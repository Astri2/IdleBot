package me.astri.idleBot.commands;

import me.astri.idleBot.Entities.equipments.Equipment;
import me.astri.idleBot.Entities.player.Player;
import me.astri.idleBot.eventWaiter.EventWaiter;
import me.astri.idleBot.eventWaiter.Waiter;
import me.astri.idleBot.game.GameUtils;
import me.astri.idleBot.main.Emotes;
import me.astri.idleBot.main.Lang;
import me.astri.idleBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LevelUpEquipment implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("levelup","increase the level of your equipment")
                .addOptions(new OptionData(OptionType.STRING, "equipment","which equipment to level up")
                        .addChoice("sword","sword")
                        .addChoice("shield","shield")
                        .addChoice("armor","armor")
                        .addChoice("helmet","helmet")
                        .addChoice("boots","boots")
                        .addChoice("ring","ring")
                        .addChoice("dagger","dagger")
                        .addChoice("axe","axe")
                        .addChoice("staff","staff")
                        .addChoice("bow","bow")
                        .addChoice("spellbook","spellbook")
                        .addChoice("spirit","spirit")
                        .addChoice("necklace","necklace"))
                .addOptions(new OptionData(OptionType.STRING,"levels","up by how many levels")
                        .addChoice("1","1")
                        .addChoice("10","10")
                        .addChoice("50","50")
                        .addChoice("max","max")
                        .addChoice("custom","custom"));
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Player player = GameUtils.getUser(hook, e.getUser());
        if(player == null)
            return;

        String equipment = null;
        String levels = null;
        if(e.getOption("equipment") != null)
            equipment = e.getOption("equipment").getAsString();
        if(e.getOption("levels") != null)
            levels = e.getOption("levels").getAsString();

        if(equipment == null)
            askEquipment(hook, player, levels);
        else if (levels == null)
            askLevel(hook, player, equipment);
        else if (levels.equals("custom"))
            askCustomLevel(hook,player,equipment);
        else
            levelUp(hook, player, equipment, levels);
    }

    public static void askEquipment(InteractionHook hook, Player player, String levels) {
        SelectionMenu.Builder menu = SelectionMenu.create("equipmentSelect" + player.getId());
        for(Map.Entry<String,Equipment> eq : player.getEquipment().entrySet()) {
            menu.addOption(Lang.get(player.getLang(),eq.getValue().getName()),eq.getKey(), Emoji.fromMarkdown(eq.getValue().getEmote()));
        }
        hook.sendMessage("What equipment piece do you want to upgrade?").setEphemeral(true)
            .addActionRow(menu.build()).queue(msg -> {
                Waiter<SelectionMenuEvent> waiter = new Waiter<>();
                waiter.setExpirationTime(1, TimeUnit.MINUTES).setTimeoutAction(() -> msg.delete().queue());
                waiter.setAutoRemove(true).setEventType(SelectionMenuEvent.class);
                waiter.setConditions(e -> e.getInteraction().getComponent().getId().equals("equipmentSelect" + e.getUser().getId()));
                waiter.setAction(ctx -> {
                    ctx.getEvent().editSelectionMenu(ctx.getEvent().getSelectionMenu().asDisabled()).queue();
                    String equipment = ctx.getEvent().getInteraction().getSelectedOptions().get(0).getValue();
                    if(levels == null)
                        askLevel(hook,player,equipment);
                    else if(levels.equals("custom"))
                        askCustomLevel(hook,player,equipment);
                    else
                        levelUp(hook,player,equipment,levels);
                });
                EventWaiter.register(waiter);
        });
    }

    private static void askLevel(InteractionHook hook, Player player, String equipment) {
        hook.sendMessage("how many levels?").setEphemeral(true)
            .addActionRow(SelectionMenu.create("levelsSelect" + player.getId())
                .addOption("1","1")
                .addOption("10","10")
                .addOption("50","50")
                .addOption("max","max")
                .addOption("custom","custom").build()
            ).queue(msg -> {
                Waiter<SelectionMenuEvent> waiter = new Waiter<>();
                waiter.setExpirationTime(1, TimeUnit.MINUTES).setTimeoutAction(() -> msg.delete().queue());
                waiter.setAutoRemove(true).setEventType(SelectionMenuEvent.class);
                waiter.setConditions(e -> e.getInteraction().getComponent().getId().equals("levelsSelect" + e.getUser().getId()));
                waiter.setAction(ctx -> {
                    ctx.getEvent().editSelectionMenu(ctx.getEvent().getSelectionMenu().asDisabled()).queue();
                    String levels = ctx.getEvent().getInteraction().getSelectedOptions().get(0).getValue();
                    if(levels.equals("custom"))
                        askCustomLevel(hook,player,equipment);
                    else
                        levelUp(hook,player,equipment,levels);
                });
                EventWaiter.register(waiter);
        });
    }

    private static void askCustomLevel(InteractionHook hook, Player player, String equipment) {
        hook.sendMessage("choose a number").setEphemeral(true).queue(msg -> {
            Waiter<MessageReceivedEvent> waiter = new Waiter<>();
            waiter.setExpirationTime(1, TimeUnit.MINUTES).setTimeoutAction(() -> msg.delete().queue());
            waiter.setAutoRemove(true).setEventType(MessageReceivedEvent.class);
            waiter.setConditions(e -> e.getChannel().equals(msg.getChannel()) && e.getAuthor().getId().equals(player.getId()));
            waiter.setAction(ctx -> {
                String levels = ctx.getEvent().getMessage().getContentRaw();
                levelUp(hook,player,equipment,levels);
            });
            EventWaiter.register(waiter);
        });
    }

    private static void levelUp(InteractionHook hook, Player player, String equipment, String levels) {
        Equipment eq = player.getEquipment().get(equipment);
        player.update();
        int level=0;
        if(levels.equals("max")) {
            do {
                level++;
            } while(eq.getPrice(level+1).compareTo(player.getCoins()) < 0);
        }
        else try {
            level = Integer.parseInt(levels);
        } catch(NumberFormatException e) {
            hook.sendMessage("not a number").setEphemeral(true).queue();
            return;
        }
        if(eq.getPrice(level).compareTo(player.getCoins()) > 0) {
            hook.sendMessage("you don't have enough money!** "
                    + GameUtils.getNumber(player.getCoins(),player) + "/" + GameUtils.getNumber(eq.getPrice(level),player) + "**"
                    + Emotes.getEmote("coin")).setEphemeral(true).queue();
            return;
        }
        eq.levelUp(level);
        hook.sendMessage(eq.getEmote() + " " + Lang.get(player.getLang(),eq.getName()) + " is now level** " + eq.getLevel() + "** (+" + level + " levels)").setEphemeral(true)
                .addActionRow(
                        Button.secondary("redoLevelUp:" + equipment + ":" + levels,"\uD83D\uDD04"),
                        Button.secondary("equipmentDisplay",Lang.get(player.getLang(),"display_equipment_button")))
                .queue();
    }

    public static void redoLevelUp(InteractionHook hook, IMentionable author, String buttonId) {
        Player player = GameUtils.getUser(hook,author);
        String[] args = buttonId.split(":");
        levelUp(hook,player,args[1],args[2]);
    }

    @Override
    public Boolean isEphemeral() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}
