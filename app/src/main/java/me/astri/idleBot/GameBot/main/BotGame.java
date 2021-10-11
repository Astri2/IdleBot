package me.astri.idleBot.GameBot.main;

import me.astri.idleBot.GameBot.commands.*;
import me.astri.idleBot.GameBot.commands.debug.Give;
import me.astri.idleBot.GameBot.commands.displayCommands.EquipmentDisplay;
import me.astri.idleBot.GameBot.commands.displayCommands.ProfileDisplay;
import me.astri.idleBot.GameBot.eventWaiter.EventWaiter;
import me.astri.idleBot.GameBot.game.PermaActionComponent;
import me.astri.idleBot.GameBot.slashCommandHandler.SlashCommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class BotGame {
    public static JDA jda;
    public static SlashCommandManager slashCommandManager;

    public BotGame() throws LoginException, InterruptedException {
        Emotes.init();
        slashCommandManager = new SlashCommandManager(
                new Register(),
                new EquipmentDisplay(),
                new setLang(),
                new setNotation(),
                new setEphemeral(),
                new ProfileDisplay(),
                new LevelUpEquipment(),
                new Reset()
        );
        jda = JDABuilder.createDefault(Config.get("TOKEN_1"))
                .addEventListeners(
                        new DataBase(),
                        new ControlPanel(),
                        new EventWaiter(),
                        new PermaActionComponent(),
                        slashCommandManager,

                        //DEBUG
                        new Give()
                )
                .build().awaitReady();
    }
}