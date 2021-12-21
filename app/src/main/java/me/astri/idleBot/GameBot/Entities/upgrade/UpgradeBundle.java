package me.astri.idleBot.GameBot.Entities.upgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class UpgradeBundle {
    private final HashMap<String, ArrayList<String>> eqUpgrades = new HashMap<>();

    public UpgradeBundle(boolean init) {
        //init eqUpgrades
        {
            HashMap<String, LinkedHashMap<String, EquipmentUpgrade>> mEqUpgrades = UpgradeManager.getEqUpgrades();
            for (String eqId : mEqUpgrades.keySet()) {
                ArrayList<String> eqsById = new ArrayList<>();
                eqUpgrades.put(eqId, eqsById);
                if (init) {
                    for (String eqName : mEqUpgrades.get(eqId).keySet()) {
                        eqsById.add(mEqUpgrades.get(eqId).get(eqName).getName());
                    }
                }
            }
        }
    }

    public HashMap<String, ArrayList<String>> getEq() {
        return eqUpgrades;
    }
}
