package me.astri.idleBot.GameBot.commands;

import me.astri.idleBot.GameBot.Entities.player.Player;
import me.astri.idleBot.GameBot.Entities.upgrade.EquipmentUpgrade;
import me.astri.idleBot.GameBot.Entities.upgrade.Upgrade;
import me.astri.idleBot.GameBot.Entities.upgrade.UpgradeManager;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.main.BotGame;
import me.astri.idleBot.GameBot.main.Emotes;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

import java.math.BigDecimal;
import java.util.*;

public class Upgrades implements ISlashCommand {
    @Override
    public CommandData getCommandData() {
        return new CommandData("upgrades","displays your available upgrades");
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) throws Exception {
        Player player = GameUtils.getUser(hook, e.getUser());
        if(player == null)
            return;

        HashMap<String, List<String>> availableEqUpgrades = new HashMap<>() {{
            put("sword", Arrays.stream(new String[]{"sword_steel","sword_black","sword_light"}).toList());
            put("shield", Arrays.stream(new String[]{"shield_reinforced","shield_iron","shield_steel"}).toList());
        }};

        ArrayList<Upgrade> upgrades = new ArrayList<>();
        HashMap<String, LinkedHashMap<String,EquipmentUpgrade>> actual_upgrades = UpgradeManager.getEqUpgrades();
        for(String eq : availableEqUpgrades.keySet()) {
            LinkedHashMap<String,EquipmentUpgrade> actual_eq_upgrades = actual_upgrades.get(eq);
            for(String upg : availableEqUpgrades.get(eq)) {
                upgrades.add(actual_eq_upgrades.get(upg));
            }
        }
        //TODO sort by price
        int max = Math.min(upgrades.size(), 12);
        int min = 0;
        int current = 1;
        getUpgradeDisplay(hook, player,upgrades,min,max,current).queue();

    }

    private WebhookMessageAction<Message> getUpgradeDisplay(InteractionHook hook, Player p, ArrayList<Upgrade> upgrades, int min, int max, int current) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Available Upgrades of " + BotGame.jda.getUserById(p.getId()).getName(),null, BotGame.jda.getUserById(p.getId()).getEffectiveAvatarUrl());
        eb.setDescription(GameUtils.getNumber(p.getCoins(),p) + " " + Emotes.getEmote("coin") + " " + p.getLang().get("coins"));
        p.update();
        for(int i = min ; i < max ; i++) {
            Upgrade upg = upgrades.get(i);
            eb.addField(upg.getUpgradeField(p,i==current,p.getCoins().compareTo(BigDecimal.valueOf(upg.getPrice().toDouble()))!=-1));
        }
        return hook.sendMessageEmbeds(eb.build()).addActionRow(
                Button.secondary("left","<=").withDisabled(current == min),
                Button.secondary("right","=>").withDisabled(current == max),
                Button.secondary("buy","BUY").withDisabled(p.getCoins().compareTo(
                        BigDecimal.valueOf(upgrades.get(current).getPrice().toDouble())) < 0),
                Button.secondary("buy_all","BUY ALL").withDisabled(p.getCoins().compareTo(
                        BigDecimal.valueOf(upgrades.get(0).getPrice().toDouble())) < 0)
        );
    }
}
