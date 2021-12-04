package me.astri.idleBot.GameBot.game;

import me.astri.idleBot.GameBot.commands.LevelUpEquipment;
import me.astri.idleBot.GameBot.commands.displayCommands.EquipmentDisplay;
import me.astri.idleBot.GameBot.commands.displayCommands.ProfileDisplay;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class PermaActionComponent extends ListenerAdapter {
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent e) {
        if(!e.getMessage().getAuthor().equals(e.getJDA().getSelfUser()))
            return;
        switch(e.getButton().getId().replaceAll("[^a-zA-Z].*","")) {
            case "equipmentDisplay" -> {
                e.deferReply(true).queue();
                EquipmentDisplay.display(e.getHook(), e.getUser(), e.getUser());
            }
            case "profileDisplay" -> {
                e.deferReply(true).queue();
                ProfileDisplay.display(e.getHook(),e.getUser(),e.getUser());
            }
            case "redoLevelUp" -> {
                e.deferReply(true).queue();
                LevelUpEquipment.redoLevelUp(e.getHook(),e.getUser(), e.getButton().getId());
            }
        }
    }
}
