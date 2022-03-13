package me.astri.idleBot.GameBot.slashCommandHandler;

import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.entities.player.BotUser;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SlashCommandManager extends ListenerAdapter {
    private final HashMap<String,ISlashCommand> slashCommands = new HashMap<>();
    private final HashMap<String, HashMap<Long,Long>> cooldowns = new HashMap<>();

    public SlashCommandManager(ISlashCommand ... slashCommand) {
        Arrays.stream(slashCommand).forEach(cmd -> slashCommands.put(cmd.getData().getName(),cmd));
        Arrays.stream(slashCommand).forEach(cmd -> {
            if(cmd.getSubcommands().isEmpty()) {
                cooldowns.put(cmd.getClass().getName(), new HashMap<>());
            } else
                cmd.getSubcommands().values().forEach(subCmd -> cooldowns.put(subCmd.getClass().getName(), new HashMap<>()));
        });
    }

    private ISlashGenericCommand getISlashCommand(String name, String subName) {
            if (subName == null || subName.isEmpty())
                return slashCommands.get(name);
            else
                return slashCommands.get(name).getSubcommands().get(subName);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent e) {
        ISlashGenericCommand slashCommand = getISlashCommand(e.getName(),e.getSubcommandName());

        if(e.getUser().isBot())
            return;

        if(slashCommand == null) {
            e.reply("Command not found").setEphemeral(true).queue();
            return;
        }
        //else
        if(!e.isFromGuild() && slashCommand.guildOnly()) {
            e.reply("That command is guild only!").setEphemeral(true).queue();
            return;
        }
        //else
        if(!slashCommand.hasPermission(e)) {
            e.reply("You don't have permission to perform that command!").setEphemeral(true).queue();
            return;
        }

        long cooldown = getCooldown(e, slashCommand);
        if(cooldown > 0L) {
            e.reply("That command is on cooldown. Please wait " + Utils.timeParser(cooldown, TimeUnit.MILLISECONDS)).setEphemeral(true).queue();
            return;
        }
        boolean ephemeral = slashCommand.isEphemeral();

        BotUser bUser = DataBase.getUser(e.getUser().getId());
        if(bUser != null && !bUser.isEphemeral().equals("default"))
            ephemeral = Boolean.parseBoolean(bUser.isEphemeral());

        if(e.getOption("ephemeral")!= null) ephemeral = Boolean.parseBoolean(e.getOption("ephemeral").getAsString());

        cooldowns.get(slashCommand.getClass().getName()).put(e.getUser().getIdLong(),System.currentTimeMillis());
        e.deferReply(ephemeral).queue();
        try {
            slashCommand.handle(e,e.getHook());
        } catch (Exception exception) {
            exception.printStackTrace(); //TODO exception manager
        }
    }

    private ArrayList<CommandData> getAllCommandData(boolean defaultEnabled) {
        ArrayList<CommandData> list = new ArrayList<>();
        slashCommands.values().forEach(command -> list.add(((CommandData) command.getData()).setDefaultEnabled(defaultEnabled)));

        return list;
    }

    private long getCooldown(SlashCommandEvent e, ISlashGenericCommand command) {
        if(e.getUser().getId().equals(Config.get("BOT_OWNER_ID"))) {
            return -1L;
        }
        if(command.getCooldown() <= 0L) {
            return -1L;
        }
        long time = System.currentTimeMillis();
        Long lastTime = cooldowns.get(command.getClass().getName()).get(e.getUser().getIdLong());
        if(lastTime == null) {
            return -1L;
        }

        return (lastTime + command.getCooldown()) - time;
    }




    /**
     * Add new commands, Delete missing commands, Update edited commands
     * @param jda the JDA bot on which you'll update all commands
     */
    public void updateJDACommands(JDA jda) {
        jda.updateCommands().addCommands(getAllCommandData(true)).queue();
    }

    /**
     * Clear all commands from the JDA
     * @param jda the JBA bot on which you'll clear the commands
     */
    public void clearJDACommands(JDA jda) {
        jda.updateCommands().queue();
    }

    /**
     * Add new commands, Delete missing ones and Update edited ones
     * @param guild the guild you want to update the commands on
     * @param permissions enable or not permission system on this guild
     */
    public void updateGuildCommands(Guild guild, boolean permissions) {
        guild.updateCommands().addCommands(getAllCommandData(!permissions)).complete();
        guild.retrieveCommands().queue(commands -> commands.forEach(command -> {
            System.out.println(command.getName());
            if(permissions) {
                command.updatePrivileges(guild, slashCommands.get(command.getName()).getCommandPrivileges()).queue();
            }
        }));
    }

    /**
     * Clear all commands from a specific Guild
     * @param guild the Guild on which you'll clear all commands
     */
    public void clearGuildCommands(Guild guild) {
        guild.updateCommands().queue();
    }
}
