package me.foxils.sytheSMP.items;

import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.DropAction;
import me.foxils.foxutils.itemactions.MineAction;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.foxutils.utilities.ItemAbility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VillagerGem extends UpgradeableItem implements PassiveAction, MineAction, DropAction, ClickActions {

    private final List<PotionEffect> defaultEffects = Arrays.asList(
            new PotionEffect(PotionEffectType.HASTE, 200, 1, false, false),
            new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false)
    );

    private final List<PotionEffect> maxedEffects = List.of(
            new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 200, 1)
    );

    private final PotionEffect heroOfTheVillageTen = new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 6000, 9);

    private final ItemStack villagerStack = new ItemStack(Material.VILLAGER_SPAWN_EGG, 1);

    private final NamespacedKey miningMultiplierCooldown = new NamespacedKey(plugin, "mining_multiplier_cooldown");
    private final NamespacedKey miningMultiplier = new NamespacedKey(plugin, "mining_multiplier");

    private final NamespacedKey fortuneEnchantCooldown = new NamespacedKey(plugin, "apply_fortune_cooldown");

    private final NamespacedKey heroTenCooldown = new NamespacedKey(plugin, "hero_ten_cooldown");

    private final NamespacedKey villagerEggCooldown = new NamespacedKey(plugin, "villager_egg_cooldown");

    public VillagerGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList) {
        super(material, customModelData, name, plugin, abilityList, 3, 0);
    }

    @Override
    public void dropItemAction(PlayerDropItemEvent event, ItemStack itemUsed) {
        Player player = event.getPlayer();

        if (getLevel(itemUsed) != 3) {
            return;
        }

        setDropMultiplier(itemUsed, player);
    }

    @Override
    public void blockMineAction(BlockBreakEvent blockBreakEvent, ItemStack itemStack, ItemStack thisItem) {
        if (getLevel(thisItem) != 3) {
            return;
        }

        multiplyOnMine(blockBreakEvent, thisItem, itemStack);
    }

    @Override
    public void rightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        int itemLevel = getLevel(item);

        if (itemLevel < 2) {
            return;
        }

        grantHeroTen(player, item);
    }

    @Override
    public void rightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        rightClickAir(event, itemInteracted);
    }
    @Override
    public void shiftRightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        rightClickAir(event, itemInteracted);
    }
    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        rightClickAir(event, itemInteracted);
    }

    @Override
    public void passiveAction(Player player, ItemStack item) {
        grantPassiveEffects(player);

        giveVillagerEgg(player, item);

        if (getLevel(item) < 1) return;

        enchantWithFortune(player, item);

        if (getLevel(item) != 3) return;

        grantMaxedEffects(player);
    }

    private void grantPassiveEffects(Player player) {
        player.addPotionEffects(defaultEffects);
    }

    private void giveVillagerEgg(Player player, ItemStack item) {
        if (ItemUtils.getCooldown(villagerEggCooldown, item, 900)) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.addItem(villagerStack);
    }

    private void enchantWithFortune(Player player, ItemStack thisItem) {
        if (ItemUtils.getCooldown(fortuneEnchantCooldown, thisItem, 900)) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!item.getType().name().toLowerCase().contains("pickaxe")) {
            return;
        }

        Map<Enchantment, Integer> itemCurrentEnchantmentMap = item.getEnchantments();

        if (itemCurrentEnchantmentMap.containsKey(Enchantment.FORTUNE)) {
            if (itemCurrentEnchantmentMap.get(Enchantment.FORTUNE) >= 2) {
                return;
            }
        }

        item.addEnchantment(Enchantment.FORTUNE, 2);
    }

    private void grantMaxedEffects(Player player) {
        player.addPotionEffects(maxedEffects);
    }

    private void setDropMultiplier(ItemStack itemUsed, Player player) {
        if (ItemUtils.getCooldown(miningMultiplierCooldown, itemUsed, 900, player, new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "Used Champion's Grace"))) {
            return;
        }

        ItemStack item = ItemUtils.storeIntegerData(miningMultiplier, itemUsed, 5);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }

                    if (!itemStack.isSimilar(item)) {
                        continue;
                    }

                    ItemUtils.storeIntegerData(miningMultiplier, itemStack, 1);
                }
            }
        }.runTaskLater(plugin, 100L);
    }

    private void grantHeroTen(Player player, ItemStack item) {
        if (ItemUtils.getCooldown(heroTenCooldown, item, 1800, player, new TextComponent(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Used Villager's Blessing"))) {
            return;
        }

        player.addPotionEffect(heroOfTheVillageTen);
    }

    private void multiplyOnMine(BlockBreakEvent blockBreakEvent, ItemStack thisItem, ItemStack itemUsedToMine) {
        Integer multiplier = ItemUtils.getIntegerDataFromWeaponKey(miningMultiplier, thisItem);

        if (multiplier == null) {
            ItemUtils.storeIntegerData(miningMultiplier, thisItem, 1);
            return;
        }

        if (multiplier == 1) {
            return;
        }

        blockBreakEvent.setCancelled(true);

        Collection<ItemStack> defaultDrops = blockBreakEvent.getBlock().getDrops(itemUsedToMine);
        blockBreakEvent.setDropItems(false);
        blockBreakEvent.getBlock().setType(Material.AIR);

        World world = blockBreakEvent.getBlock().getWorld();

        Location blockLocation = blockBreakEvent.getBlock().getLocation();

        for (ItemStack item : defaultDrops) {
            ItemStack multipliedItem = item.clone();

            int defaultAmount = item.getAmount();

            multipliedItem.setAmount(defaultAmount * multiplier);

            world.dropItem(blockLocation, multipliedItem);
        }
    }


}