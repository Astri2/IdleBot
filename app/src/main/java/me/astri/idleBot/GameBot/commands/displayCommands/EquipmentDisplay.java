package me.astri.idleBot.GameBot.commands.displayCommands;

import me.astri.idleBot.GameBot.Entities.equipments.Equipment;
import me.astri.idleBot.GameBot.Entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.main.Emotes;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.HashMap;
import java.util.Map;

public class EquipmentDisplay implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("equipment","shows your equipment")
                .addOption(OptionType.USER,"user","show someone's equipment",false)
                .addOption(OptionType.BOOLEAN,"ephemeral","only you can see the message",false);
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        User user = e.getOption("user") == null ? e.getUser() : e.getOption("user").getAsUser();
        Player player = GameUtils.getUser(hook, e.getUser(), user);

        display(hook, user, player);
    }

    public static void display(InteractionHook hook, IMentionable user, Player player) {
        if(player == null)
            return;
        player.update();
        String name = user instanceof Member ? ((Member) user).getEffectiveName() : ((User) user).getName();
        HashMap<String, Equipment> equipments = player.getEquipment();
        EmbedBuilder eb = new EmbedBuilder().setTitle(player.getLang().get("equipment_title",name));

        for(Map.Entry<String,Equipment> set : equipments.entrySet()) {
            Equipment gearPiece = set.getValue();
            if(!gearPiece.isUnlocked())
                continue;
            eb.addField(gearPiece.getEmote() + " " + player.getLang().get(gearPiece.getName()),
                    "Level **" + gearPiece.getLevel() + "\n" +
                            GameUtils.getNumber(gearPiece.getProduction(),player) + Emotes.getEmote("coin") + "**/s**\n" +
                    player.getLang().get("cost") + "** " + GameUtils.getNumber(gearPiece.getPrice(),player), true);
            if(gearPiece.getLevel() == 0 && gearPiece.getPrice().compareTo(player.getCoins()) > 0)
                break;
        }
        eb.setDescription(player.getLang().get("equipment_display_money",GameUtils.getNumber(player.getCoins(),player) + Emotes.getEmote("coin")));
        hook.sendMessageEmbeds(eb.build())
                .addActionRow(
                        Button.secondary("equipmentDisplay","\uD83D\uDD04"),
                        Button.secondary("profileDisplay", player.getLang().get("display_profile_button")))
                .queue();
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}
