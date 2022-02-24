package me.astri.idleBot.GameBot.commands.upgrades;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public class Upgrades extends ISlashCommand {
    @Override
    public CommandData getData() {
        return new CommandData("upgrades","everything about upgrades")
                .addSubcommands(this.getSubCommandDatas());
    }

    @Override
    protected List<ISlashSubcommand> initSubcommands() {
        return List.of(new Upgrades_buy());
    }
}
