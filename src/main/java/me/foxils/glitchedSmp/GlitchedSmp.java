package me.foxils.glitchedSmp;

import me.foxils.foxutils.ItemRegistry;
import me.foxils.glitchedSmp.items.*;
import me.foxils.glitchedSmp.commands.get;
import me.foxils.glitchedSmp.commands.getItemLevel;
import me.foxils.foxutils.utilities.ActionType;
import me.foxils.foxutils.utilities.ItemAbility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private void registerItems() {
        ItemRegistry.registerItem(new GemUpgrade(Material.NETHER_STAR, "&8&l[&d&lGem Upgrade&8&l]", new NamespacedKey(this, "gem_upgrade"),
                Arrays.asList(

                ),
                Arrays.asList(
                        new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.DIAMOND_BLOCK),
                        new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.NETHERITE_INGOT), new ItemStack(Material.GOLD_BLOCK),
                        new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.DIAMOND_BLOCK)
                ), true));
        ItemRegistry.registerItem(new EarthGem(Material.PAPER, 1, "&#63452c&l[&2&lEarth Gem&#63452c&l]", new NamespacedKey(this, "earth_gem"), Arrays.asList(
                        new ItemAbility("Gift Of The Mines", Arrays.asList(
                                "Mine in a radius of 3x3x3.",
                                ChatColor.DARK_GRAY + "(Sneak to Cancel)"
                        ), ActionType.MINE),
                        new ItemAbility("Tumble-Four³", List.of("Expand your passive mining radius to 4x4x4"), ActionType.DROP, 120),
                        new ItemAbility("Terrain-Toss", Arrays.asList(
                                "Inflicts 4 hearts of true damage.",
                                ChatColor.ITALIC + "Big mans throw bigger rock.",
                                ChatColor.ITALIC + "     -- Biggest Man"
                        ), ActionType.RIGHT_CLICK, 180),
                        new ItemAbility("Immovable Object", List.of("Your shield never breaks, like ever."), ActionType.PASSIVE),
                        new ItemAbility("Passive Haste", List.of("Grants haste II permanently"), ActionType.PASSIVE)
        )));
        ItemRegistry.registerItem(new WaterGem(Material.PAPER, 2, "&6&l[&9&lWater Gem&6&l]", new NamespacedKey(this, "water_gem"), Arrays.asList(
                        new ItemAbility("Drowning Arrows", Arrays.asList(
                                "Shoot an arrow that inflicts poison and deals 1 heart of damage.",
                                "You can shoot up to 4 arrows, each having a 1s cooldown.",
                                "Arrows restock every 7 minutes."
                        ), ActionType.LEFT_CLICK, 420),
                        new ItemAbility("Water-Jet", Arrays.asList(
                                "Shoot a jet of water.",
                                "Inflicts 1.5 hearts of true-damage, and 7s Mining Fatigue."
                        ), ActionType.RIGHT_CLICK, 120),
                        new ItemAbility("The Waves", Arrays.asList(
                                "Launches a large water-ball",
                                "Deals 4 hearts of true damage"
                        ), ActionType.SHIFT_RIGHT_CLICK, 180),
                        new ItemAbility("Poseidon's Gifts", Arrays.asList(
                                "Take 50% less damage in the nether",
                                "Permanent Conduit Power and Dolphin's Grace"
                        ), ActionType.PASSIVE)
        )));
        ItemRegistry.registerItem(new AirGem(Material.PAPER, 3, "&b&l[&f&lAir Gem&b&l]", new NamespacedKey(this, "air_gem"), Arrays.asList(
                new ItemAbility("Light Wind", List.of("Send players flying into the air."), ActionType.ATTACK, 120),
                new ItemAbility("Air-Channeling", List.of("Allows you to fly in the air with a timer of 10s."), ActionType.DOUBLEJUMP, 180),
                new ItemAbility("Double-Jump", List.of("Allows you to double jump."), ActionType.SHIFTDOUBLEJUMP, 5),
                new ItemAbility("Push-Pull", Arrays.asList(
                        "When hitting a player gain Speed II.",
                        "Inflicts Slow Falling to the hit player."
                ), ActionType.PASSIVE, 120),
                new ItemAbility("Light As A Feather", List.of("Never take fall damage from any source."), ActionType.PASSIVE)
        )));
        ItemRegistry.registerItem(new VillagerGem(Material.PAPER,4, "&#563C33&l[&#BD8B72&lVillager Gem&#563C33&l]", new NamespacedKey(this, "villager_gem"), Arrays.asList(
                new ItemAbility("Champion’s Grace", List.of("Multiplies all block drops by 5 for 5 seconds."), ActionType.DROP, 180),
                new ItemAbility("Villager’s Blessing", List.of("Grants Hero of the Village 10 for 5 minutes."), ActionType.RIGHT_CLICK, 180),
                new ItemAbility("Fortunate", List.of("Enchants your pickaxe with Fortune II"), ActionType.PASSIVE, 900),
                new ItemAbility("weird", List.of("Provides 2 Villager spawn eggs every half-hour."), ActionType.PASSIVE, 1800),
                new ItemAbility("Villager's Blessings", List.of("Permanent Hero of the Village 2 at max level, Haste 2, and Speed 1"), ActionType.PASSIVE, 0)
        )));
        ItemRegistry.registerItem(new LifeGem(Material.PAPER, 5, "&5&l[&4&lGem Of Life&5&l]", new NamespacedKey(this, "life_gem"), Arrays.asList(
                new ItemAbility("Wither-Away", List.of("Inflict the withering effect on another player."), ActionType.RIGHT_CLICK, 120),
                new ItemAbility("Power-Heal", List.of("Restores up-to 10 hearts for 5 seconds"), ActionType.SHIFT_RIGHT_CLICK, 120),
                new ItemAbility("Life Steal", Arrays.asList(
                        "Steal 4 hearts from another player.",
                        "Hearts are returned after 30s."
                ), ActionType.ATTACK, 180),
                new ItemAbility("Passive Regeneration", List.of("Grants regeneration II for 5s"), ActionType.PASSIVE, 10)
        )));
    }

    private void scheduleTasks() {
        taskIDs.addAll(Arrays.asList(
                Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, EarthGem::customThrowCollision, 1, 1),
                Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, WaterGem::customThrowCollision, 1, 1)
        ));
    }

    public static String hex(String message) {
        final Character COLOR_CHAR = '&';
        final Pattern hexPattern = Pattern.compile("&#" + "([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
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
