package me.astri.idleBot.GameBot.commands.cookie;

import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Cookie_give extends ISlashSubcommand {
    @Override
    public BaseCommand<CommandData> getData() {
        return new SubcommandData("give","give a cookie to someone to thank.")
                .addOption(OptionType.USER,"member","the member you want to give your cookie to",true);
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Player p = GameUtils.getUser(hook,e.getUser());
        if(p == null) return;
        User user = e.getOptions().get(0).getAsUser();
        Player p1 = GameUtils.getUser(hook,e.getUser(),user);
        if(p1 == null) return;

        if(p.getId().equals(p1.getId())) {
            hook.sendMessage(p.getLang().get("cookie_cant_self_give",user.getAsMention())).queue();
        }

        p1.addCookie();

        hook.sendMessage(p1.getLang().get("cookie_gave",user.getAsMention(),e.getUser().getAsMention(),Integer.toString(p1.getCookies()))).queue();
    }

    @Override
    public long getCooldown() {
        return 86400000L; //24h
    }
}
