package me.astri.idleBot.GameBot.main;

public class Config {
    public static String get(String key) {
        return System.getenv().get(key.toUpperCase());
    }
}
