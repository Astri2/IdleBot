package me.astri.idleBot;

import me.astri.idleBot.GameBot.main.BotGame;
import me.astri.idleBot.modBot.main.BotMod;

import javax.security.auth.login.LoginException;

public class Main {

    @SuppressWarnings("all") //remove instensiation of utility class warning
    public static void main(String[] args) throws LoginException, InterruptedException {
        try {
            new BotGame();
        } catch(LoginException e) {
            System.out.println(">>> BotGame not connected");
        }
        try {
            new BotMod();
        } catch(LoginException e) {
            System.out.println(">>> BotMod not connected");
        }
    }
}
