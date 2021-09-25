package me.astri.idleBot;

import me.astri.idleBot.GameBot.main.BotGame;
import me.astri.idleBot.modBot.main.BotMod;

import javax.security.auth.login.LoginException;

public class Main {

    @SuppressWarnings("all") //remove instensiation of utility class warning
    public static void main(String[] args) throws LoginException, InterruptedException {
        new BotGame();
        new BotMod();
    }
}
