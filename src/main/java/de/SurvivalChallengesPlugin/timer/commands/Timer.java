package de.SurvivalChallengesPlugin.timer.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class Timer implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0){
            sendUsage(commandSender);
            return false;
        }
        switch (strings[0].toLowerCase()) {
            case "resume": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (timer.isRunning()) {
                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.RED + "Timer is already running");
                    break;
                }
                timer.setRunning(true);
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer resumed");
                    player.sendTitle("",ChatColor.GRAY + "Timer" + ChatColor.GREEN + " resumed", 5, 30, 10);
                }
                de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                if(settings.isSettingSplitHearts())
                    de.SurvivalChallengesPlugin.general.settings.events.Settings.iniSplitPlayerHearts();
                break;
            }
            case "pause": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!timer.isRunning()) {
                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.RED + "Timer is already paused");
                    break;
                }
                timer.setRunning(false);
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer paused");
                }
                break;
            }
            case "toggle": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (timer.isRunning()) {
                    timer.setRunning(false);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer paused");
                    }
                }
                else {
                    timer.setRunning(true);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer resumed");
                    }
                }
                break;
            }
            case "set": {
                if(strings.length == 1){
                    sendUsage(commandSender);
                    return false;
                }
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                try {
                    timer.setTimeS(Integer.parseInt(strings[1]));
                    if (strings.length > 2) timer.setTimeM(Integer.parseInt(strings[2]));
                    if (strings.length > 3) timer.setTimeH(Integer.parseInt(strings[3]));
                    if (strings.length > 4) timer.setTimeD(Integer.parseInt(strings[4]));
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer updated to " + timer.getTimeD() + "d " + timer.getTimeH() + "h " + timer.getTimeM() + "m " + timer.getTimeS() + "s");
                    }
                } catch (NumberFormatException e){
                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.RED + "Invalid numbers");
                }
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                if(challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH))
                    de.SurvivalChallengesPlugin.general.challenges.events.GravitySwitch.setRandomTime(0,0);
                if(challenges.isActive(Challenges.Challenge.TRAFFIC_LIGHT))
                    de.SurvivalChallengesPlugin.general.challenges.events.TrafficLight.setRandomTime(0,0);
                if(challenges.isActive(Challenges.Challenge.PLAYER_BOOST))
                    de.SurvivalChallengesPlugin.general.challenges.events.PlayerBoost.setRandomTime(0,0);
                break;
            }
            case "color": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(strings.length == 1){
                    sendUsage(commandSender);
                    return false;
                }
                switch (strings[1].toLowerCase()) {
                    case "dark_green":
                        timer.setColor(ChatColor.DARK_GREEN);

                        break;
                    case "dark_red":
                        timer.setColor(ChatColor.DARK_RED);

                        break;
                    case "gold":
                        timer.setColor(ChatColor.GOLD);

                        break;
                    case "green":
                        timer.setColor(ChatColor.GREEN);

                        break;
                    case "red":
                        timer.setColor(ChatColor.RED);

                        break;
                    case "yellow":
                        timer.setColor(ChatColor.YELLOW);

                        break;
                    case "blue":
                        timer.setColor(ChatColor.BLUE);

                        break;
                    case "dark_blue":
                        timer.setColor(ChatColor.DARK_BLUE);

                        break;
                    case "dark_purple":
                        timer.setColor(ChatColor.DARK_PURPLE);

                        break;
                    case "purple":
                        timer.setColor(ChatColor.LIGHT_PURPLE);

                        break;
                    case "white":
                        timer.setColor(ChatColor.WHITE);

                        break;
                    case "aqua":
                        timer.setColor(ChatColor.AQUA);

                        break;
                    default:
                        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.RED + "Invalid Color");
                        break;
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                }
                break;
            }
            case "reset": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                timer.setTimeS(0);
                timer.setTimeM(0);
                timer.setTimeH(0);
                timer.setTimeD(0);
                timer.setRunning(false);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer reset");
                }
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                if(challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH))
                    de.SurvivalChallengesPlugin.general.challenges.events.GravitySwitch.setRandomTime(0,0);
                if(challenges.isActive(Challenges.Challenge.TRAFFIC_LIGHT))
                    de.SurvivalChallengesPlugin.general.challenges.events.TrafficLight.setRandomTime(0,0);
                if(challenges.isActive(Challenges.Challenge.PLAYER_BOOST))
                    de.SurvivalChallengesPlugin.general.challenges.events.PlayerBoost.setRandomTime(0,0);
            }
                break;
            case "visible": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!timer.isInvisible()){
                    timer.setInvisible(true);
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer now invisible");
                    }
                    return true;
                }
                else{
                    timer.setInvisible(false);
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer now visible");
                    }
                    return true;
                }
            }
            default:
                sendUsage(commandSender);
                break;
        }
        return true;
    }

    private void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Timer" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Usage: " + ChatColor.GOLD +  "/timer <resume | pause | reset | visible | toggle>, /timer set <Time sec> <Time min> <Time h> <Time d>, /timer color <Color>");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("timer")) {
            if (strings.length == 1) {
                List<String> subcommands = List.of("resume", "pause", "toggle", "set", "color", "reset", "visible");
                for(String string : subcommands){
                    if(string.toLowerCase().startsWith(strings[0].toLowerCase())){
                        completions.add(string);
                    }
                }
            }
            if(strings.length==2&&strings[0].equalsIgnoreCase("color")){
                List<String> subcommands = List.of("dark_green", "dark_red", "gold", "green", "red", "yellow", "blue", "dark_blue", "dark_purple", "purple", "white", "aqua");
                for(String string : subcommands){
                    if(string.toLowerCase().startsWith(strings[1].toLowerCase())){
                        completions.add(string);
                    }
                }
            }
        }
        return completions;
    }
}
