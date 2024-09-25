package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.foxutils.utilities.ItemAbility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

@SuppressWarnings("unused")
public class UpgradeableItem extends Item {

    private final int minLevel;
    private final int maxLevel;

    private final NamespacedKey levelKey = new NamespacedKey(plugin, "upgrade-level");

    public UpgradeableItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, int maxLevel, int minLevel) {
        super(material, customModelData, name, plugin, abilityList);

        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public UpgradeableItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, List<ItemStack> itemsForRecipe, boolean shapedRecipe, int maxLevel, int minLevel) {
        super(material, customModelData, name, plugin, abilityList, itemsForRecipe, shapedRecipe);

        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public ItemStack createItem(int amount) {
        ItemStack newItem = super.createItem(amount);

        return ItemUtils.storeIntegerData(levelKey, newItem, minLevel);
    }

    public void upgradeLevel(int amount, ItemStack item) {
        int level = ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);

        if (level == maxLevel) {
            return;
        }

        ItemUtils.storeIntegerData(levelKey, item, Math.min(maxLevel, level + amount));
    }

    public void downgradeLevel(int amount, ItemStack item) {
        int level = ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);

        if (level == minLevel) {
            return;
        }

        ItemUtils.storeIntegerData(levelKey, item, Math.min(minLevel, level - amount));
    }

    public final int getLevel(ItemStack item) {
        return ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);
    }
}
