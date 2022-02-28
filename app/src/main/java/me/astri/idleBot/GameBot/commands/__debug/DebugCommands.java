package me.astri.idleBot.GameBot.commands.__debug;

import me.astri.idleBot.GameBot.dataBase.DataBase;
import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.player.BotUser;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.utils.Lang;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DebugCommands extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().toLowerCase().split("\\s+");
        if(!event.getAuthor().getId().equals(Config.get("BOT_OWNER_ID"))) return;
        if(!event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser())) return;
        switch (args[0]) {
            case "i!give" -> give(event);
            case "i!load" -> load();
            case "i!prices" -> prices(event);
            case "i!save" -> save();
            case "i!ping" -> ping(event);
        }
    }

    private void give(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(event.getMessage().getMentionedMembers().isEmpty())
            return;
        if(args.length < 3)
            return;
        BigNumber money;
        try {
            money = new BigNumber(1.,Long.parseLong(args[2]));
        } catch(Exception ignore) {return;}
        Member member = event.getMessage().getMentionedMembers().get(0);
        Player player = GameUtils.getUser(null,member);
        player.editCoins(money);

        event.getMessage().addReaction("✅").queue();
    }

    private void load() {
        DataBase.load(null);
    }

    private void prices(GuildMessageReceivedEvent event) {
        if(!event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser()))
            return;
        try {
            List<BotUser> players = DataBase.getUsers().values().stream().toList();
            List<BotUser> new_players = new ArrayList<>();
            for (BotUser botUser : players) {
                System.out.println("player:" + event.getJDA().retrieveUserById(botUser.getId()).complete().getName());
                //GATHER PLAYER INFORMATION

                BigNumber money = ((Player) botUser).getCoins();
                String id = botUser.getId();
                boolean scNot = botUser.usesScNotation();
                String eph = botUser.isEphemeral();
                Lang lang = botUser.getLang();

                ArrayList<Long> eqs = new ArrayList<>();
                List<Equipment> equipments = ((Player) botUser).getEquipment().values().stream().toList();
                System.out.println("old values");
                for (Equipment value : equipments) {
                    eqs.add(value.getLevel());
                    System.out.println(value.getName() + " : level " + value.getLevel() + " : price " + value.getPrice());
                }
                System.out.println();

                //CREATE THE NEW PLAYER AND ADD BASIC INFORMATION BACK
                Player p1 = new Player(id, lang, scNot, eph);
                new_players.add(p1);

                //ADD REST OF INFORMATION BACK
                p1.editCoins(money);
                List<Equipment> equipments_reset = p1.getEquipment().values().stream().toList();
                for (int k = 0; k < equipments_reset.size(); k++) {
                    equipments_reset.get(k).levelUp(eqs.get(k).intValue(),p1.getUpgrades());
                    System.out.println(p1.getEquipment().values().stream().toList().get(k).getName() + " : level " + p1.getEquipment().values().stream().toList().get(k).getLevel() +
                            " : price " + p1.getEquipment().values().stream().toList().get(k).getPrice());
                }

            }


            //PUT NEW DATA INTO DB
            HashMap<String, BotUser> new_botUsers = new HashMap<>();
            for(BotUser new_player : new_players)
                new_botUsers.put(new_player.getId(),new_player);
            DataBase.setUsers(new_botUsers);

            event.getMessage().addReaction("✅").queue();
        } catch (Exception e) {event.getChannel().sendMessage(e.getMessage()).queue();}

    }

    private void save() {
        DataBase.save(null);
    }

    private void ping(GuildMessageReceivedEvent event) {
        long time = System.currentTimeMillis();
        event.getChannel().sendMessage("Pong !").queue(msg ->
            msg.editMessageFormat("Pong %dms 🏓 !",System.currentTimeMillis()-time).queue()
        );
    }
}
