package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.itemactions.AttackAction;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.DropAction;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.foxutils.utilities.ItemUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class SpeedGem extends UpgradeableItem implements DropAction, ClickActions, AttackAction, PassiveAction {
    private static final Random random = new Random();

    private final NamespacedKey lightningAbilityCooldownKey = new NamespacedKey(plugin, "lightning_ability_cooldown");

    private final NamespacedKey speedIncreaseCooldownKey = new NamespacedKey(plugin, "speed_increase_cooldown");
    private final NamespacedKey speedIncreaseBooleanKey = new NamespacedKey(plugin, "speed_increase_boolean");

    private final NamespacedKey hasteIncreaseCooldownKey = new NamespacedKey(plugin, "haste_increase_cooldown");
    private final NamespacedKey hasteIncreaseBooleanKey = new NamespacedKey(plugin, "haste_increase_boolean");

    private final NamespacedKey randomLightningCooldownKey = new NamespacedKey(plugin, "random_lightning_cooldown");
    private final NamespacedKey randomLightningBooleanKey = new NamespacedKey(plugin, "random_lightning_boolean");

    public SpeedGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList) {
        super(material, customModelData, name, plugin, abilityList, 3, 1);
    }

    @Override
    public void shiftLeftClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        hasteIncreaseAbility(event, itemInteracted);
    }

    @Override
    public void shiftLeftClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {shiftLeftClickAir(event, itemInteracted);}

    private void hasteIncreaseAbility(PlayerInteractEvent event, ItemStack itemInteracted) {
        if (getLevel(itemInteracted) < 3) return;

        Player player = event.getPlayer();

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.75F);

        if (ItemUtils.getCooldown(hasteIncreaseCooldownKey, itemInteracted, 900)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Wait for cooldown"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.5F);
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Activated Fast Hands"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);

        ItemUtils.storeDataOfType(PersistentDataType.BOOLEAN, true, hasteIncreaseBooleanKey, itemInteracted);
    }

    @Override
    public void shiftRightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        randomLightningHitAbility(itemInteracted);
        speedIncreaseAbility(event, itemInteracted);
    }

    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        shiftRightClickAir(event, itemInteracted);
    }

    private void randomLightningHitAbility(ItemStack itemInteracted) {
        if (getLevel(itemInteracted) < 2) return;
        if (ItemUtils.getCooldown(randomLightningCooldownKey, itemInteracted, 900)) return;

        if (random.nextInt(0, 100) > 90) ItemUtils.storeDataOfType(PersistentDataType.BOOLEAN, true, randomLightningBooleanKey, itemInteracted);
    }

    private void speedIncreaseAbility(PlayerInteractEvent event, ItemStack itemInteracted) {
        if (getLevel(itemInteracted) < 2) return;

        Player player = event.getPlayer();

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.75F);

        if (ItemUtils.getCooldown(speedIncreaseCooldownKey, itemInteracted, 900)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Wait for cooldown"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.5F);
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Activated Solo Strike"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);

        ItemUtils.storeDataOfType(PersistentDataType.BOOLEAN, true, speedIncreaseBooleanKey, itemInteracted);
    }

    @Override
    public void attackAction(EntityDamageByEntityEvent entityDamageByEntityEvent, ItemStack itemStack) {
        lightningStrikeHit(entityDamageByEntityEvent, itemStack);
    }

    private void lightningStrikeHit(EntityDamageByEntityEvent entityDamageByEntityEvent, ItemStack itemStack) {
        Boolean canIncrease = ItemUtils.getDataOfType(PersistentDataType.BOOLEAN, randomLightningBooleanKey, itemStack);

        if (canIncrease == null || !canIncrease) return;

        ItemUtils.storeDataOfType(PersistentDataType.BOOLEAN, false, randomLightningBooleanKey, itemStack);

        // Not sure if I should do this, but it makes sense
        LivingEntity entityHit = (LivingEntity) entityDamageByEntityEvent.getEntity();

        World entityHitWorld = entityHit.getWorld();
        Location entityHitLocation = entityHit.getEyeLocation();

        entityHitWorld.spawnParticle(Particle.ELECTRIC_SPARK, entityHitLocation, 40, 0, 0, 0, 3, null, true);

        entityHitWorld.strikeLightningEffect(entityHitLocation);
    }

    @Override
    public void dropItemAction(PlayerDropItemEvent playerDropItemEvent, ItemStack itemStack) {
        lightningAbility(playerDropItemEvent, itemStack);
    }

    private void lightningAbility(PlayerDropItemEvent playerDropItemEvent, ItemStack itemStack) {
        Player player = playerDropItemEvent.getPlayer();

        if (!player.isSneaking()) {
            return;
        }

        if (ItemUtils.getCooldown(lightningAbilityCooldownKey, itemStack, 600, player, new TextComponent(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Activated Static Wrath"))) {
            return;
        }

        List<Entity> entitiesNearby = player.getNearbyEntities(5, 2, 5);

        if (entitiesNearby.isEmpty()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "" + ChatColor.BOLD + "No nearby entities to strike"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.5F);
            return;
        }

        for (Entity entity : entitiesNearby) {
            entity.getWorld().strikeLightning(entity.getLocation()).setFireTicks(0);

            entity.sendMessage(ChatColor.DARK_AQUA + "You have been shocked by" + ChatColor.BOLD + player.getName());
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
    }

    @Override
    public void passiveAction(Player player, ItemStack itemStack) {
        passiveEffects(player, itemStack);
    }

    private void passiveEffects(Player player, ItemStack itemStack) {
        int itemLevel = getLevel(itemStack);

        Boolean canIncreaseSpeed = ItemUtils.getDataOfType(PersistentDataType.BOOLEAN, speedIncreaseBooleanKey, itemStack);
        PotionEffect currentSpeedEffect = player.getPotionEffect(PotionEffectType.SPEED);

        if (canIncreaseSpeed != null && canIncreaseSpeed) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 4));
            ItemUtils.storeDataOfType(PersistentDataType.BOOLEAN, false, speedIncreaseBooleanKey, itemStack);
        } else if (currentSpeedEffect == null || currentSpeedEffect.getAmplifier() <= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, itemLevel - 1));
        }

        if (itemLevel <= 1) {
            return;
        }

        Boolean canIncreaseHaste = ItemUtils.getDataOfType(PersistentDataType.BOOLEAN, hasteIncreaseBooleanKey, itemStack);
        PotionEffect currentHasteEffect = player.getPotionEffect(PotionEffectType.HASTE);

        if (canIncreaseHaste != null && canIncreaseHaste) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, 3));
            ItemUtils.storeDataOfType(PersistentDataType.BOOLEAN, false, hasteIncreaseBooleanKey, itemStack);
        } else if (currentHasteEffect == null || currentHasteEffect.getAmplifier() <= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, itemLevel - 2));
        }
    }

}