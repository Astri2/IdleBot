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

    public static String getNumber(BigDecimal number, BotUser user) {
        return user.usesScNotation() && number.compareTo(BigDecimal.valueOf(1000)) > 0 ?
                getScientistNotation(number) : getCompactNotation(number);
    }

    private static String getScientistNotation(BigDecimal number) {
        return new DecimalFormat("0.###E0").format(number).toLowerCase();
    }

    private static final String[] bigUnits = {"","K","M","B","T","Qa","Qi","Sx","Sp","Oc","No","De","Ud","Dd","Td","Qt",
                                                "Qd","Sd","St","Od","Nd","Vg"};
    private static String getCompactNotation(BigDecimal number) {
        String scNotation = getScientistNotation(number);
        try {
            int powerOfTen = Integer.parseInt(scNotation.replaceAll(".+e","")); //"1.00E9" -> 9
            double value = Double.parseDouble(scNotation.replaceAll("e.+","").replace(",",".")); //"1.00E9" -> 1.00
            String unit = bigUnits[powerOfTen/3]; //unit changes every 3 power of ten: 1 ; 1k ; 1M ; ...
            String tweakedValue = new DecimalFormat("#.##").format(value * Math.pow(10,powerOfTen%3)); //multiply by 1;10;100 because we go from 1 to 999 on each unit
            return tweakedValue + unit;
        } catch(ArrayIndexOutOfBoundsException e) { return scNotation; }//if the unit is above the last planned, use sc notation
    }
}
