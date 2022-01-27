package me.astri.idleBot;

import me.astri.idleBot.GameBot.BotGame;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.modBot.main.BotMod;

import javax.security.auth.login.LoginException;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            BotGame.startBot(Config.get("TOKEN_1"));
        } catch(LoginException e) {
            System.out.println(">>> BotGame not connected");
        }
        try {
            BotMod.startBot(Config.get("TOKEN_2"));
        } catch(LoginException e) {
            System.out.println(">>> BotMod not connected");
        }
    }
}
