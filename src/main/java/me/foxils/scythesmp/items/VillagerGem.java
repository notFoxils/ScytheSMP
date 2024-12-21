package me.foxils.scythesmp.items;

import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.DropAction;
import me.foxils.foxutils.itemactions.MineAction;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.foxutils.utilities.ItemAbility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VillagerGem extends UpgradeableItem implements PassiveAction, MineAction, DropAction, ClickActions {

    private static final List<PotionEffect> NON_MAXED_GEM_PASSIVE_EFFECTS = List.of(
            new PotionEffect(PotionEffectType.HASTE, 200, 1, false, false),
            new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false)
    );

    private static final List<PotionEffect> MAXED_GEM_PASSIVE_EFFECTS = List.of(
            new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 200, 1)
    );

    private static final PotionEffect HERO_OF_THE_VILLAGE_TEN = new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 6000, 9);

    private static final ItemStack VILLAGER_EGG = new ItemStack(Material.VILLAGER_SPAWN_EGG, 1);

    private static final List<Material> VILLAGER_MULTIPLIER_WHITELIST = List.of(
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.DIAMOND_ORE,
            Material.COPPER_ORE,
            Material.REDSTONE_ORE,
            Material.NETHER_GOLD_ORE,
            Material.NETHER_GOLD_ORE
    );

    private final NamespacedKey MINING_MULTIPIER_COOLDOWN = new NamespacedKey(plugin, "mining_multiplier_cooldown");
    private final NamespacedKey MINING_MULTIPLIER_STORAGE = new NamespacedKey(plugin, "mining_multiplier_storage");

    private final NamespacedKey FORTUNE_ENCHANT_COOLDOWN = new NamespacedKey(plugin, "fortune_cooldown");

    private final NamespacedKey VILLAGERS_BLESSING_COOLDOWN = new NamespacedKey(plugin, "villagers_blessing_cooldown");

    private final NamespacedKey VILLAGER_EGG_PASSIVE_COOLDOWN = new NamespacedKey(plugin, "villager_egg_cooldown");

    public VillagerGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList) {
        super(material, customModelData, name, plugin, abilityList, 3, 0);
    }

    @Override
    public ItemStack createItem(int amount) {
        final ItemStack newItem = super.createItem(amount);

        ItemUtils.storeDataOfType(PersistentDataType.LONG, System.currentTimeMillis(), VILLAGER_EGG_PASSIVE_COOLDOWN, newItem);
        ItemUtils.storeIntegerData(1, MINING_MULTIPLIER_STORAGE, newItem);

        return newItem;
    }

    @Override
    public void dropItemAction(PlayerDropItemEvent event, ItemStack itemUsed) {
        if (getItemStackLevel(itemUsed) != 3)
            return;

        setDropMultiplier(event, itemUsed);
    }

    @Override
    public void blockMineAction(BlockBreakEvent blockBreakEvent, ItemStack itemStack, ItemStack thisItem) {
        if (getItemStackLevel(thisItem) != 3)
            return;

        multiplyOnMine(blockBreakEvent, thisItem, itemStack);
    }

    @Override
    public void rightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        final int itemLevel = getItemStackLevel(itemInteracted);

        if (itemLevel < 2) return;

        grantHeroTen(event, itemInteracted);
    }

    @Override
    public void rightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        super.rightClickBlock(event, itemInteracted);
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
        final int itemLevel = getItemStackLevel(item);

        grantPassiveEffects(player);

        giveVillagerEgg(player, item);

        if (itemLevel < 1)
            return;

        enchantWithFortune(player, item);

        if (itemLevel < 3)
            return;

        grantMaxedEffects(player);
    }

    private void grantPassiveEffects(Player player) {
        player.addPotionEffects(NON_MAXED_GEM_PASSIVE_EFFECTS);
    }

    private void giveVillagerEgg(Player player, ItemStack item) {
        if (ItemUtils.getCooldown(VILLAGER_EGG_PASSIVE_COOLDOWN, item, 21600L))
            return;

        final PlayerInventory inventory = player.getInventory();

        inventory.addItem(VILLAGER_EGG);
    }

    private void enchantWithFortune(Player player, ItemStack thisItem) {
        if (ItemUtils.getCooldown(FORTUNE_ENCHANT_COOLDOWN, thisItem, 900L))
            return;

        final ItemStack item = player.getInventory().getItemInMainHand();

        if (!item.getType().name().toLowerCase().contains("pickaxe"))
            return;

        final Map<Enchantment, Integer> itemCurrentEnchantmentMap = item.getEnchantments();

        if (itemCurrentEnchantmentMap.containsKey(Enchantment.SILK_TOUCH))
            return;

        if (!itemCurrentEnchantmentMap.containsKey(Enchantment.FORTUNE) || itemCurrentEnchantmentMap.get(Enchantment.FORTUNE) < 2)
            item.addEnchantment(Enchantment.FORTUNE, 2);
    }

    private void grantMaxedEffects(Player player) {
        player.addPotionEffects(MAXED_GEM_PASSIVE_EFFECTS);
    }

    private void setDropMultiplier(PlayerDropItemEvent playerDropItemEvent, ItemStack itemUsed) {
        final Player player = playerDropItemEvent.getPlayer();

        if (ItemUtils.getCooldown(MINING_MULTIPIER_COOLDOWN, itemUsed, 900L, player, new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "Used Champion's Grace")))
            return;

        ItemUtils.storeIntegerData(5, MINING_MULTIPLIER_STORAGE, itemUsed);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR)
                    continue;

                if (!itemStack.isSimilar(itemUsed))
                    continue;

                ItemUtils.storeIntegerData(1, MINING_MULTIPLIER_STORAGE, itemStack);
            }
        }, 100L);
    }

    private void grantHeroTen(PlayerInteractEvent event, ItemStack item) {
        final Player player = event.getPlayer();

        if (ItemUtils.getCooldown(VILLAGERS_BLESSING_COOLDOWN, item, 1800L, player, new TextComponent(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Used Villager's Blessing"))) return;

        player.addPotionEffect(HERO_OF_THE_VILLAGE_TEN);
    }

    private void multiplyOnMine(BlockBreakEvent blockBreakEvent, ItemStack thisItem, ItemStack itemUsedToMine) {
        final int multiplier = ItemUtils.getDataOfType(PersistentDataType.INTEGER, MINING_MULTIPLIER_STORAGE, thisItem, 1);

        if (multiplier == 1)
            return;

        final Block block = blockBreakEvent.getBlock();

        if (!VILLAGER_MULTIPLIER_WHITELIST.contains(block.getType()))
            return;

        final World world = block.getWorld();
        final Location blockLocation = block.getLocation();

        for (ItemStack item : block.getDrops(itemUsedToMine)) {
            final ItemStack multipliedItem = item.clone();

            multipliedItem.setAmount(item.getAmount() * multiplier);

            world.dropItem(blockLocation, multipliedItem);
        }

        block.setType(Material.AIR);

        blockBreakEvent.setDropItems(false);
        blockBreakEvent.setCancelled(true);
    }
}