package me.astri.idleBot.modBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class Wiki {

    public static void handle(GuildMessageReceivedEvent event) {
        event.getMessage().addReaction("⌛").queue();
        String[] args = event.getMessage().getContentRaw().split("\\s+",2);
        if(args.length < 2) return;
        String arg = args[1];
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(0xFEAC01)
                .setFooter("Requested by " + event.getAuthor().getAsTag(),event.getAuthor().getEffectiveAvatarUrl())
                .setThumbnail("https://static.wikia.nocookie.net/idleslayer/images/e/e6/Site-logo.png/revision/latest?cb=20210626192252")
                .setAuthor(String.format("Results for \"%s\"", arg),
                "https://idleslayer.fandom.com/wiki/Special:Search?query="+arg.replace(" ","+"));

        try {
            JSONArray jsonSearch = new JSONObject(readOnlineFile("https://idleslayer.fandom.com/api.php?action=query&format=json&list=search&srprop&srsearch="+arg.replace(" ","_")))
                    .getJSONObject("query").getJSONArray("search");
            for(Object ObjResult : jsonSearch) {
                JSONObject jsonResult = (JSONObject)ObjResult;
                StringBuilder title = new StringBuilder(jsonResult.getString("title"));

                JSONArray jsonSectionSearch = new JSONObject(readOnlineFile("https://idleslayer.fandom.com/api.php?action=parse&format=json&prop=sections&pageid="+jsonResult.get("pageid")))
                        .getJSONObject("parse").getJSONArray("sections");
                for(Object ObjSection : jsonSectionSearch) {
                    JSONObject jsonSection = (JSONObject)ObjSection;
                    if(jsonSection.getString("line").toLowerCase().contains(arg.toLowerCase())) {
                        title.append("#").append(jsonSection.getString("anchor"));
                        break;
                    }
                }
                String link = "https://idleslayer.fandom.com/wiki/"+title.toString().replace(" ","_");
                eb.appendDescription(String.format("• [%s](%s)\n", title,link));
            }


            if(eb.getDescriptionBuilder().isEmpty())
                eb.appendDescription(String.format("Nothing found, please search directly from the [wiki](https://idleslayer.fandom.com/wiki/Special:Search?query=%s)",arg.replace(" ","_")));
            event.getMessage().replyEmbeds(eb.build()).queue();
        } catch(Exception e) {
            e.printStackTrace();
            eb.setDescription(String.format("Error while searching, please search directly from the [wiki](https://idleslayer.fandom.com/wiki/Special:Search?query=%s)",arg.replace(" ","_")));
            event.getMessage().replyEmbeds(eb.build()).queue();
        }
        event.getMessage().removeReaction("⌛",event.getJDA().getSelfUser()).queue();
    }

    private static String readOnlineFile(String url) throws IOException {
        return new String(new URL(url).openStream().readAllBytes());
    }
}