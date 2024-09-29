package me.foxils.glitchedSmp.helpers;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.ItemRegistry;
import me.foxils.glitchedSmp.items.UpgradeableItem;
import me.foxils.glitchedSmp.tables.PlayerStats;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class RandomGemStuff {

    private static final Random random = new Random();

    public final static class ShowRandomUpgradeableItem extends BukkitRunnable {

        private final Player player;

        public ShowRandomUpgradeableItem(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            player.sendTitle(RandomGemStuff.getRandomGemName(), "", 0, 3, 0);
            player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 0.8F, 0.75F + random.nextFloat() / 2.0F);
        }
    }

    public final static class GiveRandomUpgradeableItem extends BukkitRunnable {

        private final Plugin plugin;
        private final Player player;
        private final BukkitRunnable taskToCancel;

        public GiveRandomUpgradeableItem(Plugin plugin, Player player, BukkitRunnable taskToCancel) {
            this.plugin = plugin;
            this.player = player;
            this.taskToCancel = taskToCancel;
        }

        @Override
        public void run() {
            taskToCancel.cancel();
            Item randomGem = ItemRegistry.getItemFromKey(new NamespacedKey(plugin, PlayerStats.getDataObjectFromUUID(player.getUniqueId()).getCurrentGem()));

            player.getInventory().addItem(randomGem.createItem(1));

            player.sendTitle(randomGem.getName(), null, 5, 40, 5);

            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1F, 0.9F);

            Collection<? extends Player> otherPlayers = new ArrayList<>(player.getServer().getOnlinePlayers());

            otherPlayers.removeIf(otherPlayer -> otherPlayer.getUniqueId() == player.getUniqueId());

            otherPlayers.forEach(otherPlayer -> {
                TextComponent notificationComponent = new TextComponent(ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16))) + "" + ChatColor.ITALIC + ChatColor.BOLD + player.getName() + ChatColor.RESET + " has received the " + randomGem.getName());
                otherPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, notificationComponent);
                Bukkit.getScheduler().runTaskLater(plugin, () -> otherPlayer.playSound(otherPlayer, Sound.BLOCK_NOTE_BLOCK_BELL, 1.2F, 1.2F), 0L);
                Bukkit.getScheduler().runTaskLater(plugin, () -> otherPlayer.playSound(otherPlayer, Sound.BLOCK_NOTE_BLOCK_BELL, 1.2F, 1F), 4L);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    otherPlayer.playSound(otherPlayer, Sound.BLOCK_NOTE_BLOCK_BELL, 1.2F, 0.8F);
                    otherPlayer.playSound(otherPlayer, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.4F, 0.8F);
                    otherPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, notificationComponent);
                }, 8L);
            });
        }
    }

    public static String getRandomRawGemName() {
        return getRandomGem().getRawName();
    }

    public static String getRandomGemName() {
        return getRandomGem().getName();
    }

    public static Item getRandomGem() {
        Collection<Item> registeredGems = ItemRegistry.getRegisteredGems();

        registeredGems.removeIf(item -> !(item instanceof UpgradeableItem) || item.getRawName().contains("power"));

        return randomFromCollection(registeredGems);
    }

    private static <T> T randomFromCollection(Collection<T> coll) {
        int num = (int) (Math.random() * coll.size());
        for(T t: coll) if (--num < 0) return t;
        throw new AssertionError();
    }
}
