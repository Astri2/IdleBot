package me.astri.idleBot.GameBot.commands.settings;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public class Settings extends ISlashCommand {

    @Override
    public CommandData getData() {
        return new CommandData("settings","configure your game")
                .addSubcommands(this.getSubCommandDatas());
    }
    
    @Override
    protected List<ISlashSubcommand> initSubcommands() {
        return List.of(new Settings_setEphemeral(), new Settings_setLang(), new Settings_setNotation());
    }
}
