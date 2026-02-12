package de.SurvivalChallengesPlugin.general.position.commands;

import de.SurvivalChallengesPlugin.general.challenges.utils.ChunkSynchronisation.BlockKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.StringReader;
import java.util.*;

public class Position implements CommandExecutor, TabCompleter{
    public static final Map<String, Map<Location, World.Environment>> positions = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length <= 1){
            sendUsage(commandSender);
            return false;
        }
        Player player = (Player) commandSender;
        switch (strings[0].toLowerCase()){
            case ("add"):{
                Location location = player.getWorld().getBlockAt(player.getLocation()).getLocation();
                String name = strings[1].toLowerCase();
                if(positions.containsKey(name.toLowerCase())){
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Position" + ChatColor.GRAY + "] " + ChatColor.RED + name + " already exists");
                    return false;
                }
                Map<Location, World.Environment> innerMap = new HashMap<>();
                innerMap.put(location, player.getWorld().getEnvironment());
                positions.put(name, innerMap);
                String dimension = getDimension(player.getWorld().getEnvironment());
                for(Player player1 : Bukkit.getOnlinePlayers()){
                    player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Position" + ChatColor.GRAY + "] " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " added position " + ChatColor.GOLD + name + ChatColor.GRAY + " [" + ChatColor.GOLD + location.getX() + " " + location.getY() + " " + location.getZ() + ChatColor.GRAY + ", " + ChatColor.GOLD + dimension + ChatColor.GRAY + "]");
                }
                return true;
            }
            case ("remove"):{
                String name = strings[1].toLowerCase();
                if(!positions.containsKey(name)){
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Position" + ChatColor.GRAY + "] " + ChatColor.RED + name + " dosent exists");
                    return false;
                }
                positions.remove(name);
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Position" + ChatColor.GRAY + "] " + ChatColor.RED + "deleted " + name);
                return true;
            }
            case ("get"):{
                String name = strings[1].toLowerCase();
                if(!positions.containsKey(name)){
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Position" + ChatColor.GRAY + "] " + ChatColor.RED + name + " dosent exists");
                    return false;
                }
                Map <Location, World.Environment> innerMap = positions.get(name);
                Location location = innerMap.keySet().iterator().next();
                String dimension = getDimension(innerMap.get(location));
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Position" + ChatColor.GRAY + "] " + ChatColor.GRAY + "Position " + ChatColor.GOLD + strings[1] + ChatColor.GRAY + " [" + ChatColor.GOLD + location.getX() + " " + location.getY() + " " + location.getZ() + ChatColor.GRAY + ", " + ChatColor.GOLD + dimension + ChatColor.GRAY + "]");
                return true;
            }
        }
        return false;
    }

    public void sendUsage(CommandSender commandSender){
        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Position" + ChatColor.GRAY + "] " + ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/position add <name>, /position remove <name>, /position get <name>");
    }

    public String getDimension(World.Environment environment){
        if(environment == World.Environment.NORMAL){
            return "Overworld";
        }
        else if(environment == World.Environment.NETHER){
            return "Nether";
        }
        else if(environment == World.Environment.THE_END){
            return "End";
        }
        return "Custom";
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("position")) {
            if (strings.length == 1) {
                completions.add("add");
                completions.add("remove");
                completions.add("get");
            }
            if(strings.length==2&&(strings[0].equalsIgnoreCase("remove")||strings[0].equalsIgnoreCase("get"))){
                for(Map.Entry<String, Map<Location, World.Environment>> map : positions.entrySet())
                    completions.add(map.getKey());
            }
        }
        return completions;
    }
}
