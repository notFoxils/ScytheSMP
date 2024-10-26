package me.foxils.scythesmp.utilities;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.scythesmp.items.UpgradeableItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
        private final ItemStack item;
        private final String randomGemName;
        private String secondaryText = null;

        public GiveRandomUpgradeableItem(Plugin plugin, Player player, BukkitRunnable taskToCancel, ItemStack itemToGive) {
            this.plugin = plugin;
            this.player = player;
            this.taskToCancel = taskToCancel;
            this.item = itemToGive;
            this.randomGemName = Objects.requireNonNull(itemToGive.getItemMeta()).getItemName();
        }

        public GiveRandomUpgradeableItem(Plugin plugin, Player player, BukkitRunnable taskToCancel, ItemStack itemToGive, String secondaryText) {
            this(plugin, player, taskToCancel, itemToGive);
            this.secondaryText = secondaryText;
        }

        @Override
        public void run() {
            taskToCancel.cancel();

            player.getInventory().addItem(item);

            player.sendTitle(randomGemName, secondaryText, 5, 40, 5);

            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1F, 0.9F);

            final Collection<? extends Player> otherPlayers = new ArrayList<>(player.getServer().getOnlinePlayers());

            otherPlayers.removeIf(otherPlayer -> otherPlayer.getUniqueId() == player.getUniqueId());

            otherPlayers.forEach(otherPlayer -> {
                BaseComponent notificationComponent = TextComponent.fromLegacy(getPrimaryColorOfGemName(randomGemName) + ChatColor.ITALIC + player.getName() + ChatColor.RESET + " has received the " + randomGemName);
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
        return getRandomUpgradeableGem().getRawName();
    }

    public static String getRandomGemName() {
        return getRandomUpgradeableGem().getName();
    }

    public static UpgradeableItem getRandomUpgradeableGem() {
        final Collection<Item> registeredGems = ItemRegistry.getRegisteredItems();

        registeredGems.removeIf(item -> !(item instanceof UpgradeableItem));

        return (UpgradeableItem) randomFromCollection(registeredGems);
    }

    public static String getPrimaryColorOfGemName(String gemName) {
        StringBuilder stringBuilder = new StringBuilder(gemName);

        if (stringBuilder.chars().filter(num -> num == '§').count() > 6) {
            // This is for names that use hex color codes instead of regular color codes
            stringBuilder.delete(0, 17);
        } else if (stringBuilder.chars().filter(num -> num == '§').count() <= 6){
            stringBuilder.delete(0, 5);
        }

        if (stringBuilder.chars().filter(num -> num == '§').count() > 15) {
            stringBuilder.delete(16, stringBuilder.length());
        } else if (stringBuilder.chars().filter(num -> num == '§').count() < 15) {
            stringBuilder.delete(4, stringBuilder.length());
        }

        return stringBuilder.toString();
    }

    private static <T> T randomFromCollection(Collection<T> coll) {
        int num = (int) (Math.random() * coll.size());
        for(T t: coll) if (--num < 0) return t;
        throw new AssertionError();
    }
}
