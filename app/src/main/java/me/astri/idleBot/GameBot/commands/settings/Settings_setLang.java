package me.astri.idleBot.GameBot.commands.settings;

import me.astri.idleBot.GameBot.entities.player.BotUser;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import me.astri.idleBot.GameBot.utils.Lang;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Settings_setLang extends ISlashSubcommand {
    @Override
    public SubcommandData getData() {
        return new SubcommandData("lang","choose your language")
                .addOptions(new OptionData(OptionType.STRING,"language","the lang the bot will use will you",true)
                        .addChoices(Lang.getChoices()));
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        BotUser botUser = GameUtils.getUser(hook, e.getUser());
        if(botUser == null)
            return;
        botUser.setLang(Lang.valueOf(e.getOption("language").getAsString()));
        hook.sendMessage(botUser.getLang().get(
                "lang_updated",e.getUser().getAsMention(),botUser.getLang().getName())).queue();
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
