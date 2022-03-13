package me.astri.idleBot.GameBot.commands.equipment;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Equipment extends ISlashCommand {

    public Equipment(ISlashSubcommand ... subcommands) { super(subcommands); }

    @Override
    public CommandData getData() {
        return new CommandData("equipment","everything about your equipment")
                .addSubcommands(this.getSubCommandDatas());
    }
}
