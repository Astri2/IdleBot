package me.astri.idleBot.main;

import java.util.HashMap;

public class Emotes {
    private static final HashMap<String,String> emotes = new HashMap<>();
    public static String getEmote(String name) {
        String emote = emotes.get(name.toLowerCase());
        return emote == null ? "NA" : emote;
    }

    public static void init() {
        if(emotes.isEmpty())
            Utils.readCSV(Config.get("EMOTE_PATH")).forEach(arg -> emotes.put(arg.get(0),arg.get(1)));
    }
}
