package me.astri.idleBot.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Lang {
    ENGLISH("\uD83C\uDDFA\uD83C\uDDF8","English"),
    FRENCH("\uD83C\uDDEB\uD83C\uDDF7","Français");

    private final String emoji;
    private final String name;
    public String getEmoji() {
        return this.emoji;
    }
    public String getName() {
        return this.name;
    }

    Lang(String a, String b) {
        this.emoji = a;
        this.name = b;
    }

    public static Lang getEnumFromEmoji(String emote) {
        for(Lang language : Lang.values()) {
            if(language.emoji.equalsIgnoreCase(emote))
                return language;
        }
        return null;
    }

    private static final HashMap<Lang,HashMap<String,String>> langsMap = new HashMap<>();

    public static String get(Lang lang, String key, String ... variables) {
        if (lang == null)
            lang = Lang.ENGLISH;

        if(langsMap.get(lang) == null)
            loadLang(lang);
        String str = langsMap.get(lang).get(key);
        if(str == null)
            return "no string";
        for(int i = 0 ; i < variables.length ; i++)
            str = str.replaceAll("\\{"+i+"}",variables[i]);
            str = str.replaceAll("(\\[|])","");
        return str;
    }

    private static void loadLang(Lang lang) {
        ArrayList<List<String>> CSV_lang = Utils.readCSV(Config.get("LANG_PATH") + lang.toString().toLowerCase() + ".csv");
        HashMap<String,String> langMap = new HashMap<>();
        langsMap.put(lang,langMap);
        for(List<String> args : CSV_lang)
            langMap.put(args.get(0),args.get(1));
    }
}

