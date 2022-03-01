package me.astri.idleBot.GameBot.slashCommandHandler;

import me.astri.idleBot.GameBot.utils.Config;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ISlashCommand extends ISlashGenericCommand{
    private final HashMap<String,ISlashSubcommand> subCommands = new HashMap<>();

    protected List<SubcommandData> getSubCommandDatas() {
        return this.subCommands.values().stream().map(slashSubCommand -> (SubcommandData) slashSubCommand.getData()).toList();
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {}

    public final Map<String,ISlashSubcommand> getSubcommands() {
        return this.subCommands;
    }

    public ISlashCommand(ISlashSubcommand ... subs) {
        Stream.of(subs).forEach(sub -> subCommands.put(sub.getData().getName(),sub));
    }

    private static final String[] bypass = Config.get("BYPASS_PERMISSION").split(";");
    private static final List<CommandPrivilege> defaultPrivileges = new ArrayList<>();
    public List<CommandPrivilege> getCommandPrivileges() {
        if(defaultPrivileges.isEmpty()) {
            defaultPrivileges.add(CommandPrivilege.enableRole("725178568107098152"));
            for (String userid : bypass)
                defaultPrivileges.add(CommandPrivilege.enableUser(userid));
        }
        return defaultPrivileges;
    }
}
