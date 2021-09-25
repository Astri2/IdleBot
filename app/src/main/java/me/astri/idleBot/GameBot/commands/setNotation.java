package me.astri.idleBot.GameBot.commands;

import me.astri.idleBot.GameBot.Entities.player.BotUser;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class setNotation implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("notation","choose your number notation")
                .addOptions(
                        new OptionData(OptionType.STRING, "notation","which notation to use",true)
                            .addChoice("scientific notation","sc")
                            .addChoice("units notation", "un")
                );
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        BotUser botUser = GameUtils.getUser(hook, e.getUser());
        if(botUser == null)
            return;
        setNot(hook,botUser,e.getOption("notation").getAsString().equals("sc"));
    }

    public void setNot(InteractionHook hook, BotUser botUser, boolean useScNot) {
        botUser.setUseScNotation(useScNot);
        hook.sendMessage(botUser.getLang().get(
                useScNot ? "notation_updated_sc" : "notation_updated_unit",hook.getInteraction().getUser().getAsMention())).queue();
    }

    @Override
    public Boolean isEphemeral() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 60000L;
    }
}
