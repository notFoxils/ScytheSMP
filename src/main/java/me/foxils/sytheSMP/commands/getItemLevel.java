package me.foxils.sytheSMP.commands;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.ItemRegistry;
import me.foxils.sytheSMP.items.UpgradeableItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class getItemLevel implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(commandSender instanceof Player player) || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            return false;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        Item item = ItemRegistry.getItemFromItemStack(itemStack);

        if (!(item instanceof UpgradeableItem upgradeableItem)) {
            return false;
        }

        player.sendMessage(upgradeableItem.getLevel(itemStack) + "");

        return true;
    }
}