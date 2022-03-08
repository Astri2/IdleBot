package me.astri.idleBot.GameBot.game;

import me.astri.idleBot.GameBot.entities.player.BotUser;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.utils.Lang;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public abstract class GameUtils {
    /**
     * @param hook interaction hook
     * @param author author of the interaction
     * @param user the player we want to get
     * @return the specified player or null if that player does not exist
     */
    public static Player getUser(InteractionHook hook, IMentionable author, IMentionable user) {
        Player player = (Player) DataBase.getUser(user.getId());
        if(player == null) {
            if(author == null) {
                hook.sendMessage(Lang.ENGLISH.get("error_you_unregistered", user.getAsMention()))
                        .setEphemeral(true).queue();
            } else if(!author.equals(user)) {
                BotUser playerAuthor = DataBase.getUser(author.getId());
                Lang lang = playerAuthor == null ? Lang.ENGLISH : playerAuthor.getLang();
                hook.sendMessage(lang.get("error_someone_unregistered", user.getAsMention()))
                        .setEphemeral(true).queue();
            }
        }
        return player;
    }

    public static Player getUser(InteractionHook hook, IMentionable author) {
        return getUser(hook,null,author);
    }

}
