package me.foxils.sytheSMP.commands;

import me.foxils.foxutils.ItemRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class getItems implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        ItemRegistry.getRegisteredGems().forEach(item -> commandSender.sendMessage(item.getName()));
        return true;
    }
}
