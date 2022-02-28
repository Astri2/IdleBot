package me.astri.idleBot.GameBot.slashCommandHandler;

import me.astri.idleBot.GameBot.utils.Config;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public abstract class ISlashGenericCommand {

    public abstract void handle(SlashCommandEvent e, InteractionHook hook);

    public abstract BaseCommand<CommandData> getData();

    private final static List<String> bypass = List.of(Config.get("BYPASS_PERMISSION").split(";"));
    public Boolean hasPermission(SlashCommandEvent e) {
        //user bypass OR event from dev guild OR ( official guild AND has deadly slayer )
        return bypass.contains(e.getUser().getId())
                || e.getGuild().getId().equals("317236920155504640")
                || ( e.getGuild().getId().equals("724614126780809337") &&
                        e.getMember().getRoles().contains(e.getGuild().getRoleById("725178568107098152")));
    }

    public Boolean guildOnly() { return false; }

    public Boolean isEphemeral() { return false; }

    public long getCooldown() { return -1L; } //no cooldown
}