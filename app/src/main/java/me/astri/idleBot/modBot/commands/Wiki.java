package me.astri.idleBot.modBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wiki {

    private static final Pattern pattern = Pattern.compile("\"title\":\"(.*?)\".*?\"fullurl\":\"(.*?)\"");
    public static void handle(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(args.length < 2) return;
        String arg = args[1];

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(0xFEAC01)
                .setFooter("Requested by" + event.getAuthor().getAsTag(),event.getAuthor().getAvatarUrl())
                .setThumbnail("https://static.wikia.nocookie.net/idleslayer/images/e/e6/Site-logo.png/revision/latest?cb=20210626192252");

        try {
            //getting online data
            URL url = new URL("https://idleslayer.fandom.com/api.php?format=json&action=query&prop=info&inprop=url&generator=allpages&gapfrom="
                    + arg);
            InputStream input = url.openStream();
            String search = new String(input.readAllBytes());
            Matcher results = pattern.matcher(search);

            //parsing data to send it on discord
            eb.setAuthor(String.format("Results for \"%s\"", arg),
                            "https://idleslayer.fandom.com/wiki/Special:Search?query="+arg);
            int i = 0;
            while(results.find()) {
                eb.appendDescription(String.format("• [%s](%s)\n",
                        results.group(i+1),
                        results.group(i+2)));
            }
            if(eb.getDescriptionBuilder().isEmpty())
                eb.appendDescription(String.format("Nothing found, please search directly from the [wiki](https://idleslayer.fandom.com/wiki/Special:Search?query=%s)",arg));
            event.getMessage().replyEmbeds(eb.build()).queue();
        } catch(Exception e) {
            eb.setTitle("Results for %s", arg)
                    .setDescription(String.format("Nothing found, please search directly from the [wiki](https://idleslayer.fandom.com/wiki/Special:Search?query=%s)",arg));
            event.getMessage().replyEmbeds(eb.build()).queue();
        }
    }
}
