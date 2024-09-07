package me.foxils.glitchedSmp.commands;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.ItemRegistry;
import me.foxils.glitchedSmp.GlitchedSmp;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class get implements CommandExecutor {

    private final Plugin plugin = GlitchedSmp.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 2 && !(commandSender instanceof Player)) {
            commandSender.sendMessage("No player provided to give item");
            return false;
        }

        Player givePlayer;

        if (strings.length == 1) {
            givePlayer = (Player) commandSender;
        } else {
            givePlayer = commandSender.getServer().getPlayer(strings[1]);
            assert givePlayer != null;
        }

        PlayerInventory playerInventory = givePlayer.getInventory();

        NamespacedKey itemKey = new NamespacedKey(plugin, strings[0]);

        Item itemToCreate = ItemRegistry.getItemFromKey(itemKey);

        if (itemToCreate == null) {
            commandSender.sendMessage(strings[0] + " is not a valid item!");
            return false;
        }

        int amountToGive = 1;

        if (strings.length == 3) {
            amountToGive = Integer.parseInt(strings[2]);
        }

        playerInventory.addItem(itemToCreate.createItem(amountToGive));

        return false;
    }
}
