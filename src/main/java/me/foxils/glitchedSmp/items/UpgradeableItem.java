package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.glitchedSmp.GlitchedSmp;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UpgradeableItem extends Item {

    private final int minLevel;
    private final int maxLevel;

    private static final NamespacedKey levelKey = new NamespacedKey(GlitchedSmp.getInstance(), "upgrade-level");

    public UpgradeableItem(Material material, String name, NamespacedKey key, List<ItemStack> itemsForRecipe, boolean shapedRecipe, int maxLevel, int minLevel) {
        super(material, name, key, itemsForRecipe, shapedRecipe);

        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public ItemStack createItem(int amount) {
        ItemStack newItem = super.createItem(amount);

        return ItemUtils.storeIntegerData(minLevel, newItem, levelKey);
    }

    public void upgradeLevel(int amount, ItemStack item) {
        int level = ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);

        if (level == maxLevel) {
            return;
        }

        ItemUtils.storeIntegerData(Math.min(maxLevel, level + amount), item, levelKey);

    }

    public void downgradeLevel(int amount, ItemStack item) {
        int level = ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);

        if (level == minLevel) {
            return;
        }

        ItemUtils.storeIntegerData(Math.max(minLevel, level - amount), item, levelKey);
    }

    public static int getLevel(ItemStack item) {
        return ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);
    }
}
