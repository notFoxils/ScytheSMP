package me.foxils.synthsmp.listeners;

import me.foxils.foxutils.utilities.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractEntityListener implements Listener {

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        Player player = event.getPlayer();

        ItemStack itemInteractedWith = player.getInventory().getItem(event.getHand());
        
        if (itemInteractedWith == null || !itemInteractedWith.hasItemMeta() || itemInteractedWith.getType().equals(Material.AIR)) return;
        
        if (!ItemUtils.isFoxItem(itemInteractedWith)) return;
        
        event.setCancelled(true);
    }
}
