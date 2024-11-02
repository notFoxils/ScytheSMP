package me.foxils.scythesmp.listeners;

import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.scythesmp.items.UpgradeableItem;
import me.foxils.scythesmp.tables.PlayerStats;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class PlayerRespawnListener implements Listener {

    private final Plugin plugin;

    public PlayerRespawnListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        givePlayerCurrentGem(event);
    }

    private void givePlayerCurrentGem(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        final UUID playerUUID = player.getUniqueId();

        PlayerStats playerStats = PlayerStats.getDataObjectFromUUID(playerUUID);

        if (playerStats == null) playerStats = new PlayerStats(playerUUID);

        final String playerGemRawName = playerStats.getCurrentGem();

        final UpgradeableItem playerGem = (UpgradeableItem) ItemRegistry.getItemFromKey(new NamespacedKey(plugin, playerGemRawName));

        final ItemStack playerGemItem = playerGem.createItem(1);

        UpgradeableItem.setItemStackLevel(playerGemItem, playerStats.getGemLevelMap().get(playerGemRawName));

        player.getInventory().addItem(playerGemItem);
    }
}
