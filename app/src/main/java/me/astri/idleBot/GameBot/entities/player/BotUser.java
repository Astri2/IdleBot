package me.astri.idleBot.GameBot.entities.player;

import me.astri.idleBot.GameBot.entities.ColorEnum;
import me.astri.idleBot.GameBot.entities.Cookie;
import me.astri.idleBot.GameBot.utils.Lang;

import java.awt.*;

public abstract class BotUser {
    private Lang lang;
    private final String id;
    private boolean useScNotation;
    private String ephemeral;
    private final Cookie cookie;
    public ColorEnum color;

    public BotUser(String id, Lang lang, boolean useScNotation, String ephemeral) {
        this.id = id;
        this.lang = lang;
        this.useScNotation = useScNotation;
        this.ephemeral = ephemeral;
        this.color = ColorEnum.BLACK;

        if(this.lang == null)
            this.lang = Lang.ENGLISH;

        this.cookie = new Cookie();
    }

    public String getId() {
        return this.id;
    }

    public Lang getLang() {
        return this.lang;
    }

    public Color getColor() {
        return this.color.getColor();
    }
    
    public boolean usesScNotation() {
        return this.useScNotation;
    }

    public String isEphemeral() {
        return this.ephemeral;
    }

    public void setLang(Lang newLang) {
        this.lang = newLang;
    }

    public void setColor(ColorEnum color) {
        this.color = color;
    }

    public void setUseScNotation(boolean useScNotation) {
        this.useScNotation = useScNotation;
    }

    public void setEphemeral(String ephemeral) {
        this.ephemeral = ephemeral;
    }

    public Cookie getCookies() {
        return this.cookie;
    }
}
