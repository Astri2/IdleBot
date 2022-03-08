package me.astri.idleBot.GameBot.commands.noCategory;

import me.astri.idleBot.GameBot.entities.minions.Minion;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.utils.Emotes;
import me.astri.idleBot.GameBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
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

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(e.getUser().getAsTag() + " minions");
        for(Minion minion : player.getMinions()) {
            if(!minion.isBought()) continue;

            String title = Emotes.get(minion.toString()) + " " + minion;
            String sb =
                    "Level %d\n".formatted(minion.getLevel()) +
                    "+%d%% Cps bonus\n".formatted(minion.getCPSBonus()) +
                    "Cost: %d %s\n".formatted(
                            minion.getCost(), Emotes.get("sp")) +
                    "Reward: %d %s\n".formatted(
                            minion.getReward(), Emotes.get("sp")) +
                    Emotes.get("progress_bar_%d".formatted(
                            getProgressionState(minion))) + "\n" +
                    "\uD83D\uDD50 %s\n".formatted(
                            getTime(minion));
            eb.addField(title, sb,true);
        }
        hook.sendMessageEmbeds(eb.build()).addActionRows(getActionRows(player)).queue();
/*
        hook.sendMessageEmbeds(
                new EmbedBuilder().setTitle(e.getUser().getAsTag() + " minions")
                        .addField("<:Skeleton_Minion:882792395845033984> Skeleton",
                                """
                                        Level 26
                                        +13% Cps bonus
                                        Cost: 20<:slayer_point:882795259971661844>
                                        Reward: 910<:slayer_point:882795259971661844>
                                        <:bar_f:882933427521859585><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_e1:882933409784147969><:bar_e2:882933417602342942>
                                        🕐 50m""",
                                true)
                        .addField("<:White_Knight_Minion:882792436638814219> White knight",
                                """
                                        Level 26
                                        +13% Cps bonus
                                        Cost: 40<:slayer_point:882795259971661844>
                                        Reward: 26<:slayer_point:882795259971661844>
                                        <:bar_f:882933427521859585><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_f2:882933451551035422>
                                        🕐 Ready""",true)
                        .addField("<:Goo_Mutant_Minion:882792386311368794> Mutant Goo",
                                """
                                        Level 43

                                        Cost: 200<:slayer_point:882795259971661844>
                                        Reward: 2150<:slayer_point:882795259971661844>
                                        <:bar_e:882933403329122324><:bar_e1:882933409784147969><:bar_e1:882933409784147969><:bar_e1:882933409784147969><:bar_e2:882933417602342942>
                                        🕐 3h - Idle""",true)
                        .addField("<:Dark_Wizard_Minion:882792427524620288> Dark Wizard",
                                """
                                        Level 19
                                        +19% Cps bonus
                                        Cost: 100<:slayer_point:882795259971661844>
                                        Reward: 380<:slayer_point:882795259971661844>
                                        <:bar_f:882933427521859585><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_e2:882933417602342942>
                                        🕐 50m""",true)
                        .addField("<:Stone_Golem_Minion:882792417072381964> Stone Golem",
                                """
                                        Level 3

                                        Cost: 2000<:slayer_point:882795259971661844>
                                        Reward: 4500<:slayer_point:882795259971661844>
                                        <:bar_e:882933403329122324><:bar_e1:882933409784147969><:bar_e1:882933409784147969><:bar_e1:882933409784147969><:bar_e2:882933417602342942>
                                        🕐 24h - Idle""",true)
                        .build()
        ).addActionRow(
                Button.of(ButtonStyle.SECONDARY,"skeleton","In Mission...",Emoji.fromEmote("Skeleton_Minion",882792395845033984L,false)).withDisabled(true),
                Button.of(ButtonStyle.SUCCESS,"white_knight","Click to Claim",Emoji.fromEmote("White_Knight_Minion",882792436638814219L,false)),
                Button.of(ButtonStyle.PRIMARY,"goo_mutant","Click to Start",Emoji.fromEmote("Goo_Mutant_Minion",882792386311368794L,false)),
                Button.of(ButtonStyle.SECONDARY,"dark_wizzard","In Mission...",Emoji.fromEmote("Dark_Wizard_Minion",882792427524620288L,false)).withDisabled(true),
                Button.of(ButtonStyle.DANGER,"stone_golem","Can't Afford Start",Emoji.fromEmote("882792427524620288",882792417072381964L,false)).withDisabled(true)
        ).queue();
 */
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }

    private int getProgressionState(Minion minion) {
        //generates a number between 0 and 5
        //which is the progression of the mission
        return minion.isIdle() ?
                0:
                Math.min(5,(int)(5*(System.currentTimeMillis()/1000)/minion.getEndTime()));
    }

    private String getTime(Minion minion) {
        if(minion.isIdle())
            return Utils.timeParser(minion.getDuration(), TimeUnit.SECONDS) + " - Idle";
        //else
        long now = System.currentTimeMillis()/1000;
        if(now > minion.getEndTime())
            return "Ready";
        else
            return Utils.timeParser(minion.getEndTime()-now,TimeUnit.SECONDS);
    }

    private ArrayList<ActionRow> getActionRows(Player p) {
        ArrayList<Button> buttons = new ArrayList<>();

        for(Minion minion : p.getMinions()) {
            ButtonStyle style = null;
            String id = "";
            String label = "";
            int buttonState;
            long now = System.currentTimeMillis()/1000;
            boolean disable = false;

            if (minion.isIdle())
                if(true) buttonState = 1; //TODO enough SP
                else buttonState = 2;
            else
                if(now < minion.getEndTime()) buttonState = 3;
                else buttonState = 4;

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
                    style = ButtonStyle.PRIMARY;
                    id = "minion_" + minion + "_claim_" + p.getId();
                    label = "Click to Start";
                }
            }
            buttons.add(Button.of(style,id, label,Emoji.fromMarkdown(
                    Emotes.get(minion.toString()))).withDisabled(disable));
        }

        ArrayList<ActionRow> rows = new ArrayList<>();
        ArrayList<Component> row = new ArrayList<>();
        for(int i = 0 ; i < buttons.size() ; i++) {
            row.add(buttons.get(i));
            if(i % 5 == 4) {
                rows.add(ActionRow.of(row));
                row = new ArrayList<>();
            }
        }
        rows.add(ActionRow.of(
                        Button.secondary("minion_reload_"+ p.getId(),"Reload")));
        return rows;
    }
}
