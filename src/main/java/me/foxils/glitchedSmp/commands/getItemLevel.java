package me.foxils.glitchedSmp.commands;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.ItemRegistry;
import me.foxils.glitchedSmp.items.UpgradeableItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class getItemLevel implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player) || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            return false;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        Item item = ItemRegistry.getItemFromItemStack(itemStack);

        if (!(item instanceof UpgradeableItem)) {
            return false;
        }

        player.sendMessage(UpgradeableItem.getLevel(itemStack) + "");

        return true;
    }
}