package me.astri.idleBot.slashCommandHandler;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface ISlashCommand {
    CommandData getCommandData();

    void handle(SlashCommandEvent e, InteractionHook hook);

    default Boolean hasPermission(SlashCommandEvent e) {
        return true;
    }

    default Boolean guildOnly() { return false; }

    default Boolean isEphemeral() { return false; }

    default long getCooldown() { return -1L; } //no cooldown
}
