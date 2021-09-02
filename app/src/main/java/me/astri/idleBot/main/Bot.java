package me.astri.idleBot.main;

import me.astri.idleBot.commands.Minions;
import me.astri.idleBot.slashCommandHandler.IntegratedSlashCommandManager;
import me.astri.idleBot.slashCommandHandler.SlashCommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot {
    public static JDA jda;
    public static SlashCommandManager slashCommandManager;

    public static void main(String[] args) throws LoginException, InterruptedException {
        slashCommandManager = new SlashCommandManager(
                new Minions()
        );

        jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(
                        new IntegratedSlashCommandManager(),
                        slashCommandManager
                )
                .build().awaitReady();
    }
}