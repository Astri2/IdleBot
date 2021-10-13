package me.astri.idleBot.GameBot.main;

import me.astri.idleBot.GameBot.Entities.player.BotUser;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class DataBase extends ListenerAdapter {
    private static HashMap<String, BotUser> botUsers = new HashMap<>();

    public static void registerPlayer(BotUser user) {
        botUsers.put(user.getId(),user);
    }

    public static BotUser getUser(String id) {
        return botUsers.get(id);
    }

    public static void save(@Nullable ButtonClickEvent event) {
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
            BotGame.jda.getTextChannelById(Config.get("SLASH_MANAGER_GUILD_ID")).sendMessage("Error while Saving!").queue();
        }
    }

    //because of an Object to Game cast
    @SuppressWarnings("unchecked")
    public static void load(@Nullable ButtonClickEvent event) {
        System.out.println("loading");
        try {
            FileInputStream fis = new FileInputStream(System.getenv("PLAYER_DATA"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            botUsers = (HashMap<String, BotUser>) ois.readObject();
            fis.close();
            ois.close();
            if(event != null)
                event.getHook().sendMessage("Loaded!").queue();
        } catch (EOFException e) { //file empty
            botUsers = new HashMap<>();
            if(event != null)
                event.getHook().sendMessage("Loaded! (file was empty)").queue();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            BotGame.jda.getTextChannelById(Config.get("SLASH_MANAGER_CHANNEL_ID")).sendMessage("Error while Loading!").queue();
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


    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                save(null);
            }
        },60000,60000);


        //if(event.getJDA().getSelfUser().getId().equals("880922037189771386")) {
            Timer timer1 = new Timer();
            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        event.getJDA().getTextChannelById("897522180005437550")
                                .sendMessage(String.format("<t:%d>", Instant.now().getEpochSecond()))
                                .addFile(new File(System.getenv("PLAYER_DATA"))).queue();
                    } catch(Exception ignored) {}

                }
            },300000,300000);
        //}
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        save(null);
    }
}
