package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.DropAction;
import me.foxils.foxutils.itemactions.MineAction;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.glitchedSmp.GlitchedSmp;
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

    private static final Plugin plugin = GlitchedSmp.getInstance();

    private static final List<PotionEffect> defaultEffects = Arrays.asList(
            new PotionEffect(PotionEffectType.HASTE, 200, 1, false, false),
            new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false)
    );

    private static final List<PotionEffect> maxedEffects = List.of(
            new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 200, 1)
    );

    private static final PotionEffect heroOfTheVillageTen = new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 6000, 9);

    private static final ItemStack villagerEggs = new ItemStack(Material.VILLAGER_SPAWN_EGG, 2);

    private static final NamespacedKey miningMultiplier = new NamespacedKey(plugin, "mining_multiplier");
    private static final NamespacedKey miningMultiplierCooldown = new NamespacedKey(plugin, "mining_multiplier_cooldown");
    private static final NamespacedKey fortuneEnchantCooldown = new NamespacedKey(plugin, "apply_fortune_cooldown");
    private static final NamespacedKey heroTenCooldown = new NamespacedKey(plugin, "hero_ten_cooldown");
    private static final NamespacedKey villagerEggCooldown = new NamespacedKey(plugin, "villager_egg_cooldown");

    public VillagerGem(Material material, String name, NamespacedKey key, List<ItemStack> itemsForRecipe, boolean shapedRecipe) {
        super(material, name, key, itemsForRecipe, shapedRecipe, 3, 0);
    }

    @Override
    public ItemStack createItem(int amount) {
        ItemStack newItem = super.createItem(amount);

        return ItemUtils.storeIntegerData(1, newItem, miningMultiplier);
    }

    @Override
    public void dropItemAction(PlayerDropItemEvent event, ItemStack itemUsed) {
        Player player = event.getPlayer();

        if (getLevel(itemUsed) != 3) {
            return;
        }

        setDropMultiplier(itemUsed, player);
    }

    private void setDropMultiplier(ItemStack itemUsed, Player player) {
        if (ItemUtils.getCooldown(miningMultiplierCooldown, itemUsed, 900)) {
            return;
        }

        ItemStack item = ItemUtils.storeIntegerData(5, itemUsed, miningMultiplier);

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

                    ItemUtils.storeIntegerData(1, itemStack, miningMultiplier);
                }
            }
        }.runTaskLater(plugin, 100L);
    }

    @Override
    public void blockMineAction(BlockBreakEvent blockBreakEvent, ItemStack itemStack, ItemStack thisItem) {
        if (getLevel(thisItem) != 3) {
            return;
        }

        multiplyOnMine(blockBreakEvent, thisItem, itemStack);
    }

    private void multiplyOnMine(BlockBreakEvent blockBreakEvent, ItemStack thisItem, ItemStack itemUsedToMine) {
        int multiplier = ItemUtils.getIntegerDataFromWeaponKey(miningMultiplier, thisItem);

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

    @Override
    public void rightClickAir(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        int itemLevel = getLevel(item);

        if (itemLevel < 2) {
            return;
        }

        grantHeroTen(player, item);
    }

    private void grantHeroTen(Player player, ItemStack item) {
        if (ItemUtils.getCooldown(heroTenCooldown, item, 1800)) {
            return;
        }

        player.addPotionEffect(heroOfTheVillageTen);
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

    @Override
    public void passiveAction(Player player, ItemStack item) {
        grantDefaultEffects(player);
        giveVillagerEgg(player, item);

        if (getLevel(item) < 1) {
            return;
        }

        enchantWithFortune(player, item);

        if (getLevel(item) != 3) {
            return;
        }

        grantMaxedEffects(player);
    }

    private void grantDefaultEffects(Player player) {
        player.addPotionEffects(defaultEffects);
    }

    private void giveVillagerEgg(Player player, ItemStack item) {
        if (ItemUtils.getCooldown(villagerEggCooldown, item, 3600)) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.addItem(villagerEggs);
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
}