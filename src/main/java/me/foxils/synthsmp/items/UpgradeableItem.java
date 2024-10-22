package me.foxils.synthsmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.foxutils.utilities.ItemAbility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.List;

@SuppressWarnings("unused")
public abstract class UpgradeableItem extends Item implements ClickActions {

    private final int minLevel;
    private final int maxLevel;

    private static final NamespacedKey levelKey = new NamespacedKey("sythesmp", "upgrade-level");

    public UpgradeableItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, List<ItemStack> itemsForRecipe, boolean shapedRecipe, int maxLevel, int minLevel) {
        super(material, customModelData, name, plugin, abilityList, itemsForRecipe, shapedRecipe);

        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public UpgradeableItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, int maxLevel, int minLevel) {
        this(material, customModelData, name, plugin, abilityList, null, false, maxLevel, minLevel);
    }

    @Override
    public ItemStack createItem(int amount) {
        ItemStack newItem = super.createItem(amount);

        return ItemUtils.storeIntegerData(levelKey, newItem, minLevel);
    }

    @Override
    @MustBeInvokedByOverriders
    public void rightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        preventPotInteraction(event, itemInteracted);
    }

    private void preventPotInteraction(PlayerInteractEvent event, ItemStack itemInteracted) {
        assert event.getClickedBlock() != null;

        if (event.getClickedBlock().getType() != Material.DECORATED_POT) return;

        event.setCancelled(true);
    }

    public void setLevel(Integer level, ItemStack itemStack) {
        int currentLevel = ItemUtils.getIntegerDataFromWeaponKey(levelKey, itemStack);

        if (currentLevel == maxLevel) {
            return;
        }

        if (currentLevel + level > maxLevel) {
            return;
        }

        ItemUtils.storeIntegerData(levelKey, itemStack, level);
    }

    public boolean upgradeLevel(int amount, ItemStack item) {
        int level = ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);

        if (level == maxLevel) {
            return false;
        }

        ItemUtils.storeIntegerData(levelKey, item, Math.min(maxLevel, level + amount));
        return true;
    }

    public void downgradeLevel(int amount, ItemStack item) {
        int level = ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);

        if (level == minLevel) {
            return;
        }

        ItemUtils.storeIntegerData(levelKey, item, Math.max(minLevel, level - amount));
    }

    public final int getLevel(ItemStack item) {
        return ItemUtils.getIntegerDataFromWeaponKey(levelKey, item);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMinLevel() {
        return minLevel;
    }

}
