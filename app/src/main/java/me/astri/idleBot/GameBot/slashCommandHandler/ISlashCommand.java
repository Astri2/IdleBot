package me.astri.idleBot.GameBot.slashCommandHandler;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ISlashCommand extends ISlashGenericCommand{
    protected final List<ISlashSubcommand> subCommands;

    protected List<SubcommandData> getSubCommandDatas() {
        return this.subCommands.stream().map(slashSubCommand -> (SubcommandData) slashSubCommand.getData()).toList();
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {}

    public final Collection<ISlashSubcommand> getSubcommands() {
        return this.subCommands;
    }

    public ISlashCommand() {
        this.subCommands = null;
    }

    public ISlashCommand(ISlashSubcommand ... subcommands) {
        this.subCommands = List.of(subcommands);
    }
}
