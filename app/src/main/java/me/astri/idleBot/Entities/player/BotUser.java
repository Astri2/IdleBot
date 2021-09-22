package me.astri.idleBot.Entities.player;

import me.astri.idleBot.main.Lang;

import java.io.Serializable;

public abstract class BotUser implements Serializable {
    private Lang lang;
    private final String id;
    private boolean useScNotation;
    private String ephemeral;

    public BotUser(String id, Lang lang, boolean useScNotation, String ephemeral) {
        this.id = id;
        this.lang = lang;
        this.useScNotation = useScNotation;
        this.ephemeral = ephemeral;
    }

    public String getId() {
        return this.id;
    }

    public BotUser setLang(Lang newLang) {
        this.lang = newLang;
        return this;
    }

    public Lang getLang() {
        return this.lang;
    }
    
    public boolean isUseScNotation() {
        return this.useScNotation;
    }

    public String isEphemeral() {
        return this.ephemeral;
    }
    
    public BotUser setUseScNotation(boolean useScNotation) {
        this.useScNotation = useScNotation;
        return this;
    }

    public BotUser setEphemeral(String ephemeral) {
        this.ephemeral = ephemeral;
        return this;
    }
}
