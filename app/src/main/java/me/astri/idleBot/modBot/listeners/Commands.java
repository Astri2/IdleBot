package me.astri.idleBot.modBot.listeners;

import me.astri.idleBot.modBot.commands.Wiki;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Commands extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String cmd = event.getMessage().getContentRaw().split("\\s+")[0].toLowerCase();
        if ("wiki".equals(cmd)) {
            Wiki.handle(event);
        }
    }
}
