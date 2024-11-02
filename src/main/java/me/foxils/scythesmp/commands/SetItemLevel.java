package me.foxils.scythesmp.commands;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.registry.ItemRegistry;
import me.foxils.scythesmp.items.UpgradeableItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SetItemLevel implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String commandName, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Sender is not an player");
            return true;
        }

        final ItemStack itemStack = player.getInventory().getItemInMainHand();

        final Item item = ItemRegistry.getItemFromItemStack(itemStack);

        if (!(item instanceof UpgradeableItem)) {
            commandSender.sendMessage(ChatColor.YELLOW + "Held item is not an ArtifactItem");
            return true;
        }

        final int newLevel;

        try {
            newLevel = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
            commandSender.sendMessage(ChatColor.YELLOW + args[0] + " is not a number");
            return true;
        }

        if (!UpgradeableItem.setItemStackLevel(itemStack, newLevel)) {
            commandSender.sendMessage(ChatColor.DARK_RED + "Could not set the level for item: " + item.getName());
            return true;
        }

        commandSender.sendMessage(ChatColor.GREEN + "Set " + item.getName() + "'s " + ChatColor.RESET + "Level to" + newLevel);
        return true;
    }
}
