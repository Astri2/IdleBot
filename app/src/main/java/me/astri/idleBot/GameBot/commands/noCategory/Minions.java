package me.astri.idleBot.GameBot.commands.noCategory;

import me.astri.idleBot.GameBot.BotGame;
import me.astri.idleBot.GameBot.entities.minions.Minion;
import me.astri.idleBot.GameBot.entities.minions.PlayerMinions;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.utils.Emotes;
import me.astri.idleBot.GameBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Minions extends ISlashCommand {
    @Override
    public CommandData getData() {
        return new CommandData("minions","shows your minions");
    }


    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Player player = GameUtils.getUser(hook, e.getUser());
        if(player == null)
            return;

        hook.sendMessageEmbeds(getEmbed(player).build()).addActionRows(getActionRows(player, false)).queue(
                msg -> player.getMinions().setLastMessageid(msg.getId()));
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }

    private static EmbedBuilder getEmbed(Player player) {
        PlayerMinions minions = player.getMinions();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(BotGame.jda.getUserById(player.getId()).getAsTag() + " minions");
        for(Minion minion : minions.get().values()) {
            if(!minion.isBought()) continue;

            String title = Emotes.get(minion.toString()) + " " + minion;
            String sb =
                    "Level %d\n".formatted(minion.getLevel()) +
                            "+%d%% Cps bonus\n".formatted(minion.getCPSBonus()) +
                            "Cost: %d %s\n".formatted(
                                    minion.getPrice(player), Emotes.get("sp")) +
                            "Reward: %d %s\n".formatted(
                                    minion.getReward(player), Emotes.get("sp")) +
                            Emotes.get("progress_bar_%d".formatted(
                                    getProgressionState(player, minion))) + "\n" +
                            "\uD83D\uDD50 %s\n".formatted(
                                    getTime(player, minion));
            eb.addField(title, sb,true);
        }
        if(eb.getFields().size() == 0)
            eb.setDescription("You have not unlocked any minion :(");

        return eb;
    }

    private static int getProgressionState(Player p, Minion minion) {
        //If minion isn't on a mission, the bar will be empty (state 0)
        if(minion.isIdle()) return 0;

        //if current time is after the mission ending time, bar is full (state 5)
        double remainingTime = minion.getEndTime()-System.currentTimeMillis()/1000.;
        if(remainingTime < 0) return 5;

        //Otherwise, quantify the advancement of the mission, between state 0-4
        double advancement = Math.floor(5*(1 - (remainingTime-1)/minion.getDuration(p)));
        return (int) advancement;

    }

    private static String getTime(Player p, Minion minion) {
        if(minion.isIdle())
            return Utils.timeParser(minion.getDuration(p), TimeUnit.SECONDS) + " - Idle";
        //else
        long now = Math.round(System.currentTimeMillis()/1000.);
        if(now > minion.getEndTime())
            return "Ready";
        else
            return Utils.timeParser(minion.getEndTime()-now,TimeUnit.SECONDS);
    }

    private static ArrayList<ActionRow> getActionRows(Player p, boolean allDisable) {
        ArrayList<Button> buttons = new ArrayList<>();

        for(Minion minion : p.getMinions().get().values()) {
            if(!minion.isBought()) continue;

            ButtonStyle style = null;
            String id = "";
            String label = "";
            int buttonState;
            long now = System.currentTimeMillis()/1000;
            boolean disable = false;
            //1 = start ; 2 = can't buy ; 3 = running ; 4 = claim
            if (minion.isIdle()) {
                if (true) buttonState = 1; //TODO enough SP
                else buttonState = 2;
            } else {
                if (now < minion.getEndTime()) buttonState = 3;
                else buttonState = 4;
            }

            switch(buttonState) {
                case 1 -> {
                    style = ButtonStyle.PRIMARY;
                    id = "minion_" + minion + "_start_" + p.getId();
                    label = "Click to Start";
                }
                case 2 -> {
                    style = ButtonStyle.DANGER;
                    id = "minion_" + minion + "_cant_" + p.getId();
                    label = "Can't afford";
                    disable = true;
                }
                case 3 -> {
                    style = ButtonStyle.SECONDARY;
                    id = "minion_" + minion + "_miss_" + p.getId();
                    label = "In Mission...";
                    disable = true;
                }
                case 4 -> {
                    style = ButtonStyle.SUCCESS;
                    id = "minion_" + minion + "_claim_" + p.getId();
                    label = "Click to Claim";
                }
            }
            //if minion is in a mission, use animated emote
            String emotePrefix = buttonState == 3 ? "a_":"";
            buttons.add(Button.of(style,id, label,Emoji.fromMarkdown(
                    Emotes.get(emotePrefix + minion))).withDisabled(disable || allDisable));
        }

        ArrayList<ActionRow> rows = new ArrayList<>();
        ArrayList<Component> row = new ArrayList<>();
        for(int i = 0 ; i < buttons.size() ; i++) {
            row.add(buttons.get(i));
            if(i % 3 == 2) {
                rows.add(ActionRow.of(row));
                row = new ArrayList<>();
            }
        }
        if(!row.isEmpty())
            rows.add(ActionRow.of(row));
        rows.add(ActionRow.of(
                        Button.secondary("minion_all_refresh_"+ p.getId(),"Refresh").withDisabled(allDisable)));
        return rows;
    }

    public static void buttonClick(ButtonClickEvent e) {
        InteractionHook hook = e.getHook();
        //ID : minion_<minionId>_<actionType>_<userId>
        String[] args = e.getButton().getId().split("_");

        if(!e.getUser().getId().equals(args[3])) {
            hook.sendMessage("You can't interract with that!").queue();
            return;
        }

        Player player = GameUtils.getUser(hook, e.getUser());
        if(player == null)
            return;

        Minion m = player.getMinions().get().get(args[1]);
        PlayerMinions minions = player.getMinions();

        if(!e.getMessageId().equals(minions.getLastMessageid())) {
            e.editMessage("This message has expired")
                    .setEmbeds(getEmbed(player).build())
                    .setActionRows(getActionRows(player, true)).queue();
            return;
        }

        switch(args[2]) {
            case "start" -> {
                m.startMission(minions);
                e.editMessage("your %s is now in mission".formatted(m.getType()))
                        .setEmbeds(getEmbed(player).build())
                        .setActionRows(getActionRows(player, false)).queue();
            }
            case "claim" -> {
                m.endMission();
                e.editMessage("your %s finished its mission!".formatted(m.getType()))
                        .setEmbeds(getEmbed(player).build())
                        .setActionRows(getActionRows(player, false)).queue();
            }
            case "refresh" -> e.editMessageEmbeds(getEmbed(player).build())
                                .setActionRows(getActionRows(player, false)).queue();

            default -> e.reply("How did you interact with that? O.o").queue();
        }
    }
}
