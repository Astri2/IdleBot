package me.astri.idleBot.GameBot.commands.settings;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Settings extends ISlashCommand {

    public Settings(ISlashSubcommand ... subcommands) { super(subcommands); }

    @Override
    public CommandData getData() {
        return new CommandData("settings","configure your game")
                .addSubcommands(this.getSubCommandDatas());
    }
}
