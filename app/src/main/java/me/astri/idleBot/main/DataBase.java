package me.astri.idleBot.main;

import me.astri.idleBot.Entities.player.BotUser;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DataBase {
    private static HashMap<String, BotUser> botUsers = new HashMap<>();

    public static void registerPlayer(BotUser user) {
        botUsers.put(user.getId(),user);
    }

    public static BotUser getUser(String id) {
        return botUsers.get(id);
    }

    public static void save(ButtonClickEvent event) {
        try {
            FileOutputStream fos = new FileOutputStream(System.getenv("PLAYER_DATA"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(botUsers);
            oos.flush();
            oos.close();
            event.reply("Saved!").setEphemeral(true).queue();
        } catch (IOException e) {
            e.printStackTrace();
            event.reply("Error while Saving!").setEphemeral(true).queue();
        }
    }

    //because of an Object to Game cast
    @SuppressWarnings("unchecked")
    public static void load(ButtonClickEvent event) {
        boolean success = true;
        try {
            FileInputStream fis = new FileInputStream(System.getenv("PLAYER_DATA"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            botUsers = (HashMap<String, BotUser>) ois.readObject();
            fis.close();
            ois.close();
            event.reply("Loaded!").setEphemeral(true).queue();
        } catch (EOFException e) { //file empty
            botUsers = new HashMap<>();
            event.reply("Loaded! (file was empty)").setEphemeral(true).queue();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            event.reply("Error while Loading!").setEphemeral(true).queue();
        }
    }

}
