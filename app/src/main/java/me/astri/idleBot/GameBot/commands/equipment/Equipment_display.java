package me.astri.idleBot.GameBot.commands.equipment;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import me.astri.idleBot.GameBot.utils.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.HashMap;
import java.util.Map;

public class Equipment_display extends ISlashSubcommand {
    @Override
    public SubcommandData getData() {
        return new SubcommandData("display","shows your equipment")
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
        EmbedBuilder eb = new EmbedBuilder().setAuthor(author.getLang().get("equipment_title",name),null, user.getEffectiveAvatarUrl())
                .setColor(player.getColor());
        if(!author.equals(player))
            eb.setFooter(author.getLang().get("requested_by",event_author.getAsTag()),event_author.getEffectiveAvatarUrl());

        boolean previousIsntLevel0 = true, currentIsntLevel0;
        for(Map.Entry<String,Equipment> set : equipments.entrySet()) {
            Equipment gearPiece = set.getValue();
            if(!gearPiece.isUnlocked())
                continue;

            currentIsntLevel0 = gearPiece.getLevel() != 0;
            //display if previous/current eq isn't level 0 or if you have enough money

            BigNumber prod = BigNumber.multiply(gearPiece.getProduction(),player.getBoost());
            if(previousIsntLevel0 || currentIsntLevel0 || gearPiece.getPrice().compareTo(player.getCoins()) <= 0) {
                eb.addField(gearPiece.getEmote() + " " + author.getLang().get(gearPiece.getName()),
                        "Level **" + gearPiece.getLevel() + "\n" +
                                prod.getNotation(author.usesScNotation()) + Emotes.get("coin") + "**/s**\n" +
                                author.getLang().get("cost") + "** " + gearPiece.getPrice().getNotation(author.usesScNotation()), true);
            }
            previousIsntLevel0 = currentIsntLevel0;
        }
        eb.setDescription(player.getCoins().getNotation(author.usesScNotation()) + " " + Emotes.get("coin") + " " + author.getLang().get("coins"));
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
