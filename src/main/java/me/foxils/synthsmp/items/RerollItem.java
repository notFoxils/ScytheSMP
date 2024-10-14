package me.foxils.synthsmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.ItemRegistry;
import me.foxils.foxutils.itemactions.InventoryClickAction;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.synthsmp.utilities.RandomGemStuff;
import me.foxils.synthsmp.tables.PlayerStats;
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

    private void changeToRandomItem(InventoryClickEvent inventoryClickEvent) {
        if (!inventoryClickEvent.getSlotType().equals(InventoryType.SlotType.RESULT)) return;

        Player player = (Player) inventoryClickEvent.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();

        PlayerStats playerStats = PlayerStats.getDataObjectFromUUID(player.getUniqueId());

        Map<String, Integer> gemLevelMap = playerStats.getGemLevelMap();

        Arrays.stream(playerInventory.getContents()).forEach(itemStack -> {
            if (!(ItemRegistry.getItemFromItemStack(itemStack) instanceof UpgradeableItem upgradeableItem) || upgradeableItem.getRawName().contains("power")) return;

            upgradeableItem.downgradeLevel(1, itemStack);

            final int newLevel = upgradeableItem.getLevel(itemStack);
            final String rawItemName = upgradeableItem.getRawName();

            if (gemLevelMap.containsKey(rawItemName)) {
                gemLevelMap.replace(rawItemName, newLevel);
            } else {
                gemLevelMap.put(rawItemName, newLevel);
            }

            itemStack.setAmount(0);
        });

        UpgradeableItem item = RandomGemStuff.getRandomUpgradeableGem();
        ItemStack gemItemStack = item.createItem(1);
        final String rawItemName = item.getRawName();

        if (gemLevelMap.containsKey(rawItemName)) {
            item.setLevel(gemLevelMap.get(rawItemName), gemItemStack);
        } else {
            gemLevelMap.put(rawItemName, item.getMinLevel());
        }

        playerStats.setGemLevelMap(gemLevelMap);
        playerStats.setCurrentGem(rawItemName);
        playerStats.updateColumn();

        RandomGemStuff.ShowRandomUpgradeableItem showRandomUpgradeableItem = new RandomGemStuff.ShowRandomUpgradeableItem(player);
        RandomGemStuff.GiveRandomUpgradeableItem giveRandomUpgradeableItem = new RandomGemStuff.GiveRandomUpgradeableItem(plugin, player, showRandomUpgradeableItem, gemItemStack, RandomGemStuff.getPrimaryColorOfGemName(item.getName()) + "Rerolled Gem");

        Bukkit.getScheduler().runTaskLater(plugin, player::closeInventory, 1L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> playerInventory.remove(createItem(1)), 2L);

        showRandomUpgradeableItem.runTaskTimer(plugin, 1L, 2L);
        giveRandomUpgradeableItem.runTaskLater(plugin, 80L);
    }

    @Override
    public void onInvetoryPull(InventoryClickEvent inventoryClickEvent, ItemStack itemStack) {
        changeToRandomItem(inventoryClickEvent);
    }
}