package me.astri.idleBot.main;

import me.astri.idleBot.Entities.player.BotUser;

import java.util.HashMap;

public class DataBase {
    private static HashMap<String, BotUser> botUsers = new HashMap<>();

    public static void registerPlayer(BotUser user) {
        botUsers.put(user.getId(),user);
    }

    public static BotUser getUser(String id) {
        return botUsers.get(id);
    }
}
