package me.astri.idleBot.commands;

import me.astri.idleBot.Entities.player.BotUser;
import me.astri.idleBot.game.GameUtils;
import me.astri.idleBot.main.Lang;
import me.astri.idleBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class setLang implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("lang","choose your language")
                .addOptions(new OptionData(OptionType.STRING,"language","the lang the bot will use will you",true)
                        .addChoices(
                                new Command.Choice("English","ENGLISH"),
                                new Command.Choice("Français","FRENCH")));
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        BotUser botUser = GameUtils.getUser(hook, e.getUser());
        if(botUser == null)
            return;
        botUser.setLang(Lang.valueOf(e.getOption("language").getAsString()));
        hook.sendMessage(Lang.get(botUser.getLang(),"lang_updated",e.getUser().getAsMention())).queue();
    }

    @Override
    public Boolean isEphemeral() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 300000L;
    }
}
