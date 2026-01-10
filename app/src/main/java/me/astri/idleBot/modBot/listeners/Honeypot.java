package me.astri.idleBot.modBot.listeners;

import me.astri.idleBot.GameBot.utils.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class Honeypot extends ListenerAdapter {

    private final String _channelId;

    private final HashSet<String> _bypassUsers;
    private final HashSet<String> _bypassRoles;

    public Honeypot() {
        super();
        _channelId = Config.get("HONEYPOT_CHANNEL");

        _bypassUsers = new HashSet<>(Config.getList("HONEYPOT_BYPASS_USERS"));
        _bypassRoles = new HashSet<>(Config.getList("HONEYPOT_BYPASS_ROLES"));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(!event.getChannel().getId().equals(_channelId)) return;

        Guild guild = event.getGuild();

        Member member = event.getMember();
        if(member == null) return; // hopefully won't happen

        // handle bypass features
        if(_bypassUsers.contains(member.getId())) return;
        if(member.getRoles().stream().anyMatch(role -> _bypassRoles.contains(role.getId()))) return;

        try {
            // ban to remove messages
            member.ban(1, "You got trapped D:").queue(
                    ignored -> {
                        // then unban
                        guild.unban(member.getUser()).queue();
                    }
            );
        } catch(InsufficientPermissionException | HierarchyException e) {
            System.out.println("Could not ban the member. Lacking permissions");
        }
    }
}
