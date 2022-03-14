package me.astri.idleBot.GameBot.commands.cookie;

import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Cookie_display extends ISlashSubcommand {
    @Override
    public BaseCommand<CommandData> getData() {
        return new SubcommandData("display","see how many cookies you have");
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Player p = GameUtils.getUser(hook,e.getUser());
        if(p == null) return;

        hook.sendMessage(p.getLang().get("cookie_display",e.getUser().getAsMention(),Integer.toString(p.getCookies().getNumber()))).queue();
    }
}
