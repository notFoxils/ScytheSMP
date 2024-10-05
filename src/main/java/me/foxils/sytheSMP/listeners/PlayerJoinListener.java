package me.foxils.sytheSMP.listeners;

import me.foxils.foxutils.ItemRegistry;
import me.foxils.sytheSMP.helpers.RandomGemStuff;
import me.foxils.sytheSMP.tables.PlayerStats;
import org.bukkit.NamespacedKey;
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

        if (player.getLastPlayed() != 0) return;//&& !player.getName().equals("Foxils")) return;

        //if (player.getName().equals("Foxils")) PlayerStats.getDataObjectFromUUID(player.getUniqueId()).deleteColumn();

        PlayerStats playerStats = new PlayerStats(player.getUniqueId());

        PlayerStats.createColumn(playerStats);

        RandomGemStuff.ShowRandomUpgradeableItem showRandomUpgradeableItem = new RandomGemStuff.ShowRandomUpgradeableItem(player);
        RandomGemStuff.GiveRandomUpgradeableItem giveRandomUpgradeableItem = new RandomGemStuff.GiveRandomUpgradeableItem(plugin, player, showRandomUpgradeableItem, ItemRegistry.getItemFromKey(new NamespacedKey(plugin, playerStats.getCurrentGem())).createItem(1));

        showRandomUpgradeableItem.runTaskTimer(plugin, 0L, 2L);
        giveRandomUpgradeableItem.runTaskLater(plugin, 80L);
    }
}
