package de.SurvivalChallengesPlugin.general.forcebattles.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NextResult implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings){
        if(!commandSender.isOp()){
            commandSender.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + org.bukkit.ChatColor.GRAY + "] " + org.bukkit.ChatColor.RED + "You have no permission to execute this command");
            return false;
        }
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if(forceBattles.isForceBattlesResults()) {
            if (forceBattles.isForceBattlesTeams()) {
                if(forceBattles.isForceBattlesCustomItems()) {
                    //CItems
                    de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.openNextTeamResult();
                }
                else {
                    //NoCItems
                    de.SurvivalChallengesPlugin.general.forcebattles.events.teams.Normal.openNextTeamResult();
                }
            } else {
                if (forceBattles.isForceBattlesCustomItems())
                    de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems.openNextPlayerResult();
                else
                    de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.openNextPlayerResult();
            }
        }
        else if(!forceBattles.isForceBattlesEnabled())
            commandSender.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "No Force Battles are active");
        else
            commandSender.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "No results to show");
        return true;
    }
}
