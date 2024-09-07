package me.foxils.glitchedSmp;

import me.foxils.foxutils.ItemRegistry;
import me.foxils.glitchedSmp.items.*;
import me.foxils.glitchedSmp.commands.get;
import me.foxils.glitchedSmp.commands.getItemLevel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GlitchedSmp extends JavaPlugin {

    public static final List<Integer> taskIDs = new ArrayList<>();

    private static Plugin instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;


        scheduleTasks();
        registerItems();
        registerCommands();
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("get").setExecutor(new get());
        Bukkit.getPluginCommand("getLevel").setExecutor(new getItemLevel());
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void registerItems() {
        ItemRegistry.registerItem(new GemUpgrade(Material.NETHER_STAR, "gemupgrade", new NamespacedKey(this, "gem_upgrade"),
                Arrays.asList(
                        new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.DIAMOND_BLOCK),
                        new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.NETHERITE_INGOT), new ItemStack(Material.GOLD_BLOCK),
                        new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.DIAMOND_BLOCK)
                ), true));
        ItemRegistry.registerItem(new EarthGem(Material.STICK, "earthgem", new NamespacedKey(this, "earth_gem"),
                Arrays.asList(
                        new ItemStack(Material.NETHERITE_SWORD), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.NETHERITE_SWORD),
                        new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT),
                        new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT)
                ), true));
        ItemRegistry.registerItem(new WaterGem(Material.STICK, "watergem", new NamespacedKey(this, "water_gem"),
                Arrays.asList(
                        new ItemStack(Material.NETHERITE_SWORD), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DIAMOND),
                        new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT),
                        new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT)
                ), true));
        ItemRegistry.registerItem(new AirGem(Material.STICK, "airgem", new NamespacedKey(this, "air_gem"),
                Arrays.asList(
                        new ItemStack(Material.DIAMOND), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DIAMOND),
                        new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT),
                        new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT)
                ), true));
        ItemRegistry.registerItem(new VillagerGem(Material.STICK, "villagergem", new NamespacedKey(this, "villager_gem"),
                Arrays.asList(
                        new ItemStack(Material.RED_BANNER), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DIAMOND),
                        new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT),
                        new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT), new ItemStack(Material.DARK_OAK_BOAT)
                ), true));
    }

    private void scheduleTasks() {
        taskIDs.addAll(Arrays.asList(
                Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, EarthGem::customThrowCollision, 1, 1),
                Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, WaterGem::customThrowCollision, 1, 1)
        ));
    }

    @Override
    public void onDisable() {
        cancelTasks();
    }

    private void cancelTasks() {
        for (int taskID : taskIDs) {
            cancelTask(taskID);
        }
    }

    public static void cancelTask(int taskID) {
        taskIDs.remove(taskID);
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public static Plugin getInstance() {
        return instance;
    }

}
