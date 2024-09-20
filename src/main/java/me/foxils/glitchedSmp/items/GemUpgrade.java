package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.ItemRegistry;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.utilities.ItemAbility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GemUpgrade extends Item implements ClickActions { //InventoryClickAction as well
    public GemUpgrade(Material material, String name, NamespacedKey key, List<ItemAbility> abilityList, List<ItemStack> itemsForRecipe, boolean shapedRecipe) {
        super(material, name, key, abilityList, itemsForRecipe, shapedRecipe);
    }

    @Override
    public void rightClickAir(PlayerInteractEvent event) {
        // Implement upgrading gems in inventory

        // Some looping, etc
        Player player = event.getPlayer();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }

            Item customItemClass = ItemRegistry.getItemFromItemStack(item);

            if (!(customItemClass instanceof UpgradeableItem upgradeableCustomItem)) {
                continue;
            }

            ItemStack upgradeItem = event.getItem();

            if (upgradeItem == null) {
                return;
            }

            int upgradeItemAmount = upgradeItem.getAmount();

            int i;

            for (i = 0; i < upgradeItemAmount; i++) {
                upgradeableCustomItem.upgradeLevel(1, item);
            }

            player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation(), 1);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);

            upgradeItem.setAmount(upgradeItemAmount - (i+1));
            return;
        }
    }

    @Override
    public void rightClickBlock(PlayerInteractEvent event) {
        rightClickAir(event);
    }
    @Override
    public void shiftRightClickAir(PlayerInteractEvent event) {
        rightClickAir(event);
    }
    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event) {
        rightClickAir(event);
    }
}
