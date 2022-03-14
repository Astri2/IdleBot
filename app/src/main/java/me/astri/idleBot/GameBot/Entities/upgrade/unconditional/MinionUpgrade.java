package me.astri.idleBot.GameBot.entities.upgrade.unconditional;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.minions.Minion;
import me.astri.idleBot.GameBot.entities.minions.PlayerMinions;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.utils.Lang;
import me.astri.idleBot.GameBot.utils.Utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MinionUpgrade extends UnconditionalUpgrade {
    public MinionUpgrade(String name, String icon, BigNumber price, String[] args) {
        super(name, icon, price, args);
    }

    @Override
    protected String getTitleId() {
        return "minion_upg_title";
    }

    @Override
    protected String getDescId() {
        return "minion_upg_desc";
    }

    @Override
    protected String[] getDescArgs(Player p) {
        Minion m = p.getMinions().get().get(this.args[0]);
        Lang l = p.getLang();
        return new String[]{l.get(this.name), Utils.timeParser(m.getDuration(p), TimeUnit.SECONDS), Integer.toString(m.getCPSBonusPerLevel())};
    }

    @Override
    protected Consumer<Object> getAction() {
        return obj -> {
            PlayerMinions minions = (PlayerMinions) obj;
            minions.get().get(args[0]).setBought(true);
        };
    }
}
