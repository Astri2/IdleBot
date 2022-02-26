package me.astri.idleBot.GameBot.game;

import me.astri.idleBot.GameBot.commands.equipment.Equipment_upgrade;
import me.astri.idleBot.GameBot.commands.equipment.Equipment_display;
import me.astri.idleBot.GameBot.commands.noCategory.Profile;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class PermaActionComponent extends ListenerAdapter {
    private static final Pattern replacePattern = Pattern.compile("[^a-zA-Z].*");
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent e) {
        if(!e.getMessage().getAuthor().equals(e.getJDA().getSelfUser()))
            return;
        switch(replacePattern.matcher(e.getButton().getId()).replaceAll("")) {
            case "equipmentDisplay" -> {
                e.deferReply(true).queue();
                Equipment_display.display(e.getHook(), e.getUser(), e.getUser());
            }
            case "profileDisplay" -> {
                e.deferReply(true).queue();
                Profile.display(e.getHook(),e.getUser(),e.getUser());
            }
            case "redoLevelUp" -> {
                e.deferReply(true).queue();
                Equipment_upgrade.redoLevelUp(e.getHook(),e.getUser(), e.getButton().getId());
            }
        }
    }
}
