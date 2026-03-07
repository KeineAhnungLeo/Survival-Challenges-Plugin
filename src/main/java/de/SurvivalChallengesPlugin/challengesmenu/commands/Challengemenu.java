package de.SurvivalChallengesPlugin.challengesmenu.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.SurvivalChallengesPlugin.challengesmenu.events.invClick.createMainMenu;

public class Challengemenu implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "SurvivalChallenges" + ChatColor.GRAY + "] " + ChatColor.RED + "This command can only be used by players");
            return false;
        }
        createMainMenu(player);
        return true;
    }
}
