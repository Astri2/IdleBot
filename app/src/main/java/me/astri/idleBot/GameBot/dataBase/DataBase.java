package me.astri.idleBot.GameBot.dataBase;

import me.astri.idleBot.GameBot.entities.player.BotUser;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.GameBot.BotGame;
import me.astri.idleBot.GameBot.utils.Config;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class DataBase extends ListenerAdapter {
    public static HashMap<String, BotUser> botUsers = new HashMap<>();

    public static void setUsers(HashMap<String, BotUser> users) {
        botUsers = users;
    }

    public static HashMap<String, BotUser> getUsers() {
        return botUsers;
    }

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
    public static int load(@Nullable ButtonClickEvent event) {
        try {
            FileInputStream fis = new FileInputStream(System.getenv("PLAYER_DATA"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            botUsers = (HashMap<String, BotUser>) ois.readObject();
            fis.close();
            ois.close();
            if(event != null)
                event.getHook().sendMessage("Loaded!").queue();
        } catch (EOFException e) { //file empty
            System.err.println("Warning - File empty");
            botUsers = new HashMap<>();
            if(event != null)
                event.getHook().sendMessage("Loaded! (file was empty)").queue();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            if(event != null)
                event.getHook().sendMessage("Error while Loading!").queue();
            return -1;
        }
        return 0;
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

    //@Override
    public void TEMPonReady(@NotNull ReadyEvent event) {
       try {
            if(load(null) != 0) {
                System.out.println("loading backup");
                event.getJDA().getTextChannelById(Config.get("BACKUP_CHANNEL"))
                    .getHistory().retrievePast(1).queue(history ->
                        history.get(0).getAttachments().get(0)
                            .downloadToFile(Config.get("PLAYER_DATA"))
                                .thenRun(() -> load(null))
                    )
                ;
            }
        } catch(Exception ignore) {} //no backup files detected

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                save(null);
            }
        },60000,60000);

        Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String path = System.getenv("PLAYER_DATA");
                    if(Files.size(Path.of(path)) > 100)
                        event.getJDA().getTextChannelById(Config.get("BACKUP_CHANNEL"))
                            .sendMessage(String.format("<t:%d>\n%d",
                                Instant.now().getEpochSecond(),
                                Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))
                            .addFile(new File(path)).queue();
                } catch(Exception ignored) {}
            }
        },300000,300000);
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        save(null);
    }
}
