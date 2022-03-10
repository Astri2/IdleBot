package me.astri.idleBot.GameBot.entities.upgrade.management;

import me.astri.idleBot.GameBot.entities.upgrade.conditional.EquipmentUpgrade;
import me.astri.idleBot.GameBot.entities.upgrade.unconditional.UnconditionalUpgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class UpgradeBundle {
    private final HashMap<String, List<String>> eqUpgrades = new HashMap<>();
    private final List<String> unconditionalUpgrades = new ArrayList<>();

    public UpgradeBundle(boolean fill) {
        //init eqUpgrades
        {
            HashMap<String, LinkedHashMap<String, EquipmentUpgrade>> mEqUpgrades = UpgradeManager.getEqUpgrades();
            for (String eqId : mEqUpgrades.keySet()) {
                ArrayList<String> eqsById = new ArrayList<>();
                eqUpgrades.put(eqId, eqsById);
                if (fill) {
                    for (String eqName : mEqUpgrades.get(eqId).keySet()) {
                        eqsById.add(mEqUpgrades.get(eqId).get(eqName).getName());
                    }
                }
            }
        }
        //init unconditionalUpgrades
        if (fill) {
            HashMap<String, UnconditionalUpgrade> uncondUpgrades = UpgradeManager.getUnConditionalUpgrades();
            unconditionalUpgrades.addAll(uncondUpgrades.keySet());
        }
    }

    public HashMap<String, List<String>> getEq() {
        return eqUpgrades;
    }

    public List<String> getUncond() {
        return unconditionalUpgrades;
    }
}
