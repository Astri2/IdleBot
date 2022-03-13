package me.astri.idleBot.GameBot.commands.upgrades;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Upgrades extends ISlashCommand {

    public Upgrades(ISlashSubcommand ... subcommands) { super(subcommands); }

    @Override
    public CommandData getData() {
        return new CommandData("upgrades","everything about upgrades")
                .addSubcommands(this.getSubCommandDatas());
    }
}
