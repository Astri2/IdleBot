package me.astri.idleBot.GameBot.utils;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Lang {
    ENGLISH("English", "game_texts"),
    FRENCH("Français", "French");

    private final String name;
    private final String path;
    public String getName() {
        return this.name;
    }

    Lang(String a, String b) {
        this.name = a;
        this.path =b;
    }

    private static final HashMap<Lang,HashMap<String,String>> langsMap = new HashMap<>();

    public static ArrayList<Command.Choice> getChoices() {
        ArrayList<Command.Choice> choices = new ArrayList<>();
        Arrays.stream(Lang.values()).forEach(lang -> choices.add(new Command.Choice(lang.getName(),lang.name())));
        return choices;
    }

    public String get(String key, String ... variables) {
        if(langsMap.get(this) == null)
            loadLang(this);
        String str = langsMap.get(this).get(key.toLowerCase());
        if(str == null)
            return "`no string`";
        return fixString(str,variables);
    }

    private static final Pattern emotePattern = Pattern.compile("§(.+?)§");
    private static final Pattern varPattern = Pattern.compile("\\{([0-9]+)}");
    private String fixString(String str, String[] variables) {
        str = str.replace("\\n","\n"); //fix multiple lines
        Matcher m = varPattern.matcher(str); //variables
        while(m.find()) {
            str = str.replace(m.group(0),variables[Integer.parseInt(m.group(1))]);
        }
        Matcher m1 = emotePattern.matcher(str); //emotes
        while(m1.find()) {
            str = str.replace(m1.group(0), Emotes.getEmote(m1.group(1)));
        }
        return str;
    }

    private static void loadLang(Lang lang) {
        try {
            ArrayList<List<String>> CSV_lang = Utils.readCSV(Config.get("LANG_PATH") + lang.path + ".csv");
            HashMap<String, String> langMap = new HashMap<>();
            langsMap.put(lang, langMap);
            for (List<String> args : CSV_lang)
                langMap.put(args.get(0), args.get(1));
        } catch(Exception e) {
            System.out.println("can't open file");
        }
    }
}