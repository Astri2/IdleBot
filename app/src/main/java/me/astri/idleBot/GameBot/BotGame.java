package me.astri.idleBot.GameBot;

import me.astri.idleBot.GameBot.commands.__debug.DebugCommands;
import me.astri.idleBot.GameBot.commands.equipment.Equipment;
import me.astri.idleBot.GameBot.commands.equipment.Equipment_display;
import me.astri.idleBot.GameBot.commands.equipment.Equipment_upgrade;
import me.astri.idleBot.GameBot.commands.noCategory.*;
import me.astri.idleBot.GameBot.commands.settings.Settings;
import me.astri.idleBot.GameBot.commands.settings.Settings_setEphemeral;
import me.astri.idleBot.GameBot.commands.settings.Settings_setLang;
import me.astri.idleBot.GameBot.commands.settings.Settings_setNotation;
import me.astri.idleBot.GameBot.commands.upgrades.Upgrades;
import me.astri.idleBot.GameBot.commands.upgrades.Upgrades_buy;
import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.entities.upgrade.management.UpgradeManager;
import me.astri.idleBot.GameBot.eventWaiter.EventWaiter;
import me.astri.idleBot.GameBot.game.PermaActionComponent;
import me.astri.idleBot.GameBot.slashCommandHandler.SlashCommandManager;
import me.astri.idleBot.GameBot.utils.ControlPanel;
import me.astri.idleBot.GameBot.utils.Emotes;
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
                //subcommands
                new Equipment(
                        new Equipment_display(),
                        new Equipment_upgrade()
                ),
                new Upgrades(
                        new Upgrades_buy()
                ),
                new Settings(
                        new Settings_setEphemeral(),
                        new Settings_setLang(),
                        new Settings_setNotation()
                ),

                //regular commands
                new Profile(),
                new Register(),
                new Reset(),

                //other
                new ChestHunt(),
                new Minions()
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