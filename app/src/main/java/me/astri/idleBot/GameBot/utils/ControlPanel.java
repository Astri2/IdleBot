package me.astri.idleBot.GameBot.utils;

import me.astri.idleBot.GameBot.BotGame;
import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.eventWaiter.EventWaiter;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.modBot.main.BotMod;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ControlPanel extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        TextChannel channel = event.getJDA().getGuildById(Config.get("SLASH_MANAGER_GUILD_ID")).getTextChannelById(Config.get("SLASH_MANAGER_CHANNEL_ID"));
        if(channel.getHistory().retrievePast(1).complete().stream()
                .noneMatch(msg -> msg.getContentRaw().equals("Slash Commands controller") && msg.getAuthor().equals(event.getJDA().getSelfUser())))
        {
            MessageAction msg = channel.sendMessage("Slash Commands controller").setActionRows(ActionRow.of(
                    Button.primary("updateJDACommands", "update all JDA commands"),
                    Button.primary("updateGuildCommands", "update all Guild commands"),
                    Button.danger("updateSomeJDACommands", "update specific JDA commands").withDisabled(true),
                    Button.danger("updateSomeGuildCommands", "update specific Guild commands").withDisabled(true)),
                    ActionRow.of(
                        Button.primary("clearJDACommands","Clear all JDA commands"),
                        Button.primary("clearGuildCommands", "Clear all commands from a guild")),
                    ActionRow.of(
                        Button.danger("clearAllGuildCommands", "Clear all commands from all guilds").withDisabled(true),
                        Button.danger("removeSomeGuildCommands", "remove specific commands from a guild").withDisabled(true)),
                    ActionRow.of(
                        Button.success("loadPlayers","Load player progressions"),
                        Button.success("savePlayers","Save player progressions"),
                        Button.success("dlPlayers","⏬ Download player progressions"),
                        Button.success("upPlayers","⏫ Upload player progressions")),
                    ActionRow.of(
                            Button.secondary("shutdown","shutdown the bot"))
            );

            msg.queue();
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if(!event.getChannel().getId().equals(Config.get("SLASH_MANAGER_CHANNEL_ID")))
            return;
        switch (event.getButton().getId()) {
            case "updateJDACommands" -> {
                BotGame.slashCommandManager.updateCommands(event.getJDA());
                event.reply("All global slash commands updated! (may take some time to effectively update to users)").setEphemeral(true).queue();
            }
            case "updateGuildCommands" -> {
                BotGame.slashCommandManager.updateGuildCommands(event.getGuild());
                event.reply("All slash commands updated on that guild!").setEphemeral(true).queue();
            }
            case "clearJDACommands" -> {
                BotGame.slashCommandManager.clearJDACommands(event.getJDA());
                event.reply(" All slash commands updated on that JDA! (may take some time to effectively update to users)").setEphemeral(true).queue();
            }
            case "clearGuildCommands" -> {
                BotGame.slashCommandManager.clearGuildCommands(event.getGuild());
                event.reply("All slash commands cleared of that guild!").setEphemeral(true).queue();
            }
            case "shutdown" -> {
                event.deferReply(true).queue();
                event.getHook().sendMessage("Are you sure?").addActionRow(
                        Button.success("shutdown_confirm_" + event.getId(), "CONFIRM"),
                        Button.danger("shutdown_cancel_" + event.getId(), "CANCEL")
                ).queue(confirmMsg ->
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
                                            ctx.getEvent().reply("Shutdown confirmed.").setEphemeral(true).complete();
                                            ctx.getEvent().getJDA().shutdown();

                                            if(BotMod.jda == null || !BotMod.jda.getStatus().isInit())
                                                System.exit(0);
                                        } else {
                                            confirmMsg.editMessageComponents().queue();
                                            ctx.getEvent().reply("Shutdown canceled").setEphemeral(true).queue();
                                        }
                                    })

                            , "JDA_SHUTDOWN_" + event.getId())
                );
            }



            case "loadPlayers" -> {
                event.deferReply(true).queue();
                DataBase.load(event);
            }
            case "savePlayers" -> {
                event.deferReply(true).queue();
                DataBase.save(event);
            }
            case "dlPlayers" ->  {
                event.deferReply(true).queue();
                DataBase.download(event);
            }
            case "upPlayers" -> {
                event.deferReply(true).queue();
                DataBase.upload(event);
            }
        }
    }
}
