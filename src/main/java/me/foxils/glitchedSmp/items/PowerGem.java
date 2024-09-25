package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.utilities.ItemAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PowerGem extends UpgradeableItem {

    public PowerGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, List<ItemStack> itemsForRecipe, boolean shapedRecipe) {
        super(material, customModelData, name, plugin, abilityList, itemsForRecipe, shapedRecipe, 3, 1);
    }


}
