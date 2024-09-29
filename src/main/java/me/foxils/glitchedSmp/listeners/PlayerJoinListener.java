package me.foxils.glitchedSmp.listeners;

import me.foxils.glitchedSmp.helpers.RandomGemStuff;
import me.foxils.glitchedSmp.tables.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinListener implements Listener {

    private final Plugin plugin;

    public PlayerJoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getLastPlayed() != 0 && !player.getName().equals("Foxils")) return;

        if (player.getName().equals("Foxils")) {
            PlayerStats.getDataObjectFromUUID(player.getUniqueId()).deleteColumn();
        }

        PlayerStats playerStats = new PlayerStats(player.getUniqueId());

        PlayerStats.createColumn(playerStats);

        RandomGemStuff.ShowRandomUpgradeableItem showRandomUpgradeableItem = new RandomGemStuff.ShowRandomUpgradeableItem(player);
        RandomGemStuff.GiveRandomUpgradeableItem giveRandomUpgradeableItem = new RandomGemStuff.GiveRandomUpgradeableItem(plugin, player, showRandomUpgradeableItem);

        showRandomUpgradeableItem.runTaskTimer(plugin, 0L, 2L);
        giveRandomUpgradeableItem.runTaskLater(plugin, 80L);
    }
}
