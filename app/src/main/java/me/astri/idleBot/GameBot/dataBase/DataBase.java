package me.astri.idleBot.GameBot.dataBase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.astri.idleBot.GameBot.BotGame;
import me.astri.idleBot.GameBot.dataBase.Gson.GsonIgnoreStrategy;
import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.ColorEnum;
import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.player.BotUser;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.utils.Emotes;
import me.astri.idleBot.GameBot.utils.Utils;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
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
            HashMap<String,BotUser> playerList = DataBase.getUsers();
            final GsonBuilder builder = new GsonBuilder();
            builder.setExclusionStrategies(new GsonIgnoreStrategy());
            final Gson gson = builder.create();
            final String json = gson.toJson(playerList);

            FileWriter myWriter = new FileWriter(Config.get("PLAYER_DATA"));
            myWriter.write(json);
            myWriter.close();

            if(event != null)
                event.getHook().sendMessage("Saved!").queue();
        } catch (IOException e) {
            e.printStackTrace();
            BotGame.jda.getTextChannelById(Config.get("SLASH_MANAGER_GUILD_ID")).sendMessage("Error while Saving!").queue();
        }
    }

    public static int load(@Nullable ButtonClickEvent event) {
        try {
            String json = Utils.readFile(Config.get("PLAYER_DATA"));
            if(json.isEmpty())
            {
                System.err.println("Warning - File empty or non existent");
                botUsers = new HashMap<>();
                if(event != null)
                    event.getHook().sendMessage("Loaded! (file was empty)").queue();
                return 1;
            }
            Gson gson = new GsonBuilder().create();
            Type classType = new TypeToken<HashMap<String, Player>>() {}.getType();
            botUsers = gson.fromJson(json, classType);

            initNulls();

            System.out.println("Successfully load from the file.");
            if(event != null)
                event.getHook().sendMessage("Loaded!").queue();
        } catch (Exception e) {
            e.printStackTrace();
            if(event != null)
                event.getHook().sendMessage("Error while Loading!").queue();
            return -1;
        }
        return 0;
    }

    private static void initNulls() {
        botUsers.values().forEach(user -> {
            Player p = (Player)user;
            if(p.color == null)
                p.color = ColorEnum.RED;
        });
    }

    public static void download(ButtonClickEvent event) {
        event.getHook().sendMessage("players data").addFile(new File(Config.get("PLAYER_DATA"))).queue();
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
       try {
            if(load(null) != 0) { // => if it can't find the save file
                System.out.println("loading backup");
                event.getJDA().getTextChannelById(Config.get("BACKUP_CHANNEL"))
                    .getHistory().retrievePast(1).queue(history ->
                        history.get(0).getAttachments().get(0)
                            .downloadToFile(Config.get("PLAYER_DATA"))
                                .thenRun(() -> load(null))
                    )
                ;
            }

           Arrays.stream(Config.get("REBOOT_CHANNEL").split(",")).forEach(channelId ->
               event.getJDA().getTextChannelById(channelId).sendMessage("Bot just rebooted, up to 5 minutes of progress may be lost :(").queue());
        } catch(Exception ignore) {} //no backup files detected

        Timer saver = new Timer();
        saver.schedule(new TimerTask() {
            @Override
            public void run() {
                save(null);
            }
        },60000,60000);

        Timer backupMaker = new Timer();
        backupMaker.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String path = System.getenv("PLAYER_DATA");
                    if(Files.size(Path.of(path)) > 100)
                        event.getJDA().getTextChannelById(Config.get("BACKUP_CHANNEL"))
                            .sendMessage(String.format("<t:%d>",
                                Instant.now().getEpochSecond()))
                            .addFile(new File(path)).queue();
                } catch(Exception ignored) {}
            }
        },300000,TimeUnit.MILLISECONDS.convert(5L, TimeUnit.MINUTES));
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        save(null);
    }
}
