package me.astri.idleBot.GameBot.slashCommandHandler;

import me.astri.idleBot.GameBot.BotGame;
import me.astri.idleBot.GameBot.entities.player.BotUser;
import me.astri.idleBot.GameBot.eventWaiter.EventWaiter;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.utils.Utils;
import me.astri.idleBot.modBot.main.BotMod;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
            e.reply("That command is on cooldown. Please wait " + Utils.timeParser(cooldown)).setEphemeral(true).queue();
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

    private ArrayList<CommandData> getAllCommandData() {
        ArrayList<CommandData> list = new ArrayList<>();
        slashCommands.values().forEach(command -> list.add((CommandData) command.getData()));

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
    public void updateCommands(JDA jda) {
        jda.updateCommands().addCommands(getAllCommandData()).queue();
    }

    /**
     * Add new commands, Delete missing commands, Update edited commands
     * @param guild the Guild on which you'll update all commands
     */
    public void updateGuildCommands(Guild guild, InteractionHook hook) {
        guild.updateCommands().addCommands(getAllCommandData()).complete();
        hook.sendMessage("Enable Permissions for \"%s\"?".formatted(guild.getName())).setEphemeral(true).addActionRows(ActionRow.of(
                Button.success("updateGuildCommands_%s_confirm".formatted(guild.getId()),"Yes"),
                Button.danger("updateGuildCommands_%s_cancel".formatted(guild.getId()),"No")
        )).queue(msg ->
            EventWaiter.register(new Waiter<ButtonClickEvent>()
                .setEventType(ButtonClickEvent.class)
                .setAutoRemove(true)
                .setConditions(e -> e.getButton().getId().startsWith("updateGuildCommands_" + guild.getId()))
                .setExpirationTime(1, TimeUnit.MINUTES)
                .setTimeoutAction(() -> msg.editMessageComponents().queue())
                .setAction(ctx -> {
                    String id = ctx.getEvent().getButton().getId();
                    msg.editMessageComponents(
                            ActionRow.of(Button.success("yes","Yes").asDisabled(),Button.danger("no","No").asDisabled())).queue();
                    if (id.contains("confirm")) {
                        ctx.getEvent().editMessage("Permissions will be enabled for " + guild.getName()).queue();
                        guild.retrieveCommands().queue(commands -> commands.forEach(command -> {
                            System.out.println(command.getName());

                            command.editCommand().setDefaultEnabled(false).queue();
                            command.updatePrivileges(guild, slashCommands.get(command.getName()).getCommandPrivileges()).queue();
                        }));
                    } else {
                        ctx.getEvent().editMessage("Permissions won't be enabled for " + guild.getName()).queue();
                    }
                }),"updateGuildCommands_" + guild.getId()
            )
        );
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
