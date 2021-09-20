package me.astri.idleBot.commands.displayCommands;

import me.astri.idleBot.Entities.equipments.Equipment;
import me.astri.idleBot.Entities.player.Player;
import me.astri.idleBot.game.GameUtils;
import me.astri.idleBot.main.Emotes;
import me.astri.idleBot.main.Lang;
import me.astri.idleBot.slashCommandHandler.ISlashCommand;
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
        IMentionable author = e.isFromGuild() ? e.getMember() : e.getUser();
        User user = e.getOption("user") == null ? e.getUser() : e.getOption("user").getAsUser();
        Player player = GameUtils.getUser(hook, e.getUser(), user);

        display(hook, author, player);
    }

    public static void display(InteractionHook hook, IMentionable author, Player player) {
        if(player == null)
            return;
        String name = author instanceof Member ? ((Member) author).getEffectiveName() : ((User) author).getName();
        HashMap<String, Equipment> equipments = player.getEquipment();
        EmbedBuilder eb = new EmbedBuilder().setTitle(Lang.get(player.getLang(),"profile_title",name));

        for(Map.Entry<String,Equipment> set : equipments.entrySet()) {
            Equipment gearPiece = set.getValue();
            if(!gearPiece.isUnlocked())
                continue;
            eb.addField(gearPiece.getEmote() + " " + Lang.get(player.getLang(),gearPiece.getName()),
                    "Level **" + gearPiece.getLevel() + "\n" +
                            GameUtils.getNumber(gearPiece.getProduction(),player) + Emotes.getEmote("coin") + "**/s**\n" +
                    Lang.get(player.getLang(),"cost") + "** " + GameUtils.getNumber(gearPiece.getPrice(),player), true);
            if(gearPiece.getLevel() == 0 && gearPiece.getPrice().compareTo(player.getCoins()) == 1)
                break;
        }
        hook.sendMessageEmbeds(eb.build())
                .addActionRow(
                        Button.secondary("equipmentDisplay","\uD83D\uDD04"),
                        Button.secondary("profileDisplay", Lang.get(player.getLang(),"display_profile_button")))
                .queue();
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}
