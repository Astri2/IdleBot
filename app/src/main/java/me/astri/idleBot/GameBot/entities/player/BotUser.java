package me.astri.idleBot.GameBot.entities.player;

import me.astri.idleBot.GameBot.utils.Lang;

public abstract class BotUser {
    private int cookies;
    private Lang lang;
    private final String id;
    private boolean useScNotation;
    private String ephemeral;

    public BotUser(String id, Lang lang, boolean useScNotation, String ephemeral) {
        this.id = id;
        this.lang = lang;
        this.useScNotation = useScNotation;
        this.ephemeral = ephemeral;

        if(this.lang == null)
            this.lang = Lang.ENGLISH;
    }

    public String getId() {
        return this.id;
    }

    public Lang getLang() {
        return this.lang;
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
    
    public void setUseScNotation(boolean useScNotation) {
        this.useScNotation = useScNotation;
    }

    public void setEphemeral(String ephemeral) {
        this.ephemeral = ephemeral;
    }

    public void addCookie() {
        this.cookies++;
    }

    public int getCookies() {
        return this.cookies;
    }
}
