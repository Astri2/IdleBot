package me.astri.idleBot.GameBot.commands.utils;

import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.utils.Font;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Say extends ISlashCommand {
    @Override
    public BaseCommand<CommandData> getData() {
        return new CommandData("say","say somthing using rabbid's font")
                .addOption(OptionType.STRING,"text","the text to say",true)
                .addOptions(new OptionData(OptionType.INTEGER,"zoom","size of pixels")
                        .addChoice("1",1)
                        .addChoice("2",2)
                        .addChoice("3",3)
                        .addChoice("4",4)
                        .addChoice("5",5)
                        .addChoice("10",10))
                .addOptions(new OptionData(OptionType.STRING,"aligned","where the text should be aligned")
                        .addChoice("Left","LEFT")
                        .addChoice("Center","CENTER")
                        .addChoice("Right","RIGHT"));
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        String input = e.getOption("text") != null ? e.getOption("text").getAsString() : e.getUser().getName();
        int zoom = e.getOption("zoom") != null ? (int)e.getOption("zoom").getAsLong() : 1;
        Font.Align align = Font.Align.valueOf(e.getOption("aligned") != null ? e.getOption("aligned").getAsString() : "LEFT");
        BufferedImage output = Font.getImage(input, zoom, align);

        if(output == null)
            hook.sendMessage("Image generation failed :(").queue();
        else {
            sendImage(output,hook,input);
        }
    }

    private void sendImage(BufferedImage output, InteractionHook hook, String input) {
        try {
            File f = new File("app/src/main/resources/img","tmp.png");
            ImageIO.write(output,"PNG",f);

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("Text generated with success!",null,hook.getInteraction().getUser().getAvatarUrl())
                    .setFooter("Font by " + hook.getJDA().retrieveUserById("678627991206494219").complete().getAsTag())
                    .setDescription(input);
            hook.getJDA().getTextChannelById("954145438863347724").sendFile(f).queue(msg -> {
                hook.sendMessageEmbeds(eb.setImage(msg.getAttachments().get(0).getUrl()).build()).queue();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
