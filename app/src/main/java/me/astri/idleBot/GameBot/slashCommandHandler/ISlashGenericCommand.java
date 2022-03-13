package me.astri.idleBot.GameBot.slashCommandHandler;

import me.astri.idleBot.GameBot.utils.Config;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Arrays;
import java.util.List;

public abstract class ISlashGenericCommand {

    public abstract BaseCommand<CommandData> getData();

    public abstract void handle(SlashCommandEvent e, InteractionHook hook);

    protected static final List<String> bypassUsers = Arrays.stream(Config.get("BYPASS_USERS").split(",")).toList();
    private static final List<String> authorizedChannels = Arrays.stream(Config.get("AUTHORIZED_CHANNELS").split(",")).toList();
    public Boolean hasPermission(SlashCommandEvent e) {
        return (authorizedChannels.contains(e.getChannel().getId()) || bypassUsers.contains(e.getUser().getId()));
    }

    public Boolean guildOnly() { return false; }

    public Boolean isEphemeral() { return false; }

    public long getCooldown() { return -1L; } //no cooldown
}