package me.astri.idleBot.GameBot.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static String get(String key) {
        return System.getenv(key.toUpperCase());
    }

    public static List<String> getList(String key) {

        String content = get(key);

        // fallback
        if(content == null) return new ArrayList<String>();

        return Arrays.stream(content.split(",")).toList();
    }
}
