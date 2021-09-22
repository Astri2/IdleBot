package me.astri.idleBot.commands.displayCommands;

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

public class ProfileDisplay implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("profile","shows your profile")
                .addOption(OptionType.USER,"user","show someone's profile",false)
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
        player.update();
        String name = author instanceof Member ? ((Member) author).getEffectiveName() : ((User) author).getName();
        EmbedBuilder eb = new EmbedBuilder().setTitle(Lang.get(player.getLang(),"profile_title",name))
                .addField(Emotes.getEmote("coin") + " " + Lang.get(player.getLang(),"coins"), GameUtils.getNumber(player.getCoins(),player),true)
                .addField(Emotes.getEmote("coin") + " " + Lang.get(player.getLang(),"production"), GameUtils.getNumber(player.getProduction(),player),true);

        hook.sendMessageEmbeds(eb.build())
                .addActionRow(
                        Button.secondary("profileDisplay","\uD83D\uDD04"),
                        Button.secondary("equipmentDisplay",Lang.get(player.getLang(),"display_equipment_button")))
                .queue();
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}