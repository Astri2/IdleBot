package me.astri.idleBot.GameBot.commands.displayCommands;

import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.utils.Emotes;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
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
        display(hook, user, e.getUser());
    }

    public static void display(InteractionHook hook, User user, User event_author) {
        Player player,author;
        if((player = GameUtils.getUser(hook, event_author, user)) == null | (author = GameUtils.getUser(hook, event_author)) == null)
            return;

        player.update();
        String name = hook.getInteraction().getGuild().getMember(user) == null ? user.getName() :
                hook.getInteraction().getGuild().getMember(user).getEffectiveName();

        EmbedBuilder eb = new EmbedBuilder().setAuthor(author.getLang().get("profile_title",name),null, user.getEffectiveAvatarUrl())
                .addField(Emotes.getEmote("coin") + " " + author.getLang().get("coins"), GameUtils.getNumber(player.getCoins(),author),true)
                .addField(Emotes.getEmote("coin") + " " + author.getLang().get("production"), GameUtils.getNumber(player.getProduction(),author),true);

        if(!author.equals(player))
            eb.setFooter(author.getLang().get("requested_by",event_author.getAsTag()),event_author.getEffectiveAvatarUrl());

        hook.sendMessageEmbeds(eb.build())
                .addActionRow(
                        Button.secondary("profileDisplay",author.getLang().get("display_profile_button")),
                        Button.secondary("equipmentDisplay",author.getLang().get("display_equipment_button")))
                .queue();
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}