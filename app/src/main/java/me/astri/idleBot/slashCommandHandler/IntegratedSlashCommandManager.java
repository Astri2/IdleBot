package me.astri.idleBot.slashCommandHandler;

import me.astri.idleBot.main.Bot;
import me.astri.idleBot.main.Config;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.jetbrains.annotations.NotNull;

public class IntegratedSlashCommandManager extends ListenerAdapter {
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
                    Button.danger("updateSomeGuildCommands", "update specific Guild commands").withDisabled(true),
                    Button.primary("clearGuildCommands", "Clear all commands from a guild")), ActionRow.of(
                    Button.danger("clearAllGuildCommands", "Clear all commands from all guilds").withDisabled(true),
                    Button.danger("removeSomeGuildCommands", "remove specific commands from a guild").withDisabled(true)
            ));

            msg.queue();
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if(!event.getChannel().getId().equals(Config.get("SLASH_MANAGER_CHANNEL_ID")))
            return;
        switch (event.getButton().getId()) {
            case "updateJDACommands" -> {
                Bot.slashCommandManager.updateCommands(event.getJDA());
                event.reply("All global slash commands updated! (may take some time to effectively update to users)").setEphemeral(true).queue();
            }
            case "updateGuildCommands" -> {
                Bot.slashCommandManager.updateGuildCommands(event.getGuild());
                event.reply("All slash commands updated on that guild!").setEphemeral(true).queue();
            }
            case "clearGuildCommands" -> {
                Bot.slashCommandManager.clearGuildCommands(event.getGuild());
                event.reply("All slash commands cleared of that guild!").setEphemeral(true).queue();
            }
        }
    }
}
