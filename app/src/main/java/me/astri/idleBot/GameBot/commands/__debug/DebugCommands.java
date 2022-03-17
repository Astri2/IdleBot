package me.astri.idleBot.GameBot.commands.__debug;

import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.minions.Minion;
import me.astri.idleBot.GameBot.entities.minions.PlayerMinions;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DebugCommands extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().toLowerCase().split("\\s+");
        if(!event.getAuthor().getId().equals(Config.get("BOT_OWNER_ID"))) return;

        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " your helm is now level 50\n-20T").queue();
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setAuthor("<:bar_e1:882933409784147969>",null,event.getAuthor().getAvatarUrl())
                        .setThumbnail(event.getAuthor().getAvatarUrl() + "?size=48")
                        .setDescription(" your helm is now level 50\n-20T")
                .setColor(Color.CYAN).build()).queue();

        if(!event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser())) return;
        switch (args[0]) {
            case "i!give" -> give(event);
            case "i!load" -> load();
            case "i!save" -> save();
            case "i!lprices" -> prices();
            case "i!ping" -> ping(event);
            case "i!emote" -> emote(event);
        }
    }

    private void give(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(event.getMessage().getMentionedMembers().isEmpty())
            return;
        if(args.length < 3)
            return;
        BigNumber money;
        try {
            money = new BigNumber(1.,Long.parseLong(args[2]));
        } catch(Exception ignore) {return;}
        Member member = event.getMessage().getMentionedMembers().get(0);
        Player player = GameUtils.getUser(null,member);
        player.editCoins(money);

        event.getMessage().addReaction("✅").queue();
    }

    private void load() {
        DataBase.load(null);
    }

    private void save() {
        DataBase.save(null);
    }

    private static final HashMap<String,String> basePrices = new HashMap<>()
      {{put("sword","6"); put("shield","50");put("armor","750"); put("helmet","1.6e4");
        put("boots","1.2e5"); put("ring","1.2e6");put("dagger","1.4e7"); put("axe","5.8e8");
        put("staff","5.4e9"); put("bow","3.3e11");put("spellbook","4.6e12"); put("spirit","2.2e14");
        put("necklace","1.5e17");}};

    private void prices() {
        DataBase.getUsers().values().forEach(user -> {
            Player p = (Player) user;
            p.getEquipment().forEach((id, eq) ->
                eq.updatePrices(new BigNumber(basePrices.get(eq.getId()))));
        });
    }

    private void ping(GuildMessageReceivedEvent event) {
        long time = System.currentTimeMillis();
        event.getChannel().sendMessage("Pong !").queue(msg ->
            msg.editMessageFormat("Pong %dms 🏓 !",System.currentTimeMillis()-time).queue()
        );
    }

    private void emote(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        List<Emote> emotes = new ArrayList<>();
        if(args.length < 3) {
            emotes = event.getGuild().getEmotes();

        }
        else for(int i = 2 ; i < args.length ; i++) {
            List<Emote> match = event.getGuild().getEmotesByName(args[i],true);
            if(match.isEmpty()) continue;
            emotes.add(match.get(0));
        }
        StringBuilder str = new StringBuilder("emotes:\n");
        emotes.forEach(emote -> str.append(emote.getAsMention()).append(" \\").append(emote.getAsMention()).append("\n"));
        event.getMessage().reply(str.toString()).queue();
    }
}
