package me.astri.idleBot.main;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    //public static String get(String key) {
    //    return dotenv.get(key.toUpperCase());
    //}

    public static String get(String key) {
        return System.getenv().get(key.toUpperCase());
    }

}
