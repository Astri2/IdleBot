package me.astri.idleBot.GameBot.commands.debug;

import me.astri.idleBot.GameBot.Entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.main.DataBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class DebugCommands extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(args[0].equals("i!give"))
            give(event);
        else if (args[0].equals("i!load"))
            load(event);
    }

    private void give(GuildMessageReceivedEvent event) {
        if(!event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser()))
            return;
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(event.getMessage().getMentionedMembers().isEmpty())
            return;
        if(args.length < 3)
            return;
        BigDecimal money;
        try {
            money = BigDecimal.valueOf(Long.parseLong(args[2]));
        } catch(Exception ignore) {return;}
        Member member = event.getMessage().getMentionedMembers().get(0);
        Player player = GameUtils.getUser(null,member);
        player.editCoins(money);

        event.getMessage().addReaction("✅").queue();
    }

    private void load(GuildMessageReceivedEvent event) {
        if(!event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser()))
            return;
        try {
            DataBase.load(null);
            event.getMessage().addReaction("✅").queue();
        } catch (Exception e) {event.getChannel().sendMessage(e.getMessage()).queue();}
    }
}
