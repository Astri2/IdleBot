package me.astri.idleBot.modBot.listeners;

import me.astri.idleBot.GameBot.utils.Config;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VoiceRole extends ListenerAdapter {
    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        if(!event.getGuild().getId().equals("724614126780809337"))
            return;
        event.getGuild().addRoleToMember(event.getEntity(),event.getGuild().getRoleById(Config.get("VC_ROLE_ID"))).queue();
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if(!event.getGuild().getId().equals("724614126780809337"))
            return;
        event.getGuild().removeRoleFromMember(event.getEntity(),event.getGuild().getRoleById(Config.get("VC_ROLE_ID"))).queue();
    }
}
