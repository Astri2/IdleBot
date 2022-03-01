package me.astri.idleBot.GameBot.slashCommandHandler;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class ISlashGenericCommand {

    public abstract void handle(SlashCommandEvent e, InteractionHook hook);

    public abstract BaseCommand<CommandData> getData();

    public Boolean hasPermission(SlashCommandEvent e) {
        return true;
    }

    public Boolean guildOnly() { return false; }

    public Boolean isEphemeral() { return false; }

    public long getCooldown() { return -1L; } //no cooldown
}