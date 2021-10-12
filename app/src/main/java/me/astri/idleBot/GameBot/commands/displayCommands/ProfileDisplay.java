package me.astri.idleBot.GameBot.commands.displayCommands;

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

public class ProfileDisplay implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("profile","shows your profile")
                .addOption(OptionType.USER,"user","show someone's profile",false)
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
        EmbedBuilder eb = new EmbedBuilder().setAuthor(player.getLang().get("profile_title",name),null, hook.getJDA().getUserById(user.getId()).getAvatarUrl())
                .addField(Emotes.getEmote("coin") + " " + player.getLang().get("coins"), GameUtils.getNumber(player.getCoins(),player),true)
                .addField(Emotes.getEmote("coin") + " " + player.getLang().get("production"), GameUtils.getNumber(player.getProduction(),player),true);

        hook.sendMessageEmbeds(eb.build())
                .addActionRow(
                        Button.secondary("profileDisplay",player.getLang().get("display_profile_button")),
                        Button.secondary("equipmentDisplay",player.getLang().get("display_equipment_button")))
                .queue();
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}