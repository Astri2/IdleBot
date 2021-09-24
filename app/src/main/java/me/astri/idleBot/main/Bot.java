package me.astri.idleBot.main;

import me.astri.idleBot.commands.*;
import me.astri.idleBot.commands.displayCommands.EquipmentDisplay;
import me.astri.idleBot.commands.displayCommands.ProfileDisplay;
import me.astri.idleBot.eventWaiter.EventWaiter;
import me.astri.idleBot.slashCommandHandler.SlashCommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {
    public static JDA jda;
    public static SlashCommandManager slashCommandManager;

    public static void main(String[] args) throws LoginException, InterruptedException {
        Emotes.init();
        slashCommandManager = new SlashCommandManager(
                new Minions(),
                new Register(),
                new EquipmentDisplay(),
                new setLang(),
                new setNotation(),
                new setEphemeral(),
                new ProfileDisplay(),
                new LevelUpEquipment(),
                new Reset()
        );
        jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .addEventListeners(
                        new Bot(),
                        new controlPanel(),
                        new EventWaiter(),
                        new PermaActionComponent(),
                        slashCommandManager
                )
                .build().awaitReady();
    }
}