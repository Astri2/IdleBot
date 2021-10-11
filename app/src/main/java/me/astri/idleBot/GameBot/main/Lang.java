package me.astri.idleBot.GameBot.main;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Lang {
    ENGLISH("English"),
    FRENCH("Français");

    private final String name;
    public String getName() {
        return this.name;
    }

    Lang(String a) {
        this.name = a;
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
        String str = langsMap.get(this).get(key);
        if(str == null)
            return "`no string`";
        for(int i = 0 ; i < variables.length ; i++)
            str = str.replaceAll("\\{"+i+"}",variables[i]);
            str = str.replaceAll("([^\\\\]|^)[\\[\\]]","$1"); //remove all [] except if backslash before
        return str ;
    }

    private static void loadLang(Lang lang) {
        try {
            ArrayList<List<String>> CSV_lang = Utils.readCSV(Config.get("LANG_PATH") + lang.toString().toLowerCase() + ".csv");
            HashMap<String, String> langMap = new HashMap<>();
            langsMap.put(lang, langMap);
            for (List<String> args : CSV_lang)
                langMap.put(args.get(0), args.get(1));
        } catch(Exception e) {
            System.out.println("can't open file");
        }
    }
}

