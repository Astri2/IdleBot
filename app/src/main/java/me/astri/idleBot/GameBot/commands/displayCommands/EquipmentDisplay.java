package me.astri.idleBot.GameBot.commands.displayCommands;

import me.astri.idleBot.GameBot.Entities.equipments.Equipment;
import me.astri.idleBot.GameBot.Entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.main.Emotes;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
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
        display(hook, user, e.getUser());
    }

    public static void display(InteractionHook hook, User user, User event_author) {
        Player player,author;
        if((player = GameUtils.getUser(hook, event_author, user)) == null | (author = GameUtils.getUser(hook, event_author)) == null)
            return;
        
        player.update();
        String name = hook.getInteraction().getGuild().getMember(user) == null ? user.getName() :
                hook.getInteraction().getGuild().getMember(user).getEffectiveName();

        HashMap<String, Equipment> equipments = player.getEquipment();
        EmbedBuilder eb = new EmbedBuilder().setAuthor(author.getLang().get("equipment_title",name),null, user.getAvatarUrl());
        if(!author.equals(player))
            eb.setFooter(author.getLang().get("requested_by",event_author.getAsTag()),event_author.getAvatarUrl());
        for(Map.Entry<String,Equipment> set : equipments.entrySet()) {
            Equipment gearPiece = set.getValue();
            if(!gearPiece.isUnlocked())
                continue;
            eb.addField(gearPiece.getEmote() + " " + author.getLang().get(gearPiece.getName()),
                    "Level **" + gearPiece.getLevel() + "\n" +
                            GameUtils.getNumber(gearPiece.getProduction(),player) + Emotes.getEmote("coin") + "**/s**\n" +
                            author.getLang().get("cost") + "** " + GameUtils.getNumber(gearPiece.getPrice(),player), true);
            if(gearPiece.getLevel() == 0 && gearPiece.getPrice().compareTo(player.getCoins()) > 0)
                break;
        }
        eb.setDescription(GameUtils.getNumber(player.getCoins(),player) + " " + Emotes.getEmote("coin") + author.getLang().get("coins"));
        hook.sendMessageEmbeds(eb.build())
                .addActionRow(
                        Button.secondary("equipmentDisplay",author.getLang().get("display_equipment_button")),
                        Button.secondary("profileDisplay", author.getLang().get("display_profile_button")))
                .queue();
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}
