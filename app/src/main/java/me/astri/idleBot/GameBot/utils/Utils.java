package me.astri.idleBot.GameBot.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public abstract class Utils {
    public static ArrayList<List<String>> readCSV(String path) {
        ArrayList<List<String>> records = new ArrayList<>();
        try {
            Files.readAllLines(Path.of(path)).forEach(line -> {
                if(line.startsWith("#")) return;
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static String readFile(String path) {
        try {
            return Files.readString(Path.of(path));
        }
        catch(IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void addReactions(Message message, String ... reactions) {
        Arrays.stream(reactions).forEach(reaction -> message.addReaction(reaction).queue());
    }

    public static boolean hasBotReacted(MessageReactionAddEvent e) {
        return e.getReaction().retrieveUsers().complete().contains(e.getJDA().getSelfUser());
    }

    public static String timeParser(long amount, TimeUnit unit) {
        long sec = TimeUnit.SECONDS.convert(amount,unit);
        StringBuilder str = new StringBuilder();
        long days = sec / 86400; if(days > 0) str.append(days).append("d");
        long hours = sec / 3600 % 24; if(hours > 0) str.append(hours).append("h");
        long minutes = sec / 60 % 60; if(minutes > 0) str.append(minutes).append("m");
        long seconds = sec % 60; if(seconds > 0) str.append(seconds).append("s");
        return str.toString();
    }
}
