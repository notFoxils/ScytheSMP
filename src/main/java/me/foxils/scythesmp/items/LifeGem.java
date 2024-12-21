package me.foxils.scythesmp.items;

import me.foxils.foxutils.itemactions.AttackAction;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.scythesmp.utilities.EntityTracing;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class LifeGem extends UpgradeableItem implements PassiveAction, AttackAction, ClickActions {

    public final List<PotionEffect> passivePotionEffects = List.of(
            new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, false)
    );
    public final PotionEffect witheringEffect = new PotionEffect(PotionEffectType.WITHER, 300, 1, false, true);
    public final PotionEffect powerHeal = new PotionEffect(PotionEffectType.REGENERATION, 100, 4, false, true);

    public final NamespacedKey lifeStealCooldownKey = new NamespacedKey(plugin, "lifegem_lifesteal");
    public final NamespacedKey powerHealCooldownKey = new NamespacedKey(plugin, "lifegem_powerheal");
    public final NamespacedKey witheringCooldownKey = new NamespacedKey(plugin, "lifegem_withering");
    public final NamespacedKey passiveEffectsCooldownKey = new NamespacedKey(plugin, "lifegem_passive_effects");

    public LifeGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList) {
        super(material, customModelData, name, plugin, abilityList, 3, 0);
    }

    @Override
    public void shiftRightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        powerHeal(event, itemInteracted);
    }

    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        shiftRightClickAir(event, itemInteracted);
    }

    private void powerHeal(PlayerInteractEvent event, ItemStack itemInteracted) {
        Player player = event.getPlayer();

        if (ItemUtils.getCooldown(powerHealCooldownKey, itemInteracted, 120L, player, new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "Used Power-Heal"))) return;

        player.addPotionEffect(powerHeal);
    }

    @Override
    public void rightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        inflictWithering(event, itemInteracted);
    }

    private void inflictWithering(PlayerInteractEvent event, ItemStack itemInteracted) {
        Player playerInflicting = event.getPlayer();

        if (ItemUtils.getCooldown(witheringCooldownKey, itemInteracted, 120L, playerInflicting, new TextComponent(ChatColor.GRAY + "" + ChatColor.BOLD + "Used Wither-Away"))) return;

        Entity hitEntity = EntityTracing.getEntityLookingAt(playerInflicting);

        if (!(hitEntity instanceof Player hitPlayer)) return;

        hitPlayer.addPotionEffect(witheringEffect);
    }

    @Override
    public void attackAction(EntityDamageByEntityEvent entityDamageByEntityEvent, ItemStack itemStackUsedToAttack, ItemStack itemStackOfThisItem) {
        tempLifeSteal(entityDamageByEntityEvent, itemStackOfThisItem);
    }

    private void tempLifeSteal(EntityDamageByEntityEvent event, ItemStack thisItem) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player) || !(event.getEntity() instanceof Player playerAttacked)) return;

        final AttributeInstance attackedMaxHealthAttribute = playerAttacked.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        final AttributeInstance playerMaxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attackedMaxHealthAttribute == null || playerMaxHealthAttribute == null) return;

        final double attackedMaxHealthValue = attackedMaxHealthAttribute.getValue();

        if (attackedMaxHealthValue < attackedMaxHealthAttribute.getDefaultValue()) return;

        if (ItemUtils.getCooldown(lifeStealCooldownKey, thisItem, 180L, player, new TextComponent(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Used Life-Steal"))) return;

        final double playerMaxHealthValue = playerMaxHealthAttribute.getValue();

        final double attackedNewValue = attackedMaxHealthValue - 8;
        final double playerNewValue = playerMaxHealthValue + 8;

        attackedMaxHealthAttribute.setBaseValue(attackedNewValue);
        playerMaxHealthAttribute.setBaseValue(playerNewValue);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.isOnline()) playerMaxHealthAttribute.setBaseValue(playerMaxHealthValue);

            if (playerAttacked.isOnline()) attackedMaxHealthAttribute.setBaseValue(attackedMaxHealthValue);
        }, 600);
    }

    @Override
    public void passiveAction(Player player, ItemStack itemStack) {
        giveEffects(player, itemStack);
    }

    private void giveEffects(Player player, ItemStack thisItem) {
        if (ItemUtils.getCooldown(passiveEffectsCooldownKey, thisItem, 10L)) return;

        player.addPotionEffects(passivePotionEffects);
    }

}
