package me.astri.idleBot.commands;

import me.astri.idleBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public class Minions implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("minions","shows your minions");
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        hook.sendMessageEmbeds(
                new EmbedBuilder().setTitle(e.getUser().getAsTag() + " minions")
                        .addField("<:Skeleton_Minion:882792395845033984> Skeleton",
                                "Level 26\n"+
                                "+13% Cps bonus\n"+
                                "Cost: 20<:slayer_point:882795259971661844>\n"+
                                "Reward: 910<:slayer_point:882795259971661844>\n"+
                                "<:bar_f:882933427521859585><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_e1:882933409784147969><:bar_e2:882933417602342942>\n"+
                                "🕐 50m",
                                true)
                        .addField("<:White_Knight_Minion:882792436638814219> White knight",
                                "Level 26\n"+
                                "+13% Cps bonus\n"+
                                "Cost: 40<:slayer_point:882795259971661844>\n"+
                                "Reward: 26<:slayer_point:882795259971661844>\n"+
                                "<:bar_f:882933427521859585><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_f2:882933451551035422>\n"+
                                "🕐 Ready",true)
                        .addField("<:Goo_Mutant_Minion:882792386311368794> Mutant Goo",
                                "Level 43\n"+
                                "\n"+
                                "Cost: 200<:slayer_point:882795259971661844>\n"+
                                "Reward: 2150<:slayer_point:882795259971661844>\n"+
                                "<:bar_e:882933403329122324><:bar_e1:882933409784147969><:bar_e1:882933409784147969><:bar_e1:882933409784147969><:bar_e2:882933417602342942>\n"+
                                "🕐 3h - Idle",true)
                        .addField("<:Dark_Wizard_Minion:882792427524620288> Dark Wizard",
                                "Level 19\n"+
                                "+19% Cps bonus\n"+
                                "Cost: 100<:slayer_point:882795259971661844>\n"+
                                "Reward: 380<:slayer_point:882795259971661844>\n"+
                                "<:bar_f:882933427521859585><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_f1:882933444999536681><:bar_e2:882933417602342942>\n"+
                                "🕐 50m",true)
                        .addField("<:Stone_Golem_Minion:882792417072381964> Stone Golem",
                                "Level 3\n"+
                                "\n"+
                                "Cost: 2000<:slayer_point:882795259971661844>\n"+
                                "Reward: 4500<:slayer_point:882795259971661844>\n"+
                                "<:bar_e:882933403329122324><:bar_e1:882933409784147969><:bar_e1:882933409784147969><:bar_e1:882933409784147969><:bar_e2:882933417602342942>\n"+
                                "🕐 24h - Idle",true)
                        .build()
        ).addActionRow(
                Button.of(ButtonStyle.SECONDARY,"skeleton","In Mission...",Emoji.fromEmote("Skeleton_Minion",882792395845033984L,false)).withDisabled(true),
                Button.of(ButtonStyle.SUCCESS,"white_knight","Click to Claim",Emoji.fromEmote("White_Knight_Minion",882792436638814219L,false)),
                Button.of(ButtonStyle.PRIMARY,"goo_mutant","Click to Start",Emoji.fromEmote("Goo_Mutant_Minion",882792386311368794L,false)),
                Button.of(ButtonStyle.SECONDARY,"dark_wizzard","In Mission...",Emoji.fromEmote("Dark_Wizard_Minion",882792427524620288L,false)).withDisabled(true),
                Button.of(ButtonStyle.DANGER,"stone_golem","Can't Afford Start",Emoji.fromEmote("882792427524620288",882792417072381964L,false)).withDisabled(true)
        ).queue();
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}
