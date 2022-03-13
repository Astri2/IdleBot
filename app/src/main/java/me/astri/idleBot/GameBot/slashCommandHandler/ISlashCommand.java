package me.astri.idleBot.GameBot.slashCommandHandler;

import me.astri.idleBot.GameBot.utils.Config;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String[] usersAutho = Config.get("AUTHORIZED_USERS").split(",");
    private static final String[] rolesAutho = Config.get("AUTHORIZED_ROLES").split(",");
    private static final List<CommandPrivilege> defaultPrivileges = new ArrayList<>();
    public List<CommandPrivilege> getCommandPrivileges() {
        if(defaultPrivileges.isEmpty()) {
            for (String roleId : rolesAutho)
                defaultPrivileges.add(CommandPrivilege.enableRole(roleId));
            for (String userId : usersAutho)
                defaultPrivileges.add(CommandPrivilege.enableUser(userId));
        }
        return defaultPrivileges;
    }
}
