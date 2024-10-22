package me.foxils.synthsmp.listeners;

import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.synthsmp.items.UpgradeableItem;
import me.foxils.synthsmp.tables.PlayerStats;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
        Player player = event.getPlayer();

        PlayerStats playerStats = PlayerStats.getDataObjectFromUUID(player.getUniqueId());

        String playerGemRawName = playerStats.getCurrentGem();

        UpgradeableItem playerGem = (UpgradeableItem) ItemRegistry.getItemFromKey(new NamespacedKey(plugin, playerGemRawName));

        ItemStack playerGemItem = playerGem.createItem(1);
        playerGem.setLevel(playerStats.getGemLevelMap().get(playerGemRawName), playerGemItem);

        player.getInventory().addItem(playerGemItem);
    }
}
