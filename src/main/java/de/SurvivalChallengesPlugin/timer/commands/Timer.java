package de.SurvivalChallengesPlugin.timer.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

import static de.SurvivalChallengesPlugin.general.challenges.events.GravitySwitch.setRandomMin;
import static de.SurvivalChallengesPlugin.general.challenges.events.GravitySwitch.setRandomSec;

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
                    commandSender.sendMessage(ChatColor.RED + "Timer is already running");
                    break;
                }
                timer.setRunning(true);
                commandSender.sendMessage(ChatColor.GREEN + "Timer resumed");
                SurvivalChallengesPlugin.getInstance().getSettings().syncAllPlayersHealth();
                break;
            }
            case "pause": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!timer.isRunning()) {
                    commandSender.sendMessage(ChatColor.RED + "Timer is already paused");
                    break;
                }
                timer.setRunning(false);
                commandSender.sendMessage(ChatColor.GREEN + "Timer paused");
                break;
            }
            case "set": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                try {
                    if (strings.length > 1) timer.setTimeS(Integer.parseInt(strings[1]));
                    if (strings.length > 2) timer.setTimeM(Integer.parseInt(strings[2]));
                    if (strings.length > 3) timer.setTimeH(Integer.parseInt(strings[3]));
                    if (strings.length > 4) timer.setTimeD(Integer.parseInt(strings[4]));

                    commandSender.sendMessage(ChatColor.GREEN + "Timer updated to " +
                    timer.getTimeD() + "d " +
                    timer.getTimeH() + "h " +
                    timer.getTimeM() + "m " +
                    timer.getTimeS() + "s");
                } catch (NumberFormatException e){
                    commandSender.sendMessage(ChatColor.RED + "Invalid numbers");
                }
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                if(challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH)) {
                    setRandomMin(0);
                    setRandomSec(0);
                }
                break;
            }
            case "color": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                switch (strings[1].toLowerCase()) {
                    case "dark_green":
                        timer.setColor(ChatColor.DARK_GREEN);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "dark_red":
                        timer.setColor(ChatColor.DARK_RED);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "gold":
                        timer.setColor(ChatColor.GOLD);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "green":
                        timer.setColor(ChatColor.GREEN);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "red":
                        timer.setColor(ChatColor.RED);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "yellow":
                        timer.setColor(ChatColor.YELLOW);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "blue":
                        timer.setColor(ChatColor.BLUE);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "dark_blue":
                        timer.setColor(ChatColor.DARK_BLUE);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "dark_purple":
                        timer.setColor(ChatColor.DARK_PURPLE);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "purple":
                        timer.setColor(ChatColor.LIGHT_PURPLE);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "white":
                        timer.setColor(ChatColor.WHITE);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    case "aqua":
                        timer.setColor(ChatColor.AQUA);
                        commandSender.sendMessage(ChatColor.GREEN + "Color updated to " + strings[1].toLowerCase());
                        break;
                    default:
                        commandSender.sendMessage(ChatColor.RED + "Invalid Color");
                        break;
                }
                break;
            }
            case "reset":{
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                timer.setTimeS(0);
                timer.setTimeM(0);
                timer.setTimeH(0);
                timer.setTimeD(0);
                timer.setRunning(false);
                commandSender.sendMessage(ChatColor.GREEN + "Timer reset");
            }
                break;
            case "visible": {
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (timer.isVisible()){
                    timer.setVisible(false);
                    commandSender.sendMessage(ChatColor.GREEN + "Timer now invisible");
                    return true;
                }
                else{
                    timer.setVisible(true);
                    commandSender.sendMessage(ChatColor.GREEN + "Timer now visible");
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
        sender.sendMessage(ChatColor.GRAY + "Usage: " + ChatColor.GOLD +  "/timer resume, /timer pause, /timer set <Time sec> <Time min> <Time h> <Time d>, /timer color <Color>, /timer reset, /timer visible <Boolean>");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("timer")) {
            if (strings.length == 1) {
                completions.add("resume");
                completions.add("pause");
                completions.add("set");
                completions.add("color");
                completions.add("reset");
                completions.add("visible");
            }
            if(strings.length==2&&strings[0].equalsIgnoreCase("color")){
                completions.add("dark_green");
                completions.add("dark_red");
                completions.add("gold");
                completions.add("green");
                completions.add("red");
                completions.add("yellow");
                completions.add("blue");
                completions.add("dark_blue");
                completions.add("dark_purple");
                completions.add("purple");
                completions.add("white");
                completions.add("aqua");
            }
        }
        return completions;
    }
}
