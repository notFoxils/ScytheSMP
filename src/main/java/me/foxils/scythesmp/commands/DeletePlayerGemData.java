package me.foxils.scythesmp.commands;

import me.foxils.scythesmp.tables.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeletePlayerGemData implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        OfflinePlayer player = Bukkit.getPlayer(strings[0]);
        if (player == null) {
            commandSender.sendMessage(strings[0] + " is not a real player.");
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        PlayerStats playerStats = PlayerStats.getDataObjectFromUUID(playerUUID);

        playerStats.deleteColumn();

        commandSender.sendMessage(ChatColor.RED + "Successfully deleted and regenerated gem data from database.");
        commandSender.sendMessage(ChatColor.GREEN + "Extra info, just in case you need to recover the data.");
        commandSender.sendMessage(strings[0] + " (" + playerUUID + ")");
        commandSender.sendMessage("Current Gem: " + playerStats.getCurrentGem());
        commandSender.sendMessage(playerStats.getGemLevelMapAsXML());

        return true;
    }
}
