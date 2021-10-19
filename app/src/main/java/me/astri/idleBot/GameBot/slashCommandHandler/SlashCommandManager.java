package me.astri.idleBot.GameBot.slashCommandHandler;

import me.astri.idleBot.GameBot.Entities.player.BotUser;
import me.astri.idleBot.GameBot.main.Config;
import me.astri.idleBot.GameBot.main.DataBase;
import me.astri.idleBot.GameBot.main.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SlashCommandManager extends ListenerAdapter {
    private final List<ISlashCommand> slashCommands = new ArrayList<>();
    private final HashMap<String, HashMap<Long,Long>> cooldowns = new HashMap<>();

    public SlashCommandManager(ISlashCommand ... slashCommand) {
        Collections.addAll(slashCommands, slashCommand);
        Arrays.stream(slashCommand).forEach(cmd -> cooldowns.put(cmd.getCommandData().getName(),new HashMap<>()));
    }

    private ISlashCommand getISlashCommand(String name) {
        for(ISlashCommand slashCommand : slashCommands) {
            if(name.equals(slashCommand.getCommandData().getName()))
                return slashCommand;
        }
        return null;
    }
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent e) {
        ISlashCommand slashCommand = getISlashCommand(e.getName());

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
            e.reply("That command is on cooldown. Please wait " + Utils.timeParser(cooldown)).setEphemeral(true).queue();
            return;
        }
        boolean ephemeral = slashCommand.isEphemeral();

        BotUser bUser = DataBase.getUser(e.getUser().getId());
        if(bUser != null && !bUser.isEphemeral().equals("default"))
            ephemeral = Boolean.parseBoolean(bUser.isEphemeral());

        if(e.getOption("ephemeral")!= null) ephemeral = Boolean.parseBoolean(e.getOption("ephemeral").getAsString());

        cooldowns.get(slashCommand.getCommandData().getName()).put(e.getUser().getIdLong(),System.currentTimeMillis());
        e.deferReply(ephemeral).queue();
        slashCommand.handle(e,e.getHook());
    }

    private ArrayList<CommandData> getAllCommandData() {
        ArrayList<CommandData> list = new ArrayList<>();
        slashCommands.forEach(command -> list.add(command.getCommandData()));

        return list;
    }

    private long getCooldown(SlashCommandEvent e, ISlashCommand command) {
        if(e.getUser().getId().equals(Config.get("BOT_OWNER_ID"))) {
            return -1L;
        }
        if(command.getCooldown() <= 0L)
            return -1L;

        long time = System.currentTimeMillis();
        Long lastTime = cooldowns.get(command.getCommandData().getName()).get(e.getUser().getIdLong());
        if(lastTime == null)
            return -1L;

        return (lastTime + command.getCooldown()) - time;
    }

    /**
     * Add new commands, Delete missing commands, Update edited commands
     * @param jda the JDA bot on which you'll update all commands
     */
    public void updateCommands(JDA jda) {
        jda.updateCommands().addCommands(getAllCommandData()).queue();
    }

    /**
     * Add new commands, Delete missing commands, Update edited commands
     * @param guild the Guild on which you'll update all commands
     */
    public void updateGuildCommands(Guild guild) {
        guild.updateCommands().addCommands(getAllCommandData()).queue();
    }

    /**
     * Add or Update specific commands on the whole Bot
     * @param jda the JDA bot on which you'll updates the commands
     * @param command list of commands you want to Add or Update
     */
    public void updateCommands(JDA jda, String ... command) {
        Arrays.stream(command).forEach(cmd -> jda.upsertCommand(getISlashCommand(cmd).getCommandData()).queue());
    }

    /**
     * Add or Update specific commands on a specific Guild
     * @param guild the Guild on which you'll updates the commands
     * @param command list of commands you want to Add or Update
     */
    public void updateGuildCommands(Guild guild, String ... command) {
        Arrays.stream(command).forEach(cmd -> guild.upsertCommand(getISlashCommand(cmd).getCommandData()).queue());
    }


    /**
     * Clear all commands from the JDA
     * @param jda the JBA bot on which you'll clear the commands
     */
    public void clearJDACommands(JDA jda) {
        jda.updateCommands().queue();
    }

    /**
     * Clear all commands from a specific Guild
     * @param guild the Guild on which you'll clear all commands
     */
    public void clearGuildCommands(Guild guild) {
        guild.updateCommands().queue();
    }


    /**
     * Clear all Guild commands from all Guilds
     * @param jda the JDA bot on which you'll clear the Guild commands
     */
    public void clearAllGuildCommands(JDA jda) { jda.getGuilds().forEach(guild -> guild.updateCommands().queue());}

    /**
     * Remove specific commands from a specific Guild
     * @param guild the Guild on which you'll clear the commands
     * @param command list of commands you want to remove
     */
    public void removeGuildCommands(Guild guild, String ... command) {
        if(command.length == 0)
            return;
        List<String> commandsToRemove = Arrays.asList(command);
        guild.retrieveCommands().queue(guildCommands ->
            guildCommands.forEach(cmd -> {
                if(commandsToRemove.contains(cmd.getName()))
                    guild.deleteCommandById(cmd.getId()).queue();
            }));
    }
}
