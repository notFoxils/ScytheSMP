package me.foxils.scythesmp.listeners;

import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.scythesmp.items.UpgradeableItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(itemStack -> (ItemRegistry.getItemFromItemStack(itemStack) instanceof UpgradeableItem));
    }
}
