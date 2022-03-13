package me.astri.idleBot.GameBot.commands.cookie;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Cookie extends ISlashCommand {

    public Cookie(ISlashSubcommand... subcommands) { super(subcommands); }

    @Override
    public BaseCommand<CommandData> getData() {
        return new CommandData("cookie","Cookies probably are the most important think in the world!")
                .addSubcommands(this.getSubCommandDatas());
    }
}
