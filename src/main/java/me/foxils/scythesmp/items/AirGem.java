package me.foxils.scythesmp.items;

import me.foxils.foxutils.itemactions.*;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.scythesmp.ScytheSMP;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class AirGem extends UpgradeableItem implements TakeDamageAction, AttackAction, DoubleJumpAction, PassiveAction, DropAction {

    private final PotionEffect attackBuffEffect = new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false);
    private final PotionEffect attackDebuffEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 0, false, false);

    private final NamespacedKey airAttackEffectsKey = new NamespacedKey(plugin, "air_attack_effects_cooldown");
    private final NamespacedKey airPunchKey = new NamespacedKey(plugin, "air_punch_cooldown");

    public AirGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList) {
        super(material, customModelData, name, plugin, abilityList, 3, 0);
    }

    @Override
    public void attackAction(EntityDamageByEntityEvent entityDamageByEntityEvent, ItemStack item) {
        LivingEntity attacker = (LivingEntity) entityDamageByEntityEvent.getDamager();
        LivingEntity damaged = (LivingEntity) entityDamageByEntityEvent.getEntity();

        if (getLevel(item) < 1) {
            return;
        }

        airPunch(attacker, damaged, item);

        if (getLevel(item) < 2) {
            return;
        }

        airAttackEffects(attacker, damaged, item);
    }

    private void airPunch(LivingEntity attacker, LivingEntity damaged, ItemStack item) {
        if (ItemUtils.getCooldown(airPunchKey, item, 120, (Player) attacker, new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "Used Light Wind"))) {
            return;
        }

        Vector lookDir = attacker.getEyeLocation().getDirection().clone().multiply(new Vector(0.75, 1.5, 0.75));

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> damaged.setVelocity(lookDir), 1L);
    }

    private void airAttackEffects(LivingEntity attacker, LivingEntity damaged, ItemStack item) {
        if (ItemUtils.getCooldown(airAttackEffectsKey, item, 120)) {
            return;
        }

        attacker.addPotionEffect(attackBuffEffect);
        damaged.addPotionEffect(attackDebuffEffect);
    }

    @Override
    public void onTakeDamage(EntityDamageEvent entityDamageEvent, ItemStack itemUsedToFire) {
        cancelFallDamage(entityDamageEvent);
    }

    private void cancelFallDamage(EntityDamageEvent event) {
        if (!(event.getDamageSource().getDamageType() == DamageType.FALL)) {
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public void doubleJumpAction(PlayerToggleFlightEvent playerToggleFlightEvent, ItemStack item) {
        doubleJump(playerToggleFlightEvent, item);

        if (getLevel(item) != 3) {
            return;
        }

        playerFlight(playerToggleFlightEvent, item);
    }

    private void doubleJump(PlayerToggleFlightEvent event, ItemStack item) {
        Player player = event.getPlayer();

        if (!player.isSneaking()) {
            return;
        }

        if (ItemUtils.getCooldown(new NamespacedKey(plugin, "airgem_doublejump"), item, 5, player, new TextComponent(ChatColor.WHITE + "" + ChatColor.BOLD + "Used Double Jump"))) {
            return;
        }

        Location playerLocation = player.getLocation().clone();

        if (playerLocation.subtract(0, 2, 0).getBlock().getType() == Material.AIR) {
            return;
        }

        Vector playerLookDirection = playerLocation.getDirection().add(new Vector(0, 0.25, 0));

        Vector newVelocity = playerLookDirection.clone().multiply(1);

        player.setVelocity(newVelocity);
    }

    private void playerFlight(PlayerToggleFlightEvent event, ItemStack item) {
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            return;
        }

        if (ItemUtils.getCooldown(new NamespacedKey(plugin, "airgem_flight"), item, 420, player, new TextComponent(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Used Air-Channeling"))) {
            return;
        }

        player.setFlying(true);

        ScytheSMP.taskIDs.add(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.setFlying(false), 200));
    }

    @Override
    public void passiveAction(Player player, ItemStack item) {
        player.setAllowFlight(true);
    }

    @Override
    public void dropItemAction(PlayerDropItemEvent playerDropItemEvent, ItemStack itemStack) {
        Player player = playerDropItemEvent.getPlayer();

        player.setAllowFlight(false);
    }
}
