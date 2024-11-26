package me.foxils.scythesmp.listeners;

import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.scythesmp.items.SpeedGem;
import me.foxils.scythesmp.utilities.RandomGemStuff;
import me.foxils.scythesmp.tables.PlayerStats;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final Plugin plugin;

    private static final List<NamespacedKey> recipes = Arrays.asList(
            NamespacedKey.fromString("scythesmp:gem_reroll"),
            NamespacedKey.fromString("scythesmp:gem_upgrade")
    );

    public PlayerJoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        rollFirstTimeGem(event);
        resetHealthAmount(event);
        resetMovementSpeedAttribute(event);
        grantPluginRecipes(event);
    }

    private void grantPluginRecipes(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (player.hasDiscoveredRecipe(recipes.getFirst()) && player.hasDiscoveredRecipe(recipes.getLast())) return;

        player.discoverRecipes(recipes);
    }

    private void resetMovementSpeedAttribute(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final AttributeInstance movementSpeedAttribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if (movementSpeedAttribute == null) return;

        movementSpeedAttribute.setBaseValue(SpeedGem.DEFAULT_PLAYER_SPEED_VALUE);
    }

    private void resetHealthAmount(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (maxHealthAttribute == null) return;

        if (maxHealthAttribute.getBaseValue() == 20) return;

        maxHealthAttribute.setBaseValue(20);
    }

    private void rollFirstTimeGem(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final UUID playerUUID = player.getUniqueId();

        //&& !player.getName().equals("Foxils")) return;

        if (PlayerStats.getDataObjectFromUUID(playerUUID) != null) return;

        //if (player.getName().equals("Foxils")) PlayerStats.getDataObjectFromUUID(player.getUniqueId()).deleteColumn();

        final PlayerStats playerStats = new PlayerStats(playerUUID);

        PlayerStats.createColumn(playerStats);

        final RandomGemStuff.ShowRandomUpgradeableItem showRandomUpgradeableItem = new RandomGemStuff.ShowRandomUpgradeableItem(player);
        final RandomGemStuff.GiveRandomUpgradeableItem giveRandomUpgradeableItem = new RandomGemStuff.GiveRandomUpgradeableItem(plugin, player, showRandomUpgradeableItem, ItemRegistry.getItemFromKey(new NamespacedKey(plugin, playerStats.getCurrentGem())).createItem(1));

        showRandomUpgradeableItem.runTaskTimer(plugin, 0L, 2L);
        giveRandomUpgradeableItem.runTaskLater(plugin, 80L);
    }
}
