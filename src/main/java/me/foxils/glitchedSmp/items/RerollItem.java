package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.ItemRegistry;
import me.foxils.foxutils.itemactions.InventoryClickAction;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.glitchedSmp.helpers.RandomGemStuff;
import me.foxils.glitchedSmp.tables.PlayerStats;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        if (!inventoryClickEvent.getSlotType().equals(InventoryType.SlotType.RESULT)) {
            return;
        }

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

        UpgradeableItem item = (UpgradeableItem) RandomGemStuff.getRandomGem();
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

        String itemName = item.getName();

        StringBuilder stringBuilder = new StringBuilder(itemName);

        if (itemName.chars().filter(num -> num == '§').count() > 6) {
            // This is for names that use hex color codes instead of regular color codes
            stringBuilder.delete(1, 17);
        } else {
            stringBuilder.delete(1, 5);
        }

        if (stringBuilder.toString().chars().filter(num -> num == '§').count() > 16) {
            stringBuilder.delete(17, stringBuilder.length());
        } else {
            stringBuilder.delete(5, stringBuilder.length());
        }

        String majorityChatColor = ChatColor.translateAlternateColorCodes('&', stringBuilder.toString());

        RandomGemStuff.ShowRandomUpgradeableItem showRandomUpgradeableItem = new RandomGemStuff.ShowRandomUpgradeableItem(player);

        Bukkit.getScheduler().runTaskLater(plugin, player::closeInventory, 1L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> playerInventory.remove(createItem(1)), 2L);
        showRandomUpgradeableItem.runTaskTimer(plugin, 1L, 2L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            showRandomUpgradeableItem.cancel();
            player.sendTitle(itemName, majorityChatColor + "Rerolled Gem", 5, 40, 5);
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1F, 0.9F);
            playerInventory.addItem(gemItemStack);
        }, 80L);
    }

    @Override
    public void onInvetoryPull(InventoryClickEvent inventoryClickEvent, ItemStack itemStack) {
        changeToRandomItem(inventoryClickEvent);
    }
}