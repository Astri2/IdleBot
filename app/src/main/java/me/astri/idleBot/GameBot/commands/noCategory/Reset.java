package me.astri.idleBot.GameBot.commands.noCategory;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Reset extends ISlashCommand {

    @Override
    public CommandData getData() {
        return new CommandData("reset","reset your account")
                .addOption(OptionType.BOOLEAN, "onlymoney", "will only reset the money",true)
                .addOption(OptionType.BOOLEAN,"ephemeral","only you can see the message");
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Player player = GameUtils.getUser(hook,e.getUser());
        if(player == null)
            return;
        if(e.getOption("onlymoney").getAsBoolean()) {
            player.editCoins(BigNumber.negate(player.getCoins()));
        }
        else {
            DataBase.registerPlayer(new Player(player.getId(), player.getLang(), player.usesScNotation(), player.isEphemeral()));
        }
        hook.sendMessage(player.getLang().get("progression_reset", e.getUser().getAsMention())).queue();
    }

    @Override
    public Boolean isEphemeral() {
        return true;
    }
}
