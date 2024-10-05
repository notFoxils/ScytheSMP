package me.foxils.sytheSMP.listeners;

import me.foxils.foxutils.utilities.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;

        event.getDrops().removeIf(item -> {
            if (item == null || !item.hasItemMeta() || item.getType().equals(Material.AIR)) return false;

            return ItemUtils.isFoxItem(item);
        });
    }
}
