package me.foxils.scythesmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.foxutils.itemactions.InventoryClickAction;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.scythesmp.utilities.RandomGemStuff;
import me.foxils.scythesmp.tables.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class RerollItem extends Item implements InventoryClickAction {

    public RerollItem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, List<ItemStack> itemsForRecipe, boolean shapedRecipe) {
        super(material, customModelData, name, plugin, abilityList, itemsForRecipe, shapedRecipe);
    }

    private void changeToRandomItem(InventoryClickEvent inventoryClickEvent, ItemStack clickedItemStack) {
        if (!inventoryClickEvent.getSlotType().equals(InventoryType.SlotType.RESULT))
            return;

        final Player player = (Player) inventoryClickEvent.getWhoClicked();
        final PlayerInventory playerInventory = player.getInventory();

        final UUID playerUUID = player.getUniqueId();

        PlayerStats playerStats = PlayerStats.getDataObjectFromUUID(playerUUID);

        if (playerStats == null)
            playerStats = new PlayerStats(playerUUID);

        final HashMap<String, Integer> gemLevelMap = playerStats.getGemLevelMap();

        for (ItemStack itemStack : playerInventory.getContents()) {
            if (!(ItemRegistry.getItemFromItemStack(itemStack) instanceof UpgradeableItem upgradeableItem))
                continue;

            final int storedItemLevel = UpgradeableItem.getItemStackLevel(itemStack) - 1;
            final String rawItemName = upgradeableItem.getRawName();

            if (gemLevelMap.containsKey(rawItemName)) {
                gemLevelMap.replace(rawItemName, storedItemLevel);
            } else {
                gemLevelMap.put(rawItemName, storedItemLevel);
            }

            itemStack.setAmount(0);
        }

        final UpgradeableItem item = RandomGemStuff.getRandomUpgradeableGem();
        final ItemStack gemItemStack = item.createItem(1);
        final String rawItemName = item.getRawName();

        if (gemLevelMap.containsKey(rawItemName)) {
            UpgradeableItem.setItemStackLevel(gemItemStack, gemLevelMap.get(rawItemName));
        } else {
            gemLevelMap.put(rawItemName, item.getMinimumLevel());
        }

        playerStats.setGemLevelMap(gemLevelMap);
        playerStats.setCurrentGem(rawItemName);
        playerStats.updateColumn();

        // Fucking jank ass shit bro
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            final ItemStack[] playerInventoryContents = playerInventory.getContents();

            for (int i = 0; i < playerInventoryContents.length; i++) {
                final ItemStack itemStack = playerInventoryContents[i];

                if (itemStack == null || !itemStack.isSimilar(clickedItemStack))
                    continue;

                playerInventory.setItem(i, null);
            }

            playerInventory.removeItem(clickedItemStack);
            clickedItemStack.setAmount(0);
        }, 1L);
        Bukkit.getScheduler().runTaskLater(plugin, player::closeInventory, 2L);

        final RandomGemStuff.ShowRandomUpgradeableItem showRandomUpgradeableItem = new RandomGemStuff.ShowRandomUpgradeableItem(player);
        final RandomGemStuff.GiveRandomUpgradeableItem giveRandomUpgradeableItem = new RandomGemStuff.GiveRandomUpgradeableItem(plugin, player, showRandomUpgradeableItem, gemItemStack, RandomGemStuff.getPrimaryColorOfGemName(item.getName()) + "Rerolled Gem");

        showRandomUpgradeableItem.runTaskTimer(plugin, 1L, 2L);
        giveRandomUpgradeableItem.runTaskLater(plugin, 80L);
    }

    @Override
    public void onInvetoryPull(InventoryClickEvent event, ItemStack itemStack) {
        changeToRandomItem(event, itemStack);
    }
}