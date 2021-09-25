package me.astri.idleBot.modBot.main;

import me.astri.idleBot.GameBot.main.Config;
import me.astri.idleBot.modBot.listeners.VoiceRole;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class BotMod {
    public static JDA jda;

    public BotMod() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(Config.get("TOKEN_2"))
                .addEventListeners(
                        new VoiceRole()
                )
                .build().awaitReady();
    }
}
