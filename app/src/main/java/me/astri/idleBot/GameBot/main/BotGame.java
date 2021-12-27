package me.astri.idleBot.GameBot.main;

import me.astri.idleBot.GameBot.Entities.upgrade.UpgradeManager;
import me.astri.idleBot.GameBot.commands.*;
import me.astri.idleBot.GameBot.commands.debug.DebugCommands;
import me.astri.idleBot.GameBot.commands.displayCommands.EquipmentDisplay;
import me.astri.idleBot.GameBot.commands.displayCommands.ProfileDisplay;
import me.astri.idleBot.GameBot.commands.other.Register;
import me.astri.idleBot.GameBot.commands.other.Reset;
import me.astri.idleBot.GameBot.commands.settings.setEphemeral;
import me.astri.idleBot.GameBot.commands.settings.setLang;
import me.astri.idleBot.GameBot.commands.settings.setNotation;
import me.astri.idleBot.GameBot.eventWaiter.EventWaiter;
import me.astri.idleBot.GameBot.game.PermaActionComponent;
import me.astri.idleBot.GameBot.slashCommandHandler.SlashCommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class BotGame extends ListenerAdapter {
    public static JDA jda;
    public static SlashCommandManager slashCommandManager;

    public static void startBot(String token) throws LoginException, InterruptedException {
        slashCommandManager = new SlashCommandManager(
                new Register(),
                new EquipmentDisplay(),
                new setLang(),
                new setNotation(),
                new setEphemeral(),
                new ProfileDisplay(),
                new LevelUpEquipment(),
                new Reset(),
                new Upgrades()
        );
        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(
                        new DataBase(),
                        new ControlPanel(),
                        new EventWaiter(),
                        new PermaActionComponent(),
                        new BotGame(),
                        slashCommandManager,

                        //DEBUG
                        new DebugCommands()
                )
                .build().awaitReady();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            Emotes.init();
            UpgradeManager.initUpgrades();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}