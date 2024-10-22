package me.foxils.scythesmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.DropAction;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.foxutils.utilities.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class PowerGem extends Item implements PassiveAction, ClickActions, DropAction {

    // Unfinished

    public final NamespacedKey summonDragonCooldown = new NamespacedKey(plugin, "player_dragon_cooldown");

    public final NamespacedKey shootDragonBreathCooldown = new NamespacedKey(plugin, "dragon_breath_cooldown");
    public final NamespacedKey shootDragonBreathCharge = new NamespacedKey(plugin, "dragon_breath_charge");

    private static final List<PotionEffect> passivePotionEffects = Arrays.asList(
            new PotionEffect(PotionEffectType.STRENGTH, 200, 1, true, true),
            new PotionEffect(PotionEffectType.SPEED, 200, 1, true, true)
    );

    public PowerGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList, List<ItemStack> itemsForRecipe, boolean shapedRecipe) {
        super(material, customModelData, name, plugin, abilityList, itemsForRecipe, shapedRecipe);
    }

    @Override
    public void dropItemAction(PlayerDropItemEvent event, ItemStack itemUsed) {
        summonDragon(event, itemUsed);
    }

    private void summonDragon(PlayerDropItemEvent event, ItemStack itemUsed) {
        Player player = event.getPlayer();

        //if (ItemUtils.getCooldown(summonDragonCooldown, itemUsed, 300)) return;

        World world = player.getWorld();

        EnderDragon dragon = world.spawn(player.getLocation().add(0, 20, 0), EnderDragon.class, enderDragon -> {
            enderDragon.setGlowing(true);
            enderDragon.setAI(false);
            enderDragon.addPassenger(player);
        });

        Bukkit.getScheduler().runTaskLater(plugin, dragon::remove, 600L);
    }

    @Override
    public void shiftRightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        toggleShootDragonBreath(event, itemInteracted);
    }

    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        shiftRightClickAir(event, itemInteracted);
    }

    private void toggleShootDragonBreath(PlayerInteractEvent event, ItemStack itemInteracted) {
        Player player = event.getPlayer();

        Long timeNowSeconds = System.currentTimeMillis() / 1000;
        Long lastShootTimeSeconds = ItemUtils.getDataOfType(PersistentDataType.LONG, shootDragonBreathCharge, itemInteracted);

        if (lastShootTimeSeconds == null) {
            ItemUtils.storeDataOfType(PersistentDataType.LONG, timeNowSeconds, shootDragonBreathCharge, itemInteracted);
            return;
        }

        if (timeNowSeconds - lastShootTimeSeconds > 4) {
            launchDragonFireball(player);
            ItemUtils.storeDataOfType(PersistentDataType.LONG, timeNowSeconds, shootDragonBreathCharge, itemInteracted);
        }
    }

    private void launchDragonFireball(Player player) {
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, 1F, 0.5F);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, 1F, 0.8F), 3L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, 1F, 1.1F);
            player.launchProjectile(DragonFireball.class, player.getEyeLocation().getDirection());
        }, 6L);
    }

    @Override
    public void passiveAction(Player player, ItemStack itemStack) {
        passiveEffects(player);
    }

    private void passiveEffects(Player player) {
        player.addPotionEffects(passivePotionEffects);
    }
}
