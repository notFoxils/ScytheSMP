package me.foxils.synthsmp.items;

import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.DropAction;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.foxutils.utilities.ItemUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public class StrengthGem extends UpgradeableItem implements PassiveAction, DropAction, ClickActions {

    private final NamespacedKey sharpnessApplicationCooldownKey = new NamespacedKey(plugin, "sharpness_application_cooldown");

    private final NamespacedKey weaknessAbilityCooldownKey = new NamespacedKey(plugin, "weakness_ability_cooldown");

    private final NamespacedKey strengthIncreaseCooldownKey = new NamespacedKey(plugin, "strength_increase_cooldown");
    private final NamespacedKey strengthIncreaseBooleanKey = new NamespacedKey(plugin, "strength_increase_boolean");

    public StrengthGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList) {
        super(material, customModelData, name, plugin, abilityList, 3, 0);
    }

    @Override
    public void rightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        weaknessAbility(event, itemInteracted);
    }

    @Override
    public void rightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        super.rightClickBlock(event, itemInteracted);
        rightClickAir(event, itemInteracted);
    }

    private void weaknessAbility(PlayerInteractEvent playerInteractEvent, ItemStack itemStack) {
        Player player = playerInteractEvent.getPlayer();

        if (ItemUtils.getCooldown(weaknessAbilityCooldownKey, itemStack, 300, player, new TextComponent(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Activated Weakness Aura"))) {
            return;
        }

        List<Entity> entitiesNearby = player.getNearbyEntities(5, 2, 5);

        if (entitiesNearby.isEmpty()) {
            unsuccessfulAbility(player, new TextComponent(ChatColor.DARK_RED + "" + ChatColor.BOLD + "No nearby players to weaken"));
            return;
        }

        for (Entity entity : entitiesNearby) {
            if (!(entity instanceof LivingEntity livingEntity)) return;

            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0, true, false));
            entity.sendMessage(ChatColor.GRAY + "You have been weakened by " + ChatColor.BOLD + player.getName());
        }
    }

    @Override
    public void shiftRightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        strengthIncreaseAbility(event, itemInteracted);
    }

    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        shiftRightClickAir(event, itemInteracted);
    }

    private void strengthIncreaseAbility(PlayerInteractEvent event, ItemStack itemInteracted) {
        if (getLevel(itemInteracted) < 3) return;

        Player player = event.getPlayer();

        if (ItemUtils.getCooldown(strengthIncreaseCooldownKey, itemInteracted, 900, player, new TextComponent(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Activated Super Strength"))) {
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);

        ItemUtils.storeDataOfType(PersistentDataType.BOOLEAN, true, strengthIncreaseBooleanKey, itemInteracted);
    }

    @Override
    public void dropItemAction(PlayerDropItemEvent playerDropItemEvent, ItemStack itemStack) {

    }

    @Override
    public void passiveAction(Player player, ItemStack itemStack) {
        autoSharpness(player, itemStack);
        passiveEffects(player, itemStack);
    }

    private void autoSharpness(Player player, ItemStack thisItem) {
        if (ItemUtils.getCooldown(sharpnessApplicationCooldownKey, thisItem, 30)) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isSharpnessWeapon(item)) return;

        Map<Enchantment, Integer> itemCurrentEnchantmentMap = item.getEnchantments();

        Integer sharpnessLevel = itemCurrentEnchantmentMap.get(Enchantment.SHARPNESS);

        if (sharpnessLevel == null || sharpnessLevel < 5) item.addEnchantment(Enchantment.SHARPNESS, 5);
    }

    public static boolean isSharpnessWeapon(ItemStack weapon) {
        if (weapon == null) return false;

        Material stackType = weapon.getType();
        String typeNameLower = stackType.name().toLowerCase();

        if (typeNameLower.contains("pickaxe")) return false;

        return typeNameLower.contains("sword") || typeNameLower.contains("axe") || stackType.equals(Material.TRIDENT) || typeNameLower.contains("mace");
    }

    private void passiveEffects(Player player, ItemStack itemStack) {
        Boolean canIncreaseStrength = ItemUtils.getDataOfType(PersistentDataType.BOOLEAN, strengthIncreaseBooleanKey, itemStack);
        PotionEffect currentStrengthEffect = player.getPotionEffect(PotionEffectType.STRENGTH);

        if (canIncreaseStrength != null && canIncreaseStrength) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 2));
            ItemUtils.storeDataOfType(PersistentDataType.BOOLEAN, false, strengthIncreaseBooleanKey, itemStack);
        } else if (currentStrengthEffect == null || currentStrengthEffect.getAmplifier() <= 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1));
        }
    }

    private void unsuccessfulAbility(Player player, TextComponent unsuccessfulText) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, unsuccessfulText);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.5F, 1F), 10L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.5F, 0.75F), 14L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.5F, 0.5F), 18L);
    }

}
