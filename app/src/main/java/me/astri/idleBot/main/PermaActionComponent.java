package me.astri.idleBot.main;

import me.astri.idleBot.commands.LevelUpEquipment;
import me.astri.idleBot.commands.displayCommands.EquipmentDisplay;
import me.astri.idleBot.commands.displayCommands.ProfileDisplay;
import me.astri.idleBot.game.GameUtils;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class PermaActionComponent extends ListenerAdapter {
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent e) {
        e.deferReply(true).queue();
        if(!e.getMessage().getAuthor().equals(e.getJDA().getSelfUser()))
            return;
        IMentionable author = e.isFromGuild() ? e.getMember() : e.getUser();
        switch(e.getButton().getId().replaceAll("[^a-zA-Z].*","")) {
            case "equipmentDisplay" -> EquipmentDisplay.display(e.getHook(), author, GameUtils.getUser(e.getHook(), e.getUser()));
            case "profileDisplay" -> ProfileDisplay.display(e.getHook(),author,GameUtils.getUser(e.getHook(), e.getUser()));
            case "redoLevelUp" -> LevelUpEquipment.redoLevelUp(e.getHook(),author, e.getButton().getId());
        }
    }
}
