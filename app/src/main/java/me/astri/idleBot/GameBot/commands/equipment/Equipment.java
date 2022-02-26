package me.astri.idleBot.GameBot.commands.equipment;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public class Equipment extends ISlashCommand {
    @Override
    public CommandData getData() {
        return new CommandData("equipment","everything about your equipment")
                .addSubcommands(this.getSubCommandDatas());
    }

    @Override
    protected List<ISlashSubcommand> initSubcommands() {
        return List.of(new Equipment_display(), new Equipment_upgrade());
    }
}
