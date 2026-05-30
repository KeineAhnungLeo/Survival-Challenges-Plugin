package de.SurvivalChallengesPlugin.general.forcebattles.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems;
import de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal;
import de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.SurvivalChallengesPlugin.general.forcebattles.events.teams.Normal.openSpecificTeamResult;

public class OpenResult implements CommandExecutor, TabCompleter {
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
        ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if(!forceBattles.isForceBattlesEnabled()){
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + net.md_5.bungee.api.ChatColor.GRAY + "] " + net.md_5.bungee.api.ChatColor.RED + "No Force Battles are active");
            return false;
        }
        String target = strings[0];
        if(forceBattles.isForceBattlesTeams()){
            Team team = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamByName(target.toLowerCase());
            if(team == null){
                commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "Team not found");
                return false;
            }
            Player commandPlayer = (Player) commandSender;
            if (forceBattles.isForceBattlesCustomItems()) {
                if (!de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.openSpecificTeamResult(commandPlayer, team))
                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "No results to show");
            } else {
                if (!openSpecificTeamResult(commandPlayer, team))
                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "No results to show");
            }
        }
        else {
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
                commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "Player not found");
                return false;
            } else {
                UUID targetPlayer = player.getUniqueId();
                Player commandPlayer = (Player) commandSender;
                if (forceBattles.isForceBattlesCustomItems()) {
                    if (!CustomItems.openSpecificPlayerResult(commandPlayer, targetPlayer))
                        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "No results to show");
                } else {
                    if (!Normal.openSpecificPlayerResult(commandPlayer, targetPlayer))
                        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "No results to show");
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();
        ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if(command.getName().equalsIgnoreCase("openresult")) {
            if (strings.length == 1) {
                List<String> subcommands = new ArrayList<>(List.of());
                if (forceBattles.isForceBattlesTeams()) {
                    subcommands.add("red");
                    subcommands.add("orange");
                    subcommands.add("yellow");
                    subcommands.add("green");
                    subcommands.add("light_blue");
                    subcommands.add("blue");
                    subcommands.add("purple");
                    subcommands.add("magenta");
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        subcommands.add(player.getName());
                    }
                }
                for (String string : subcommands) {
                    if (string.toLowerCase().startsWith(strings[0].toLowerCase())) {
                        completions.add(string);
                    }
                }
            }
        }
        return completions;
    }
}
