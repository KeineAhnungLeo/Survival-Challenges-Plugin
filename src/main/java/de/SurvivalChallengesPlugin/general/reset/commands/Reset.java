package de.SurvivalChallengesPlugin.general.reset.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.backpack.commands.Backpack;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems;
import de.SurvivalChallengesPlugin.general.forcebattles.events.teams.Normal;
import de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles;
import de.SurvivalChallengesPlugin.general.forcebattles.utils.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.SurvivalChallengesPlugin.challengesmenu.events.invClick.closeForceBattleResultUi;

public class Reset implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!commandSender.isOp()){
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Reset" + ChatColor.GRAY + "] " + ChatColor.RED + "You have no permission to execute this command");
            return false;
        }

        if (strings.length == 0) {
            sendUsage(commandSender);
            return false;
        }
        switch (strings[0].toLowerCase()) {
            case "confirm": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                timer.setRunning(false);
                timer.setTimeS(0);
                timer.setTimeM(0);
                timer.setTimeH(0);
                timer.setTimeD(0);
                timer.setColor(ChatColor.GOLD);
                challenges.removeAllChallenges();
                settings.resetDefault();
                closeForceBattleResultUi();
                forceBattles.resetDefault();
                de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.tasksPlayers.clear();
                de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.doneTasksPlayers.clear();
                CustomItems.taskIdPlayers.clear();
                CustomItems.tasksPlayers.clear();
                de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems.doneTasksPlayers.clear();
                de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems.tasksDonePlayers.clear();

                Normal.tasksTeams.clear();
                Normal.doneTasksTeams.clear();
                de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.tasksIdTeams.clear();
                de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.tasksTeams.clear();
                de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.doneTasksTeams.clear();
                de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.tasksDoneTeam.clear();
                forceBattles.setForceBattlesEnabled(false);
                Team.deleteAllTeams();
                SurvivalChallengesPlugin.getInstance().getBackpackCommand().clearAll();
                for (Player player : Bukkit.getOnlinePlayers())
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Reset" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Everything has been reset");
                return true;
            }
            case "challenges": {
                if (strings.length == 1) {
                    sendUsage(commandSender);
                    return false;
                }
                if (strings[1].equalsIgnoreCase("confirm")) {
                    Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                    challenges.removeAllChallenges();
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Reset" + ChatColor.GRAY + "] " + ChatColor.GREEN + "All challenges has been reset");
                    return true;
                }
                sendUsage(commandSender);
                return false;
            }
            case "settings": {
                if (strings.length == 1) {
                    sendUsage(commandSender);
                    return false;
                }
                if (strings[1].equalsIgnoreCase("confirm")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.resetDefault();
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Reset" + ChatColor.GRAY + "] " + ChatColor.GREEN + "All settings has been reset");
                    return true;
                }
                sendUsage(commandSender);
                return false;
            }
            case "backpacks": {
                if (strings.length == 1) {
                    sendUsage(commandSender);
                    return false;
                }
                if (strings[1].equalsIgnoreCase("confirm")) {
                    SurvivalChallengesPlugin.getInstance().getBackpackCommand().clearAll();
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Reset" + ChatColor.GRAY + "] " + ChatColor.GREEN + "All backpacks has been reset");
                    return true;
                }
                sendUsage(commandSender);
                return false;
            }
            case "forcebattles": {
                if (strings.length == 1) {
                    sendUsage(commandSender);
                    return false;
                }
                if (strings[1].equalsIgnoreCase("confirm")) {
                    closeForceBattleResultUi();
                    ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                    forceBattles.resetDefault();
                    de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.tasksPlayers.clear();
                    de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.doneTasksPlayers.clear();
                    CustomItems.taskIdPlayers.clear();
                    CustomItems.tasksPlayers.clear();
                    de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems.doneTasksPlayers.clear();
                    de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems.tasksDonePlayers.clear();

                    Normal.tasksTeams.clear();
                    Normal.doneTasksTeams.clear();
                    de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.tasksIdTeams.clear();
                    de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.tasksTeams.clear();
                    de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.doneTasksTeams.clear();
                    de.SurvivalChallengesPlugin.general.forcebattles.events.teams.CustomItems.tasksDoneTeam.clear();
                    forceBattles.setForceBattlesEnabled(false);
                    Team.deleteAllTeams();
                    SurvivalChallengesPlugin.getInstance().getBackpackCommand().clearTeams();
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Reset" + ChatColor.GRAY + "] " + ChatColor.GREEN + "All Force Battles settings has been reset");
                    return true;
                }
                sendUsage(commandSender);
                return false;
            }
            default:
                sendUsage(commandSender);
                break;
        }
        return false;
    }

    private void sendUsage(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Reset" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Usage: " + ChatColor.GOLD + "/reset confirm, /reset <challenges | settings | backpacks | forcebattles> confirm");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("reset")) {
            if (strings.length == 1) {
                List<String> subcommands = List.of("confirm", "settings", "challenges", "backpacks", "forcebattles");
                for(String string : subcommands){
                    if(string.toLowerCase().startsWith(strings[0].toLowerCase())){
                        completions.add(string);
                    }
                }
            }
            if(strings.length==2&&(strings[0].equalsIgnoreCase("settings")||strings[0].equalsIgnoreCase("challenges")||strings[0].equalsIgnoreCase("backpacks")||strings[0].equalsIgnoreCase("forcebattles"))) {
                String string = "confirm";
                if (string.toLowerCase().startsWith(strings[1].toLowerCase())) {
                    completions.add(string);
                }
            }
        }
        return completions;
    }
}

