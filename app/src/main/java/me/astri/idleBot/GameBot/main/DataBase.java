package me.astri.idleBot.GameBot.main;

import me.astri.idleBot.GameBot.Entities.player.BotUser;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.*;
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
            if(event != null)
                event.getHook().sendMessage("Saved!").queue();
        } catch (IOException e) {
            e.printStackTrace();
            if(event != null)
                event.getHook().sendMessage("Error while Saving!").queue();
        }
    }

    //because of an Object to Game cast
    @SuppressWarnings("unchecked")
    public static void load(ButtonClickEvent event) {
        try {
            FileInputStream fis = new FileInputStream(System.getenv("PLAYER_DATA"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            botUsers = (HashMap<String, BotUser>) ois.readObject();
            fis.close();
            ois.close();
            event.getHook().sendMessage("Loaded!").queue();
        } catch (EOFException e) { //file empty
            botUsers = new HashMap<>();
            event.getHook().sendMessage("Loaded! (file was empty)").queue();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            event.getHook().sendMessage("Error while Loading!").queue();
        }
    }

    public static void download(ButtonClickEvent event) {
        event.getHook().sendMessage("players data").addFile(new File(System.getenv("PLAYER_DATA"))).queue();
    }

    public static void upload(ButtonClickEvent event) {
        event.getHook().sendMessage("please send a message with the file attached").queue(msg -> {
            Waiter<GuildMessageReceivedEvent> waiter = new Waiter<>();
            waiter.setEventType(GuildMessageReceivedEvent.class);
            waiter.setExpirationTime(1L,TimeUnit.MINUTES);
            waiter.setConditions(e -> e.getAuthor().equals(event.getUser()) && e.getChannel().equals(event.getChannel()) &&
                !e.getMessage().getAttachments().isEmpty());
            waiter.setFailureAction(ctx -> {
                ctx.unregister();
                event.getHook().sendMessage("Incorrect message").setEphemeral(true).queue();
                ctx.getEvent().getMessage().delete().queue();
            });
            waiter.setAction(ctx -> {
                ctx.getEvent().getMessage().getAttachments().get(0).downloadToFile(System.getenv("PLAYER_DATA"));
                event.getHook().sendMessage("player data uploaded").setEphemeral(true).queue();
                ctx.getEvent().getMessage().delete().queue();
            }).setAutoRemove(true);
            waiter.register("uploadPlayerData");
        });

    }

}