package me.astri.idleBot.modBot.main;

import me.astri.idleBot.modBot.listeners.Commands;
import me.astri.idleBot.modBot.listeners.VoiceRole;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.math.BigInteger;

public class BotMod {
    public static JDA jda;

    public static void startBot(String token) throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(token)
                .addEventListeners(
                        new VoiceRole(),
                        new Commands()
                )
                .build().awaitReady();
    }
}
