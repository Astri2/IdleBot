package me.astri.idleBot.GameBot.commands.equipments;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public class Equipments extends ISlashCommand {
    @Override
    public CommandData getData() {
        return new CommandData("equipments","everything about your equipment")
                .addSubcommands(this.getSubCommandDatas());
    }

    @Override
    protected List<ISlashSubcommand> initSubcommands() {
        return List.of(new Equipments_display(), new Equipments_upgrade());
    }
}
