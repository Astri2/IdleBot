package me.astri.idleBot.GameBot.commands.equipment;

import me.astri.idleBot.GameBot.entities.equipment.Equipment;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashSubcommand;
import me.astri.idleBot.GameBot.utils.Config;
import me.astri.idleBot.GameBot.utils.Font;
import me.astri.idleBot.GameBot.utils.Lang;
import me.astri.idleBot.GameBot.utils.Utils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Equipment_image extends ISlashSubcommand {
    private final static HashMap<String, BufferedImage> emoteImages = new HashMap<>();
    private final static HashMap<String,BufferedImage> backgroundImages = new HashMap<>();

    @Override
    public BaseCommand<CommandData> getData() {
        return new SubcommandData("image","get an image of your equipment")
                .addOption(OptionType.BOOLEAN,"english","use english (False may lead to unsupported characters)",false)
                .addOptions(new OptionData(OptionType.INTEGER,"zoom","size of pixels")
                        .addChoice("1",1)
                        .addChoice("2",2)
                        .addChoice("3",3)
                        .addChoice("4",4)
                        .addChoice("5",5)
                        .addChoice("10",10));
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        boolean english = e.getOption("english") == null || e.getOption("english").getAsBoolean();
        int zoom = e.getOption("zoom") != null ? (int)e.getOption("zoom").getAsLong() : 1;
        Player p = GameUtils.getUser(hook,e.getUser());
        if(p == null) return;

        Lang l = english ? Lang.ENGLISH : p.getLang();
        hook.sendMessage("image generation in progress... ⌛").queue(msg -> {
            try {
                BufferedImage output = getImage(p, e.getUser(), l, zoom);
                msg.editMessage("image posted!").queue();
                sendImage(output,hook);
            } catch(Exception ex) {
                ex.printStackTrace();
                msg.editMessage("Something went wrong :(").queue();
            }
        });
    }

    public static void init() throws Exception {
        //convert gif into list of eq icons
        ArrayList<BufferedImage> images = new ArrayList<>();
        ImageReader r = ImageIO.getImageReadersBySuffix("GIF").next();
        ImageInputStream in = ImageIO.createImageInputStream(new File("app/src/main/resources/img/eqDisp","eqIcons.gif"));
        r.setInput(in);
        for(int i = 0 ; i < r.getNumImages(true) ; i++) {
            images.add(r.read(i));
        }
        r.dispose();
        in.flush();
        in.close();
        
        String[] imgNames = {"back","line","leftBar","rightBar"};
        for (String imgName : imgNames) {
            File f = new File("app/src/main/resources/img/eqDisp","%s.png".formatted(imgName));
            backgroundImages.put(imgName,ImageIO.read(f));
        }

        //connect every eq to its icon
        JSONObject JSONUpgrades = new JSONObject(Utils.readFile(Config.get("CONFIG_PATH") + "upgrades.json")).getJSONObject("EQUIPMENT");
        JSONArray eqNames = JSONUpgrades.getJSONArray("eq_list");
        int id = 0;
        for(int i = 0 ; i < eqNames.length() ; i++) {
            emoteImages.put(eqNames.getString(i),images.get(id)); //first evolution of each eq isn't an upgrade
            id++;
            JSONArray upgrades = JSONUpgrades.getJSONArray(eqNames.getString(i));
            for(int k = 0 ; k < upgrades.length() ; k++) {
                emoteImages.put(upgrades.getJSONObject(k).getString("name"),images.get(id));
                id++;
            }
        }
    }

    private BufferedImage getImage(Player p, User user, Lang l, int zoom) throws IOException {
        int titleGap = 64*zoom;
        int gap = 46*zoom;
        int iconSize = 32*zoom;
        int levelShift = iconSize/2-16*zoom;
        int lvlShift = 12*zoom;

        HashMap<String, Equipment> equipment = p.getEquipment();
        List<Equipment> eqToDisplay = equipment.values().stream().filter(v -> v.getLevel() != 0).toList();

        //generates all images I'll need
        BufferedImage output = new BufferedImage(
                2*gap+(eqToDisplay.size()*2)*iconSize,
                3*gap + titleGap + 2*iconSize + lvlShift + 10*zoom,
                BufferedImage.TYPE_INT_ARGB);
        BufferedImage avatar = ImageIO.read(new URL(user.getEffectiveAvatarUrl()));
        BufferedImage title = Font.getImage(l.get("equipment_title",user.getName()),zoom*3);
        List<BufferedImage> eqNamesImg = eqToDisplay.stream().map(eq ->
                Font.getImage(l.get(eq.getName()).replace(" ","\n"),zoom, Font.Align.CENTER)).toList();
        List<BufferedImage> eqIconsImg = eqToDisplay.stream().map(eq -> emoteImages.get(eq.getName())).toList();
        BufferedImage levelImg = Font.getImage("Level",zoom, Font.Align.CENTER);
        List<BufferedImage> eqLevelImg = eqToDisplay.stream().map(eq ->
                Font.getImage(Long.toString(eq.getLevel()),zoom)).toList();

        //create graphic
        Graphics g = output.createGraphics();
        g.setClip(0,0,output.getWidth(),output.getHeight());
        g.drawImage(backgroundImages.get("back"),0,0,924*zoom,288*zoom,null);
        g.drawImage(backgroundImages.get("line"),0,0,output.getWidth(),288*zoom,null);
        g.drawImage(backgroundImages.get("leftBar"),0,0,18*zoom,288*zoom,null);
        g.drawImage(backgroundImages.get("rightBar"),output.getWidth()-18*zoom,0,18*zoom,288*zoom,null);

        BufferedImage circleAvatar = new BufferedImage(70*zoom,70*zoom,BufferedImage.TYPE_INT_ARGB);
        {
            Graphics g1 = circleAvatar.createGraphics();
            g1.setColor(new Color(0x140C1C));
            g1.fillRect(0,0,70*zoom,70*zoom);
            g1.drawImage(avatar,3,3,64*zoom,64*zoom,null);
            g1.dispose();
//            g1.setClip(new Ellipse2D.Float(0, 0, 70*zoom, 70*zoom));
//            g1.fillRect(0,0,circleAvatar.getWidth(), circleAvatar.getHeight());
//            g1.setClip(new Ellipse2D.Float(3, 3, 64*zoom, 64*zoom));
//            g1.drawImage(avatar,3,3,64*zoom,64*zoom,null);
//            g1.dispose();
        }

        g.drawImage(
                circleAvatar,
                output.getWidth()/2-title.getWidth()/2 - gap,
                gap/2,
                null);
        g.drawImage(
                title,
                //xPos center on the image
                output.getWidth()/2-title.getWidth()/2 + gap,
                //yPos: gap
                gap,
                null);
        for(int i = 0 ; i < eqToDisplay.size() ; i++) {
            BufferedImage name = eqNamesImg.get(i);
            g.drawImage(
                    name,
                    //xPos: gap + shift from index + center on the spot
                    gap + iconSize*2*i + (iconSize/2-name.getWidth()/2),
                    //yPos: gap + titleGap + center on the spot
                    gap + (gap/2 + titleGap - name.getHeight()/2),
                    null);
            BufferedImage icon = eqIconsImg.get(i);
            g.drawImage(
                    icon,
                    //xPos: gap + shift fom index
                    gap + iconSize*2*i,
                    //yPos: gap titleGap + room for text to fit
                    2*gap + titleGap + gap/4,
                    // width & height
                    iconSize,
                    iconSize,
                    null);
            g.drawImage(
                    levelImg,
                    gap + iconSize*2*i + levelShift,
                    2*gap + titleGap + 2*iconSize,
                    null);
            BufferedImage lvl = eqLevelImg.get(i);
            g.drawImage(
                    lvl,
                    gap + iconSize*2*i + iconSize/2-lvl.getWidth()/2,
                    2*gap + titleGap + 2*iconSize + lvlShift,
                    null);
        }
        g.dispose();

        return output;
    }

    private void sendImage(BufferedImage output, InteractionHook hook) {
        try {
            File f = new File("app/src/main/resources/img","tmp.png");
            ImageIO.write(output,"PNG",f);
            hook.sendFile(f).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getCooldown() {
        return 3600000L;
    }
}
