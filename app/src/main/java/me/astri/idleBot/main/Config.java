package me.astri.idleBot.main;

public class Config {
    public static String get(String key) {
        return System.getenv().get(key.toUpperCase());
    }
}
