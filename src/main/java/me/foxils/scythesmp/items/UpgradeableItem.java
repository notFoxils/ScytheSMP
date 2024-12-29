package me.foxils.scythesmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.InventoryClickActions;
import me.foxils.foxutils.registry.ItemRegistry;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public abstract class UpgradeableItem extends Item implements ClickActions, InventoryClickActions {

    private final NamespacedKey LEVEL_KEY;

    private final int minLevel;
    private final int maxLevel;

    public UpgradeableItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, List<ItemStack> itemsForRecipe, boolean shapedRecipe, int maxLevel, int minLevel) {
        super(material, customModelData, name, plugin, abilityList, itemsForRecipe, shapedRecipe);

        this.minLevel = minLevel;
        this.maxLevel = maxLevel;

        this.LEVEL_KEY = new NamespacedKey(plugin, "upgrade_level");
    }

    public UpgradeableItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, int maxLevel, int minLevel) {
        this(material, customModelData, name, plugin, abilityList, null, false, maxLevel, minLevel);
    }

    @Override
    public ItemStack createItem(int amount) {
        ItemStack newItem = super.createItem(amount);

        if (!setItemStackLevel(newItem, minLevel)) {
            plugin.getLogger().severe("Error creating ItemStack for " + getClass().getName() + " item class: Cannot set " + LEVEL_KEY);
        }

        return newItem;
    }

    private void preventNonPlayerInventoryMovement(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getSlotType() == InventoryType.SlotType.RESULT) return;
        if (event.getView().getTopInventory().getType() == InventoryType.CRAFTING) return;

        event.setCancelled(true);
    }

    @Override
    public void onInvetoryClick(InventoryClickEvent inventoryClickEvent, ItemStack itemStack, @Nullable ItemStack itemStack1) {
        preventNonPlayerInventoryMovement(inventoryClickEvent);
    }

    @Override
    public void onInvetoryInteract(InventoryClickEvent inventoryClickEvent, ItemStack itemStack, @Nullable ItemStack itemStack1) {
        preventNonPlayerInventoryMovement(inventoryClickEvent);
    }

    private void preventPotInteraction(PlayerInteractEvent event, ItemStack itemInteracted) {
        assert event.getClickedBlock() != null;

        if (event.getClickedBlock().getType() != Material.DECORATED_POT) return;

        event.setCancelled(true);
    }

    @Override
    @MustBeInvokedByOverriders
    public void rightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        preventPotInteraction(event, itemInteracted);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean setItemStackLevel(@NotNull ItemStack upgradeableItemStack, int level) {
        if (!(ItemRegistry.getItemFromItemStack(upgradeableItemStack) instanceof UpgradeableItem upgradeableItem)) return false;

        if (level > upgradeableItem.getMaximumLevel() || level < upgradeableItem.getMinimumLevel()) return false;

        return ItemUtils.storeIntegerData(level, upgradeableItem.LEVEL_KEY, upgradeableItemStack);
    }

    public static Integer getItemStackLevel(@NotNull ItemStack upgradeableItemStack) {
        if (!(ItemRegistry.getItemFromItemStack(upgradeableItemStack) instanceof UpgradeableItem upgradeableItem)) return 0;

        final Integer itemLevel = ItemUtils.getIntegerData(upgradeableItem.LEVEL_KEY, upgradeableItemStack);

        if (itemLevel == null) return -1;

        return itemLevel;
    }

    public int getMaximumLevel() {
        return maxLevel;
    }

    public int getMinimumLevel() {
        return minLevel;
    }

}
