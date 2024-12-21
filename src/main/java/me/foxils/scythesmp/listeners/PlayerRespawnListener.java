package me.foxils.scythesmp.listeners;

import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.scythesmp.items.UpgradeableItem;
import me.foxils.scythesmp.tables.PlayerStats;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRespawnListener implements Listener {

    private final Plugin plugin;

    public PlayerRespawnListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        givePlayerCurrentGem(event);
        upgradeKillerCurrentGem(event);
    }

    private void upgradeKillerCurrentGem(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final Player killer = player.getKiller();

        if (killer == null || player.equals(killer))
            return;

        final UUID killerUUID = killer.getUniqueId();

        PlayerStats playerStats = PlayerStats.getDataObjectFromUUID(killerUUID);

        if (playerStats == null) {
            playerStats = new PlayerStats(killerUUID);
            PlayerStats.createColumn(playerStats);
        }

        final String killerCurrentGem = playerStats.getCurrentGem();
        final HashMap<String, Integer> killerGemLevelMap = playerStats.getGemLevelMap();
        final int killerGemLevel = Math.min(5, killerGemLevelMap.get(killerCurrentGem) + 1);

        killerGemLevelMap.replace(killerCurrentGem, killerGemLevel);
        playerStats.setGemLevelMap(killerGemLevelMap);
        playerStats.updateColumn();

        final UpgradeableItem killerCurrentGemClass = (UpgradeableItem) ItemRegistry.getItemFromKey(new NamespacedKey(plugin, killerCurrentGem));

        for (ItemStack itemStack : killer.getInventory().getContents()) {
            if (!ItemRegistry.getItemFromItemStack(itemStack).equals(killerCurrentGemClass))
                continue;

            UpgradeableItem.setItemStackLevel(itemStack, killerGemLevel);
            break;
        }
    }

    private void givePlayerCurrentGem(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        final UUID playerUUID = player.getUniqueId();

        PlayerStats playerStats = PlayerStats.getDataObjectFromUUID(playerUUID);

        if (playerStats == null) {
            playerStats = new PlayerStats(playerUUID);
            PlayerStats.createColumn(playerStats);
        }

        final String playerCurrentGem = playerStats.getCurrentGem();
        final HashMap<String, Integer> gemLevelMap = playerStats.getGemLevelMap();
        final int playerCurrentGemLevel = Math.max(0, gemLevelMap.get(playerCurrentGem) - 1);

        gemLevelMap.replace(playerCurrentGem, playerCurrentGemLevel);

        playerStats.setGemLevelMap(gemLevelMap);
        playerStats.updateColumn();

        final UpgradeableItem playerGem = (UpgradeableItem) ItemRegistry.getItemFromKey(new NamespacedKey(plugin, playerCurrentGem));
        final ItemStack playerGemItem = playerGem.createItem(1);

        UpgradeableItem.setItemStackLevel(playerGemItem, playerCurrentGemLevel);

        player.getInventory().addItem(playerGemItem);
    }
}
