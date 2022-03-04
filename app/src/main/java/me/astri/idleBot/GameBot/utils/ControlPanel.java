package me.astri.idleBot.GameBot.utils;

import me.astri.idleBot.GameBot.BotGame;
import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.eventWaiter.EventWaiter;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.modBot.main.BotMod;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ControlPanel extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        TextChannel channel = event.getJDA().getGuildById(Config.get("SLASH_MANAGER_GUILD_ID")).getTextChannelById(Config.get("SLASH_MANAGER_CHANNEL_ID"));
        if(channel.getHistory().retrievePast(1).complete().stream()
                .noneMatch(msg -> msg.getContentRaw().equals("Slash Commands controller") && msg.getAuthor().equals(event.getJDA().getSelfUser()))) {
            //then
            channel.sendMessage("Slash Commands controller").setActionRows(
                    ActionRow.of(
                            Button.success("update_JDA_AllCommands", "Update all JDA commands"),
                            Button.success("update_AllGuilds_AllCommands","Update all commands from all guilds"),
                            Button.success("update_Guilds_AllCommands", "Update all commands from specific guilds")),
                    ActionRow.of(
                            Button.danger("clear_JDA_AllCommands", "Delete all JDA commands"),
                            Button.danger("clear_AllGuilds_AllCommands", "Delete all commands from all guilds"),
                            Button.danger("clear_Guilds_AllCommands", "Delete all commands from specific guilds")),
                    ActionRow.of(
                            Button.primary("loadPlayers","Load player progressions"),
                            Button.primary("savePlayers","Save player progressions"),
                            Button.secondary("upPlayers","⏫ Upload player progressions"),
                            Button.secondary("dlPlayers","⏬ Download player progressions")),
                    ActionRow.of(
                            Button.danger("shutdown","shutdown the bot"))
            ).queue();
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if(!event.getChannel().getId().equals(Config.get("SLASH_MANAGER_CHANNEL_ID")))
            return;

        event.deferReply(true).queue();

        switch (event.getButton().getId()) {
            case "update_JDA_AllCommands" -> {
                BotGame.slashCommandManager.updateJDACommands(event.getJDA());
                event.getHook().sendMessage("All global slash commands updated! (may take some time to effectively update to users)").queue();
            }
            case "update_AllGuilds_AllCommands" -> {
                AtomicBoolean usePermissions = new AtomicBoolean(false);

                askForTogglePermissionsWaiter(event,"all guilds",usePermissions).appendAction(ctx -> {
                    event.getJDA().getGuilds().forEach(guild ->
                            BotGame.slashCommandManager.updateGuildCommands(guild, usePermissions.get())
                    );
                    ctx.getEvent().getHook().sendMessage("All slash commands from all guilds updated! (permissions system: %b)"
                            .formatted(usePermissions)).setEphemeral(true).queue();
                }).register(event.getId());

            }
            case "update_Guilds_AllCommands" -> {
                List<Guild> guilds = new ArrayList<>();
                AtomicBoolean usePermissions = new AtomicBoolean(false);

                askForGuildsWaiter(event,guilds).appendAction(ctx ->
                        guilds.forEach(guild ->
                                askForTogglePermissionsWaiter(ctx.getEvent(), guild.getName(), usePermissions).appendAction(ctx2 -> {
                                    BotGame.slashCommandManager.updateGuildCommands(guild, usePermissions.get());
                                    ctx2.getEvent().getHook().sendMessage("All slash commands from %s updated! (permissions system: %b)"
                                            .formatted(guild.getName(),usePermissions)).setEphemeral(true).queue();
                                }).register(guild.getId())
                        )
                ).register(event.getId());
            }
            case "clear_JDA_AllCommands" -> {
                BotGame.slashCommandManager.clearJDACommands(event.getJDA());
                event.getHook().sendMessage("All global slash commands deleted! (may take some time to effectively update to users)").queue();
            }
            case "clear_AllGuilds_AllCommands" -> {
                event.getJDA().getGuilds().forEach(guild ->
                        BotGame.slashCommandManager.clearGuildCommands(guild)
                );
                event.getHook().sendMessage("All slash commands from all guilds deleted!").queue();
            }
            case "clear_Guilds_AllCommands" -> {
                List<Guild> guilds = new ArrayList<>();
                askForGuildsWaiter(event,guilds).appendAction( ctx -> {
                    guilds.forEach(guild ->
                            BotGame.slashCommandManager.clearGuildCommands(guild)
                    );
                    ctx.getEvent().getHook().sendMessage("All slash commands from selected guilds deleted!").setEphemeral(true).queue();
                }).register(event.getId());
            }

            case "shutdown" -> shutdown(event);

            case "loadPlayers" -> DataBase.load(event);
            case "savePlayers" -> DataBase.save(event);
            case "dlPlayers" -> DataBase.download(event);
            case "upPlayers" -> DataBase.upload(event);
        }
    }

    private static Waiter<ButtonClickEvent> askForTogglePermissionsWaiter(GenericComponentInteractionCreateEvent event, String name, AtomicBoolean togglePerm) {
        Message msg = event.getHook().sendMessage("Enable Permissions for \"%s\"?".formatted(name)).addActionRows(ActionRow.of(
                Button.success("updateGuildCommands_YesPerm","Yes"),
                Button.danger("updateGuildCommands_NoPerm","No")
        )).setEphemeral(true).complete();

        return new Waiter<ButtonClickEvent>()
            .setEventType(ButtonClickEvent.class)
            .setAutoRemove(true)
            .setConditions(e -> e.getMessage().equals(msg))
            .setExpirationTime(1, TimeUnit.MINUTES)
            .setTimeoutAction(() -> msg.editMessageComponents().queue())
            .setAction(ctx -> {
                System.out.println("passe");
                String id = ctx.getEvent().getButton().getId();
                msg.editMessageComponents(
                        ActionRow.of(Button.success("yes", "Yes").asDisabled(), Button.danger("no", "No").asDisabled())).queue();
                if (id.contains("Yes")) {
                    msg.editMessage("Permissions will be enabled for " + name).queue();
                    togglePerm.set(true);
                } else {
                    msg.editMessage("Permissions won't be enabled for " + name).queue();
                    togglePerm.set(false);
                }
            });
    }

    private static Waiter<SelectionMenuEvent> askForGuildsWaiter(ButtonClickEvent event, List<Guild> selectedGuilds) {
        List<Guild> guildList = event.getJDA().getGuilds();
        SelectionMenu guilds = SelectionMenu.create("guildChoice").addOptions(
                guildList.stream().map(g -> SelectOption.of(g.getName(),g.getId())).collect(Collectors.toList())
        ).setRequiredRange(1,guildList.size()).build();

        Message msg = event.getHook().sendMessage("Which guild(s)?").addActionRow(guilds).setEphemeral(true).complete();

        return new Waiter<SelectionMenuEvent>()
            .setEventType(SelectionMenuEvent.class)
            .setAutoRemove(true)
            .setConditions(e -> e.getMessage().equals(msg))
            .setExpirationTime(1, TimeUnit.MINUTES)
            .setTimeoutAction(() -> msg.editMessageComponents().queue())
            .setAction(ctx -> {
                ctx.getEvent().editSelectionMenu(ctx.getEvent().getSelectionMenu().asDisabled()).queue();

                ctx.getEvent().getSelectedOptions().forEach(opt ->
                    selectedGuilds.add(event.getJDA().getGuildById(opt.getValue())));
            });
    }

    private static void shutdown(ButtonClickEvent event) {
        event.getHook().sendMessage("Are you sure?").addActionRow(
                Button.success("shutdown_confirm_" + event.getId(), "CONFIRM"),
                Button.danger("shutdown_cancel_" + event.getId(), "CANCEL")
        ).setEphemeral(true).queue(confirmMsg ->
                EventWaiter.register(new Waiter<ButtonClickEvent>()
                                .setEventType(ButtonClickEvent.class)
                                .setAutoRemove(true)
                                .setConditions(e -> e.getButton().getId().matches("shutdown_(confirm|cancel)_" + event.getId()))
                                .setExpirationTime(1, TimeUnit.MINUTES)
                                .setTimeoutAction(() -> confirmMsg.editMessageComponents().queue())
                                .setAction(ctx -> {
                                    String id = ctx.getEvent().getButton().getId();
                                    if (id.equals("shutdown_confirm_" + event.getId())) {
                                        confirmMsg.editMessageComponents().complete();
                                        ctx.getEvent().reply("Shutdown confirmed.").complete();
                                        ctx.getEvent().getJDA().shutdown();

                                        if(BotMod.jda == null || !BotMod.jda.getStatus().isInit())
                                            System.exit(0);
                                    } else {
                                        confirmMsg.editMessageComponents().queue();
                                        ctx.getEvent().reply("Shutdown canceled").queue();
                                    }
                                })

                        , "JDA_SHUTDOWN_" + event.getId()));
    }
}
