package me.astri.idleBot.Entities.player;

import me.astri.idleBot.main.Lang;

public abstract class BotUser {
    private Lang lang;
    private final String id;
    private boolean useScNotation;

    public BotUser(String id, Lang lang, boolean useScNotation) {
        this.id = id;
        this.lang = lang;
        this.useScNotation = useScNotation;
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
        return  this.useScNotation;
    }
    
    public BotUser setUseScNotation(boolean useScNotation) {
        this.useScNotation = useScNotation;
        return this;
    }
}
