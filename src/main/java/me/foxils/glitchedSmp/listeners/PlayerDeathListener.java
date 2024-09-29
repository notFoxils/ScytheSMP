package me.foxils.glitchedSmp.listeners;

import me.foxils.foxutils.utilities.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;

        event.getDrops().removeIf(ItemUtils::isFoxItem);
    }
}
