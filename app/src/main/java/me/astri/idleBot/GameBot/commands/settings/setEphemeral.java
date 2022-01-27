package me.astri.idleBot.GameBot.commands.settings;

import me.astri.idleBot.GameBot.entities.player.BotUser;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class setEphemeral implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("ephemeral","choose your language")
                .addOptions(new OptionData(OptionType.STRING, "ephemeral","will the bot reply using ephemeral messages (only you can see)",true)
                                .addChoice("True","true")
                                .addChoice("False", "false")
                                .addChoice("Default", "default")
                );
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        BotUser botUser = GameUtils.getUser(hook, e.getUser());
        if(botUser == null)
            return;
        botUser.setEphemeral(e.getOption("ephemeral").getAsString());
        switch(botUser.isEphemeral()) {
            case "true" -> hook.sendMessage(botUser.getLang().get("ephemeral_updated_on",e.getUser().getAsMention())).setEphemeral(true).queue();
            case "false" -> hook.sendMessage(botUser.getLang().get("ephemeral_updated_off",e.getUser().getAsMention())).setEphemeral(false).queue();
            case "default" -> hook.sendMessage(botUser.getLang().get("ephemeral_updated_default",e.getUser().getAsMention())).queue();
        }
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
