package me.astri.idleBot.main;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static boolean isInt(String input) {
        return input.matches("-?[0-9]+");
    }

    public static ArrayList<List<String>> readCSV(String key) {
        String path = Config.get(key.toUpperCase());
        ArrayList<List<String>> records = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("#")) continue;
                line = line.replaceAll("([ ]*,[ ]*)",",");
                String[] values = line.split(",");
                records.add(Arrays.stream(values).toList());
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static void addReactions(Message message, String ... reactions) {
        Arrays.stream(reactions).forEach(reaction -> message.addReaction(reaction).queue());
    }

    public static boolean hasBotReacted(MessageReactionAddEvent e) {
        return e.getReaction().retrieveUsers().complete().contains(e.getJDA().getSelfUser());
    }

    public static String timeParser(long ms) {
        ms = (long) Math.ceil(ms/1000.);
        StringBuilder str = new StringBuilder();
        long days = ms / 86400; if(days > 0) str.append(days).append("d");
        long hours = ms / 3600 % 24; if(hours > 0) str.append(hours).append("h");
        long minutes = ms / 60 % 60; if(minutes > 0) str.append(minutes).append("m");
        long seconds = ms % 60; if(seconds > 0) str.append(seconds).append("s");
        return str.toString();
    }
}
