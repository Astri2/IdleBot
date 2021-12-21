package me.astri.idleBot.GameBot.commands.other;

import me.astri.idleBot.GameBot.Entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.main.DataBase;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Reset implements ISlashCommand {

    @Override
    public CommandData getCommandData() {
        return new CommandData("reset","reset your account")
                .addOption(OptionType.BOOLEAN,"ephemeral","only you can see the message");
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Player player = GameUtils.getUser(hook,e.getUser());
        if(player == null)
            return;
        hook.sendMessage(player.getLang().get("progression_reset",e.getUser().getAsMention())).queue();
        DataBase.registerPlayer(new Player(player.getId(),player.getLang(),player.isUseScNotation(),player.isEphemeral()));
    }

    @Override
    public Boolean isEphemeral() {
        return true;
    }
}
