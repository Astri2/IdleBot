package me.astri.idleBot.GameBot.commands;

import me.astri.idleBot.GameBot.Entities.player.BotUser;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.main.Lang;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class setLang implements ISlashCommand {
    @Override
    public CommandData getCommandData() {

        return new CommandData("lang","choose your language")
                .addOptions(new OptionData(OptionType.STRING,"language","the lang the bot will use will you",true)
                        .addChoices(Lang.getChoices()));
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        BotUser botUser = GameUtils.getUser(hook, e.getUser());
        if(botUser == null)
            return;
        botUser.setLang(Lang.valueOf(e.getOption("language").getAsString()));
        //hook.sendMessage(Lang.get(botUser.getLang(),"lang_updated",e.getUser().getAsMention())).queue();
        hook.sendMessage(botUser.getLang().get("lang_updated",e.getUser().getAsMention())).queue();
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
