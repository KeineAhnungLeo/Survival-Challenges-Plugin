package de.SurvivalChallengesPlugin.general.forcebattles.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OpenResult implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "SurvivalChallenges" + ChatColor.GRAY + "] " + ChatColor.RED + "This command can only be used by players");
            return false;
        }
        if(strings.length == 0){
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Usage: " + ChatColor.GOLD +  "/openresult <player | team>");
            return false;
        }
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if(!forceBattles.isForceBattlesEnabled()){
            commandSender.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + net.md_5.bungee.api.ChatColor.GRAY + "] " + net.md_5.bungee.api.ChatColor.RED + "No Force Battles are active");
            return false;
        }
        String target = strings[0];
        if(forceBattles.isForceBattlesTeams()){
            //teams
        }
        else {
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
                commandSender.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "Player not found");
                return false;
            } else {
                UUID targetPlayer = player.getUniqueId();
                Player commandPlayer = (Player) commandSender;
                if (forceBattles.isForceBattlesCustomItems()) {
                    if (!de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems.openSpecificPlayerResult(commandPlayer, targetPlayer))
                        commandSender.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "No results to show");
                } else {
                    if (!de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.openSpecificPlayerResult(commandPlayer, targetPlayer))
                        commandSender.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "No results to show");
                }
            }
        }
        return true;
    }
}
