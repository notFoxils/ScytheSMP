package me.foxils.scythesmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.InventoryClickAction;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.foxutils.utilities.ItemAbility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.List;

@SuppressWarnings("unused")
public abstract class UpgradeableItem extends Item implements ClickActions, InventoryClickAction {

    private final int minLevel;
    private final int maxLevel;

    private static final NamespacedKey LEVEL_KEY = NamespacedKey.fromString("sythesmp:upgrade-level");

    public UpgradeableItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, List<ItemStack> itemsForRecipe, boolean shapedRecipe, int maxLevel, int minLevel) {
        super(material, customModelData, name, plugin, abilityList, itemsForRecipe, shapedRecipe);

        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public UpgradeableItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, int maxLevel, int minLevel) {
        this(material, customModelData, name, plugin, abilityList, null, false, maxLevel, minLevel);
    }

    @Override
    public void onInvetoryPull(InventoryClickEvent event, ItemStack itemStack) {
        preventNonPlayerInventoryMovement(event);
    }

    private void preventNonPlayerInventoryMovement(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getSlotType() == InventoryType.SlotType.RESULT) return;
        if (event.getView().getTopInventory().getType() == InventoryType.CRAFTING) return;

        event.setCancelled(true);
    }

    @Override
    public ItemStack createItem(int amount) {
        ItemStack newItem = super.createItem(amount);

        ItemUtils.storeIntegerData(LEVEL_KEY, newItem, minLevel);

        return newItem;
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
        int currentLevel = ItemUtils.getIntegerDataFromWeaponKey(LEVEL_KEY, itemStack);

        if (currentLevel == maxLevel) {
            return;
        }

        if (currentLevel + level > maxLevel) {
            return;
        }

        ItemUtils.storeIntegerData(LEVEL_KEY, itemStack, level);
    }

    public boolean upgradeLevel(int amount, ItemStack item) {
        int level = ItemUtils.getIntegerDataFromWeaponKey(LEVEL_KEY, item);

        if (level == maxLevel) {
            return false;
        }

        ItemUtils.storeIntegerData(LEVEL_KEY, item, Math.min(maxLevel, level + amount));
        return true;
    }

    public void downgradeLevel(int amount, ItemStack item) {
        int level = ItemUtils.getIntegerDataFromWeaponKey(LEVEL_KEY, item);

        if (level == minLevel) {
            return;
        }

        ItemUtils.storeIntegerData(LEVEL_KEY, item, Math.max(minLevel, level - amount));
    }

    public final int getLevel(ItemStack item) {
        return ItemUtils.getIntegerDataFromWeaponKey(LEVEL_KEY, item);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMinLevel() {
        return minLevel;
    }

}
