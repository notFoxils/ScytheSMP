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

public class GetItemLevel implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Sender is not an player");
            return true;
        }

        final ItemStack itemStack = player.getInventory().getItemInMainHand();

        final Item item = ItemRegistry.getItemFromItemStack(itemStack);

        if (!(item instanceof UpgradeableItem upgradeableItem)) {
            commandSender.sendMessage(ChatColor.YELLOW + "Held item is not an UpgradeableItem");
            return true;
        }

        commandSender.sendMessage(upgradeableItem.getName() + "'s" + ChatColor.RESET + " Level: " + UpgradeableItem.getItemStackLevel(itemStack));
        return true;
    }
}