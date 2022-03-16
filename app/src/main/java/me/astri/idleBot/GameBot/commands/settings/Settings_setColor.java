package me.astri.idleBot.GameBot.commands.settings;

import me.astri.idleBot.GameBot.entities.ColorEnum;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Settings_setColor extends ISlashSubcommand {
    @Override
    public BaseCommand<CommandData> getData() {
        return new SubcommandData("color","change the color of your embeds")
                .addOptions(
                        new OptionData(OptionType.STRING,"color","the color you want to use",true)
                                .addChoice("Black","BLACK")
                                .addChoice("Blue","BLUE")
                                .addChoice("Cyan","CYAN")
                                .addChoice("Dark gray","DARK_GRAY")
                                .addChoice("Gray","GRAY")
                                .addChoice("Green","GREEN")
                                .addChoice("Light gray","LIGHT_GRAY")
                                .addChoice("Magenta","MAGENTA")
                                .addChoice("Orange","ORANGE")
                                .addChoice("Pink","PINK")
                                .addChoice("Red","RED")
                                .addChoice("White","WHITE")
                                .addChoice("Yellow","YELLOW")
                                .addChoice("Purple","PURPLE")
                );
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        Player player = GameUtils.getUser(hook, e.getUser());
        if(player == null)
            return;

        String color = e.getOption("color").getAsString();

        player.setColor(ColorEnum.valueOf(color));
        hook.sendMessageEmbeds(
                new EmbedBuilder().setColor(player.getColor()).setDescription(player.getLang().get("color_update",
                        e.getUser().getAsMention(), player.getLang().get("color_" + color.toLowerCase()))).build()).queue();
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}













