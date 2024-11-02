package me.foxils.scythesmp;

import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.scythesmp.commands.DeletePlayerGemData;
import me.foxils.scythesmp.commands.SetItemLevel;
import me.foxils.scythesmp.databases.Database;
import me.foxils.scythesmp.items.*;
import me.foxils.scythesmp.commands.GetItemLevel;
import me.foxils.foxutils.utilities.ActionType;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.scythesmp.listeners.PlayerDeathListener;
import me.foxils.scythesmp.listeners.PlayerInteractEntityListener;
import me.foxils.scythesmp.listeners.PlayerJoinListener;
import me.foxils.scythesmp.listeners.PlayerRespawnListener;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ScytheSMP extends JavaPlugin {

    public static final List<Integer> taskIDs = new ArrayList<>();

    private static Database database;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        database = new Database(this);
        database.initializeDatabase();

        scheduleTasks();
        registerItems();
        registerCommands();
        registerListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(Bukkit.getPluginCommand("getlevel")).setExecutor(new GetItemLevel());
        Objects.requireNonNull(Bukkit.getPluginCommand("setlevel")).setExecutor(new SetItemLevel());
        Objects.requireNonNull(Bukkit.getPluginCommand("deletegemdata")).setExecutor(new DeletePlayerGemData());
    }

    private void registerItems() {
        ItemRegistry.registerItem(new GemUpgrade(Material.NETHER_STAR, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Gem Upgrade" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]", this,
                List.of(
                        new ItemAbility("Upgrade-ify", Arrays.asList(
                                "Upgrades your current Gem by 1 level.",
                                "If stacked the whole stack is used at once."
                        ), ActionType.RIGHT_CLICK)
                ),
                Arrays.asList(
                        new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.DIAMOND_BLOCK),
                        new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.AMETHYST_SHARD), new ItemStack(Material.GOLD_INGOT),
                        new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.DIAMOND_BLOCK)
                ), true));
        ItemRegistry.registerItem(new RerollItem(Material.NETHER_STAR, 1, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + ChatColor.BOLD + "Gem Reroll" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]", this,
                List.of(
                        new ItemAbility("Gem Forge", Arrays.asList(
                                "When crafted this item generates a",
                                "randomly selected Gem. And automatically",
                                "consumes the previous gem that was in your inventory.",
                                "This also lowers Gem's level by 1."
                        ), ActionType.CRAFT)
                ),
                Arrays.asList(
                        new ItemStack(Material.DIAMOND), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.DIAMOND),
                        new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.AMETHYST_SHARD), new ItemStack(Material.GOLD_INGOT),
                        new ItemStack(Material.DIAMOND), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.DIAMOND)
                ), true));
        ItemRegistry.registerItem(new PowerGem(Material.PAPER, 8, ChatColor.of("#292138") + "" + ChatColor.MAGIC + ChatColor.BOLD + "[" + ChatColor.of("#292138") + ChatColor.BOLD + "[G" + ChatColor.of("#2e233f") + ChatColor.BOLD + "e" + ChatColor.of("#332546") + ChatColor.BOLD + "m " + ChatColor.of("#38264c") + ChatColor.BOLD + "O" + ChatColor.of("#3d2853") + ChatColor.BOLD + "f " + ChatColor.of("#432a5a") + ChatColor.BOLD + "P" + ChatColor.of("#482c61") + ChatColor.BOLD + "o" + ChatColor.of("#4d2e68") + ChatColor.BOLD + "w" + ChatColor.of("#52306f") + ChatColor.BOLD + "e" + ChatColor.of("#573175") + ChatColor.BOLD + "r]" + ChatColor.MAGIC + ChatColor.BOLD + "]", this,
                List.of(
                        new ItemAbility("test", List.of("testing"), ActionType.NONE)
                ), Arrays.asList(
                        new ItemStack(Material.NETHERITE_BLOCK), new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.NETHERITE_BLOCK),
                        new ItemStack(Material.END_CRYSTAL), new ItemStack(Material.DRAGON_EGG), new ItemStack(Material.END_CRYSTAL),
                        new ItemStack(Material.NETHERITE_BLOCK), new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.NETHERITE_BLOCK)
                ), true));
        ItemRegistry.registerItem(new EarthGem(Material.PAPER, 1,  ChatColor.of("#63452c") + "" + ChatColor.BOLD + "[" + ChatColor.DARK_GREEN + ChatColor.BOLD + "Earth Gem" + ChatColor.of("#63452c") + ChatColor.BOLD + "]", this,
                Arrays.asList(
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
        ItemRegistry.registerItem(new WaterGem(Material.PAPER, 2,  ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.BLUE + ChatColor.BOLD + "Water Gem" + ChatColor.GOLD + ChatColor.BOLD + "]", this,
                Arrays.asList(
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
        ItemRegistry.registerItem(new AirGem(Material.PAPER, 3,  ChatColor.AQUA + "" + ChatColor.BOLD + "[" + ChatColor.WHITE + ChatColor.BOLD + "Air Gem" + ChatColor.AQUA + ChatColor.BOLD + "]", this,
                Arrays.asList(
                        new ItemAbility("Light Wind", List.of("Send players flying into the air."), ActionType.ATTACK, 120),
                        new ItemAbility("Air-Channeling", List.of("Allows you to fly in the air with a timer of 10s."), ActionType.DOUBLE_JUMP, 180),
                        new ItemAbility("Double-Jump", List.of("Allows you to double jump."), ActionType.SHIFT_DOUBLE_JUMP, 5),
                        new ItemAbility("Push-Pull", Arrays.asList(
                                "When hitting a player gain Speed II.",
                                "Inflicts Slow Falling to the hit player."
                        ), ActionType.PASSIVE, 120),
                        new ItemAbility("Light As A Feather", List.of("Never take fall damage from any source."), ActionType.PASSIVE)
        )));
        ItemRegistry.registerItem(new VillagerGem(Material.PAPER,4,  ChatColor.of("#563C33") + "" + ChatColor.BOLD + "[" + ChatColor.of("#BD8B72") + ChatColor.BOLD + "Villager Gem" + ChatColor.of("#563C33") + ChatColor.BOLD + "]", this,
                Arrays.asList(
                        new ItemAbility("Champion’s Grace", List.of("Multiplies all block drops by 5 for 5 seconds."), ActionType.DROP, 180),
                        new ItemAbility("Villager’s Blessing", List.of("Grants Hero of the Village 10 for 5 minutes."), ActionType.RIGHT_CLICK, 180),
                        new ItemAbility("Fortunate", List.of("Enchants your pickaxe with Fortune II"), ActionType.PASSIVE, 900),
                        new ItemAbility("weird", List.of("Provides 2 Villager spawn eggs every half-hour."), ActionType.PASSIVE, 1800),
                        new ItemAbility("Villager's Blessings", List.of("Permanent Hero of the Village 2 at max level, Haste 2, and Speed 1"), ActionType.PASSIVE, 0)
        )));
        ItemRegistry.registerItem(new LifeGem(Material.PAPER, 5, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[" + ChatColor.DARK_RED + ChatColor.BOLD + "Gem Of Life" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "]", this,
                Arrays.asList(
                        new ItemAbility("Wither-Away", List.of("Inflict the withering effect on another player."), ActionType.RIGHT_CLICK, 120),
                        new ItemAbility("Power-Heal", List.of("Restores up-to 10 hearts for 5 seconds"), ActionType.SHIFT_RIGHT_CLICK, 120),
                        new ItemAbility("Life Steal", Arrays.asList(
                                "Steal 4 hearts from another player.",
                                "Hearts are returned after 30s."
                        ), ActionType.ATTACK, 180),
                        new ItemAbility("Passive Regeneration", List.of("Grants regeneration II for 5s"), ActionType.PASSIVE, 10)
        )));
        ItemRegistry.registerItem(new SpeedGem(Material.PAPER, 6,  ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "[" + ChatColor.BLUE + ChatColor.BOLD + "Speed Gem" + ChatColor.DARK_BLUE + ChatColor.BOLD + "]", this,
                Arrays.asList(
                        new ItemAbility("Static-Wrath", Arrays.asList(
                                "Build up static with your speed powers and",
                                "zap all players within a 10 block radius.",
                                "All players hit will be stunned for 4s."
                                ), ActionType.SHIFT_DROP, 360),
                        new ItemAbility("Solo-Strike", Arrays.asList(
                                "Has a 10% chance to strike your next enemy with lightning.",
                                "Grants Speed V for 10s."
                        ), ActionType.SHIFT_RIGHT_CLICK, 300),
                        new ItemAbility("Fast-Hands", List.of("Grants Haste IV for 10s"), ActionType.SHIFT_LEFT_CLICK, 300),
                        new ItemAbility("Passive Effects", Arrays.asList(
                                "Grants Permanent Speed and Haste.",
                                "Effect level is based on Gem level."
                        ), ActionType.PASSIVE)
        )));
        ItemRegistry.registerItem(new StrengthGem(Material.PAPER, 7,  ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.RED + ChatColor.BOLD + "Strength Gem" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]", this,
                Arrays.asList(
                        new ItemAbility("Gloomy Aura", Arrays.asList(
                                "Weakens other players within a",
                                "5-block radius with Weakness I"
                        ), ActionType.RIGHT_CLICK, 300),
                        new ItemAbility("Strength Of The Abyss", Arrays.asList(
                                "Summons powerful tentacles on the ground.",
                                "Deals 3 damage when other players are hit"
                        ), ActionType.DROP, 120),
                        new ItemAbility("Super Strength", Arrays.asList(
                                "Use all your might and change your passive",
                                "to a Strength III buff for 20s."
                        ), ActionType.SHIFT_RIGHT_CLICK, 900),
                        new ItemAbility("Buff, Buff", List.of("Grants Strength II permanently"), ActionType.PASSIVE),
                        new ItemAbility("Auto Sharpness", Arrays.asList(
                                "Automatically enchants any Sharpness-capable",
                                "weapon in your main-hand with Sharpness V"
                        ), ActionType.PASSIVE, 30)
                )));
    }

    private void scheduleTasks() {
        taskIDs.addAll(Arrays.asList(
                Bukkit.getScheduler().scheduleSyncRepeatingTask(this, EarthGem::customThrowCollision, 1, 1),
                Bukkit.getScheduler().scheduleSyncRepeatingTask(this, WaterGem::customThrowCollision, 1, 1)
        ));
    }

    @Override
    public void onDisable() {
        cancelTasks();
    }

    private void cancelTasks() {
        for (int taskID : taskIDs) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public static Database getDatabase() {
        return database;
    }

}
