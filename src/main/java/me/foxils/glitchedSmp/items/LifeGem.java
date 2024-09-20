package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.itemactions.AttackAction;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.glitchedSmp.GlitchedSmp;
import me.foxils.foxutils.utilities.ItemAbility;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class LifeGem extends UpgradeableItem implements PassiveAction, AttackAction, ClickActions {

    // Note for anyone decompiling my code (why) I got lazy starting with this one.
    // the earth, air, water, and villager gems all had an amount of effort put in to make the code clean (even if they were failed to look presentable, [customThrowCollision], cough cough..), this one not so much
    // there's also a lot of things I dislike in this codebase that I have no reason to change but would clean things up to a point
    // don't judge, unless it's a truly critical mistake
    //
    // -- Foxils, (@foxils.)

    public final List<PotionEffect> passivePotionEffects = List.of(
            new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, false)
    );
    public final PotionEffect witheringEffect = new PotionEffect(PotionEffectType.WITHER, 200, 1, false, true);
    public final PotionEffect powerHeal = new PotionEffect(PotionEffectType.REGENERATION, 100, 4, false, true);

    public final NamespacedKey lifeStealCooldownKey = new NamespacedKey(plugin, "lifegem_lifesteal");
    public final NamespacedKey powerHealCooldownKey = new NamespacedKey(plugin, "lifegem_powerheal");
    public final NamespacedKey witheringCooldownKey = new NamespacedKey(plugin, "lifegem_withering");
    public final NamespacedKey passiveEffectsCooldownKey = new NamespacedKey(plugin, "lifegem_passive_effects");

    public static final Plugin plugin = GlitchedSmp.getInstance();

    public LifeGem(Material material, int customModelData, String name, NamespacedKey key, List<ItemAbility> abilityList) {
        super(material, customModelData, name, key, abilityList, 3, 0);
    }

    @Override
    public void shiftRightClickAir(PlayerInteractEvent event) {
        powerHeal(event);
    }

    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event) {
        shiftRightClickAir(event);
    }

    private void powerHeal(PlayerInteractEvent event) {
        if (ItemUtils.getCooldown(powerHealCooldownKey, event.getItem(), 120)) {
            return;
        }

        Player player = event.getPlayer();

        player.addPotionEffect(powerHeal);
    }

    @Override
    public void rightClickAir(PlayerInteractEvent event) {
        inflictWithering(event);
    }

    private void inflictWithering(PlayerInteractEvent event) {
        if (ItemUtils.getCooldown(witheringCooldownKey, event.getItem(), 120)) {
            return;
        }

        Player playerInflicting = event.getPlayer();
        Entity hitEntity = getEntityLookingAt(playerInflicting, 5);

        if (!(hitEntity instanceof Player hitPlayer)) {
            return;
        }

        hitPlayer.addPotionEffect(witheringEffect);
    }

    @Override
    public void attackAction(EntityDamageByEntityEvent entityDamageByEntityEvent, ItemStack thisItem) {
        tempLifeSteal(entityDamageByEntityEvent, thisItem);
    }

    private void tempLifeSteal(EntityDamageByEntityEvent event, ItemStack thisItem) {
        if (!(event.getDamager() instanceof Player player) || !(event.getEntity() instanceof  Player playerAttacked)) {
            return;
        }

        if (ItemUtils.getCooldown(lifeStealCooldownKey, thisItem, 180)) {
            return;
        }

        AttributeInstance attckedMaxHealthAttribute = playerAttacked.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance playerMaxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        double attackedMaxHealthValue = attckedMaxHealthAttribute.getValue();
        double playerMaxHealthValue = playerMaxHealthAttribute.getValue();

        double attackedNewValue;
        double playerNewValue;

        attackedNewValue = attackedMaxHealthValue - 8;
        playerNewValue = playerMaxHealthValue + 8;

        if (attackedMaxHealthValue <= 4 && attackedMaxHealthValue > 2) {
            attackedNewValue = attackedMaxHealthValue - 4;
            playerNewValue = playerMaxHealthValue + 4;
        }

        attckedMaxHealthAttribute.setBaseValue(attackedNewValue);
        playerMaxHealthAttribute.setBaseValue(playerNewValue);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            attckedMaxHealthAttribute.setBaseValue(attackedMaxHealthValue);
            playerMaxHealthAttribute.setBaseValue(playerMaxHealthValue);
        }, 600);
    }

    @Override
    public void passiveAction(Player player, ItemStack itemStack) {
        giveEffects(player, itemStack);
    }

    private void giveEffects(Player player, ItemStack thisItem) {
        if (ItemUtils.getCooldown(passiveEffectsCooldownKey, thisItem, 10)) {
            return;
        }

        player.addPotionEffects(passivePotionEffects);
    }

    @Nullable
    private LivingEntity getEntityLookingAt(Player player, double distanceAhead) {
        // I know this is a copy from WaterGem, whatcha gonna do about it (read the top blurb)
        World world = player.getWorld();

        Location eyeLocation = player.getEyeLocation().clone();

        Vector direction = eyeLocation.getDirection().clone();

        RayTraceResult traceResult = world.rayTraceEntities(eyeLocation.add(direction.clone().multiply(0.5)), eyeLocation.getDirection(), distanceAhead);

        if (traceResult == null) {
            return null;
        }

        Entity tracedEntity = traceResult.getHitEntity();

        if (!(tracedEntity instanceof LivingEntity livingEntity) || livingEntity.isInvulnerable() || livingEntity.equals(player)) {
            return null;
        }

        return livingEntity;
    }

}
