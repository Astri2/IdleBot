package me.astri.idleBot.GameBot.utils;

public class Config {
    public static String get(String key) {
        return System.getenv(key.toUpperCase());
    }
}
