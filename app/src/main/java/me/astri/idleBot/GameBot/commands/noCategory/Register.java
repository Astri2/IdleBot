package me.astri.idleBot.GameBot.commands.noCategory;

import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.utils.Lang;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Register extends ISlashCommand {
    @Override
    public CommandData getData() {
        return new CommandData("register","register yourself to the game")
                .addOptions(
                        new OptionData(OptionType.STRING,"language","the lang the bot will use will you")
                        .addChoices(Lang.getChoices()))

                .addOptions(
                        new OptionData(OptionType.STRING, "notation","which notation to use")
                                .addChoice("scientific notation","sc")
                                .addChoice("units notation", "un")
                )
                .addOptions(
                        new OptionData(OptionType.STRING, "ephemeral","will the bot reply using ephemeral messages (only you can see)")
                                .addChoice("True","true")
                                .addChoice("False", "false")
                                .addChoice("Default", "default")
                );
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Lang lang = null;
        boolean scNotation = false;
        String ephemeral = "default";
        if(e.getOption("language") != null) {
            lang = Lang.valueOf(e.getOption("language").getAsString());
        }
        if(e.getOption("notation") != null)
            scNotation = e.getOption("notation").getAsString().equals("sc");

        if(e.getOption("ephemeral") != null)
            ephemeral = e.getOption("ephemeral").getAsString();


        Player player = new Player(e.getUser().getId(), lang, scNotation, ephemeral);
        if(DataBase.getUser(player.getId()) == null) {
            DataBase.registerPlayer(player);
            hook.sendMessage(player.getLang().get("success_register",e.getUser().getAsMention())).queue();
        } else {
            hook.sendMessage(GameUtils.getUser(hook,e.getUser()).getLang().get("error_already_registered", e.getUser().getAsMention())).queue();
        }
    }

    @Override
    public long getCooldown() {
        return 300000L;
    }
}
