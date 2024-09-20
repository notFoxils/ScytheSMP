package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.glitchedSmp.GlitchedSmp;
import me.foxils.foxutils.utilities.ItemAbility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class UpgradeableItem extends Item {

    private final int minLevel;
    private final int maxLevel;

    private static final NamespacedKey levelKey = new NamespacedKey(GlitchedSmp.getInstance(), "upgrade-level");

    public UpgradeableItem(Material material, int customModelData, String name, NamespacedKey key, List<ItemAbility> abilityList, int maxLevel, int minLevel) {
        super(material, customModelData, name, key, abilityList);

        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public ItemStack createItem(int amount) {
        ItemStack newItem = super.createItem(amount);

        ItemUtils.nameItem(newItem, GlitchedSmp.hex(getName()));

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

    public static int getLevel(ItemStack item) {
        return ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);
    }
}
