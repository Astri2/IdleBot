package me.astri.idleBot.GameBot.commands.equipment;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.eventWaiter.EventWaiter;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.GameBot.eventWaiter.WaiterTemplates;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import me.astri.idleBot.GameBot.utils.Emotes;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Equipment_upgrade extends ISlashSubcommand {
    @Override
    public SubcommandData getData() {
        return new SubcommandData("upgrade","increase the level of your equipment")
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
                        .addChoice("custom","custom"))
                .addOption(OptionType.BOOLEAN,"ephemeral","only you can see the message");
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
            askCustomLevel(hook, true, player, equipment);
        else
            levelUp(hook, player, equipment, levels);
    }

    public static void askEquipment(InteractionHook hook, Player player, String levels) {
        SelectionMenu.Builder menu = SelectionMenu.create("equipmentSelect" + player.getId());
        for(Map.Entry<String,Equipment> eq : player.getEquipment().entrySet()) {
            menu.addOption(player.getLang().get(eq.getValue().getName()),eq.getKey(), Emoji.fromMarkdown(eq.getValue().getEmote()));
        }
        hook.sendMessage(player.getLang().get("eqpm_upgrade_ask_eqpm",hook.getInteraction().getUser().getAsMention()))
            .addActionRow(menu.build()).queue(msg -> {
                hook.setEphemeral(msg.isEphemeral());

                Waiter<SelectionMenuEvent> waiter = new Waiter<>();
                waiter.setExpirationTime(1, TimeUnit.MINUTES).setTimeoutAction(() ->
                    msg.editMessageComponents(ActionRow.of(SelectionMenu.create("unused")
                            .addOption("unused","unused")
                            .setPlaceholder(player.getLang().get("message_expired")).setDisabled(true).build())).queue()
                );
                waiter.setAutoRemove(true).setEventType(SelectionMenuEvent.class);
                waiter.setConditions(
                        e -> e.getInteraction().getComponent().getId().equals("equipmentSelect" + e.getUser().getId()) && e.getMessageId().equals(msg.getId())
                );
                waiter.setFailureAction(ctx -> {
                    if(ctx.getEvent().getMessageId().equals(msg.getId()))
                        ctx.getEvent().reply(player.getLang().get("error_cant_interact",ctx.getEvent().getUser().getAsMention()))
                            .setEphemeral(true).queue();
                });
                waiter.setAction(ctx -> {
                    String equipment = ctx.getEvent().getInteraction().getSelectedOptions().get(0).getValue();
                    ctx.getEvent().editSelectionMenu(SelectionMenu.fromData(ctx.getEvent().getSelectionMenu().toData()).setDefaultValues(List.of(equipment))
                            .setDisabled(true).build()).queue();
                    if(levels == null)
                        askLevel(hook,player,equipment);
                    else if(levels.equals("custom"))
                        askCustomLevel(hook,msg.isEphemeral(), player,equipment);
                    else
                        levelUp(hook,player,equipment,levels);
                });
                EventWaiter.register(waiter,"Eqpm_" + player.getId());
        });
    }

    private static void askLevel(InteractionHook hook, Player player, String equipment) {
        player.update();
        Equipment eq = player.getEquipment().get(equipment);
        int maxLevel = getMaxLevel(player,eq);
        hook.sendMessage(player.getLang().get("eqpm_upgrade_ask_level", hook.getInteraction().getUser().getAsMention(),
                player.getEquipment().get(equipment).getEmote(),player.getLang().get(player.getEquipment().get(equipment).getName())))
           .addActionRow(SelectionMenu.create("levelsSelect" + player.getId())
                .addOption(
                        player.getLang().get("level_s","1") + " - "
                                + eq.getPrice().getNotation(player.usesScNotation()) + "$","1")
                .addOption(
                        player.getLang().get("level_p","10") + " - "
                                + eq.getPrice(10).getNotation(player.usesScNotation()) + "$","10")
                .addOption(
                        player.getLang().get("level_p","50") + " - "
                                + eq.getPrice(50).getNotation(player.usesScNotation()) + "$","50")
                .addOption(
                        player.getLang().get("nbr_max") + " - " +
                                player.getLang().get("level_" + (maxLevel > 1 ? "p" : "s"),Integer.toString(maxLevel)) + " - "
                                + eq.getPrice(maxLevel).getNotation(player.usesScNotation()) + "$","max")
                .addOption(player.getLang().get(
                        "nbr_custom"),"custom").build()
           ).queue(msg -> {
                hook.setEphemeral(msg.isEphemeral());

                Waiter<SelectionMenuEvent> waiter = new Waiter<>();
                waiter.setExpirationTime(1, TimeUnit.MINUTES).setTimeoutAction(() ->
                    msg.editMessageComponents(ActionRow.of(SelectionMenu.create("unused")
                            .addOption("unused","unused")
                            .setPlaceholder(player.getLang().get("message_expired")).setDisabled(true).build())).queue()
                );
                waiter.setAutoRemove(true).setEventType(SelectionMenuEvent.class);
                waiter.setConditions(
                        e -> e.getInteraction().getComponent().getId().equals("levelsSelect" + e.getUser().getId()) && e.getMessageId().equals(msg.getId())
                );
                waiter.setFailureAction(ctx -> {
                    if(ctx.getEvent().getMessageId().equals(msg.getId()))
                        ctx.getEvent().reply(player.getLang().get("error_cant_interact",ctx.getEvent().getUser().getAsMention()))
                                .setEphemeral(true).queue();
                });
                waiter.setAction(ctx -> {
                    String levels = ctx.getEvent().getInteraction().getSelectedOptions().get(0).getValue();
                    ctx.getEvent().editSelectionMenu(SelectionMenu.fromData(ctx.getEvent().getSelectionMenu().toData()).setDefaultValues(List.of(levels))
                            .setDisabled(true).build()).queue();
                    if(levels.equals("custom"))
                        askCustomLevel(hook, msg.isEphemeral(), player, equipment);
                    else
                        levelUp(hook,player,equipment,levels);
                });
                EventWaiter.register(waiter, "lvl_" + player.getId());
        });
    }

    private static void askCustomLevel(InteractionHook hook, boolean isEphemeral, Player player, String equipment) {
        AtomicReference<String> number = new AtomicReference<>("");
                WaiterTemplates.numPadEvent(player.getLang().get("eqpm_upgrade_ask_level", hook.getInteraction().getUser().getAsMention(),
                        player.getEquipment().get(equipment).getEmote(),player.getLang().get(player.getEquipment().get(equipment).getName())),
                        hook, isEphemeral,number, player.getLang(), () -> levelUp(hook,player,equipment,number.get()));
    }

    private static void levelUp(InteractionHook hook, Player player, String equipment, String levels) {
        Equipment eq = player.getEquipment().get(equipment);
        player.update();
        int level;

        if(levels.equals("max")) {
            level = getMaxLevel(player, eq);
        }
        else try {
            level = Integer.parseInt(levels);
        } catch(NumberFormatException e) {
            hook.sendMessage(player.getLang().get("error_nan")).queue();
            return;
        }

        BigNumber price = eq.getPrice(level);

        if(eq.getPrice(level).compareTo(player.getCoins()) > 0) {
            hook.sendMessage(player.getLang().get("error_not_enough_money",hook.getInteraction().getUser().getAsMention()) + " **"
                    + player.getCoins().getNotation(player.usesScNotation()) + "/" + price.getNotation(player.usesScNotation()) + "**"
                    + Emotes.get("coin")).queue();
            return;
        }

        player.editCoins(BigNumber.negate(price));
        eq.levelUp(level,player.getUpgrades());

        hook.sendMessage(player.getLang().get("eqpm_upgrade_success",
                                    hook.getInteraction().getUser().getAsMention(),eq.getEmote(),player.getLang().get(eq.getName()),
                                    Long.toString(eq.getLevel())) + " (+" +
                                    player.getLang().get(levels.equals("1") ? "level_s" : "level_p", String.valueOf(level)) + ")" +
                                    "\n**-" +
                                    price.getNotation(player.usesScNotation()) + Emotes.get("coin") + "**")
            .addActionRow(
                        Button.secondary("redoLevelUp:" + equipment + ":" + levels,
                                player.getLang().get("redo_level_up_button") + " - (" +
                                player.getLang().get(levels.equals("1") ? "level_s" : "level_p",levels) + ")"),
                        Button.secondary("equipmentDisplay",player.getLang().get("display_equipment_button")))
                .queue();
    }

    private static int getMaxLevel(Player player, Equipment eq) {
        int level=0;
        do {
            level++;
        } while(eq.getPrice(level+1).compareTo(player.getCoins()) < 0);
        return level;
    }

    public static void redoLevelUp(InteractionHook hook, IMentionable author, String buttonId) {
        Player player = GameUtils.getUser(hook,author);
        String[] args = buttonId.split(":");
        levelUp(hook,player,args[1],args[2]);
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}
