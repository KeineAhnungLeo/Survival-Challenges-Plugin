package de.SurvivalChallengesPlugin.general.invsee.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Invsee implements CommandExecutor, TabCompleter {
    private static final Map<UUID, UUID> openInv = new HashMap<>();
    private static BukkitRunnable task;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "SurvivalChallenges" + ChatColor.GRAY + "] " + ChatColor.RED + "This command can only be used by players");
            return false;
        }
        if(strings.length == 0){
            sendUsage(player);
            return false;
        }
        Player targetPlayer = Bukkit.getPlayer(strings[0]);
        if (targetPlayer == null){
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Invsee" + ChatColor.GRAY + "] " +ChatColor.RED + "Player not found");
            return false;
        }
        Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.GOLD + targetPlayer.getName() + "'s Inventory");
        player.openInventory(inventory);
        openInv.put(player.getUniqueId(), targetPlayer.getUniqueId());
        updateInv(SurvivalChallengesPlugin.getInstance());
        return true;
    }

    private static void sendUsage(Player player){
        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Invsee" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Usage: " + ChatColor.GOLD + "/invsee <Player>");
    }

    public static void updateInv(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if(openInv.isEmpty()){
                    task.cancel();
                    task = null;
                    return;
                }
                for(Map.Entry<UUID, UUID> map : openInv.entrySet()){
                    if(Bukkit.getPlayer(map.getKey()) == null || Bukkit.getPlayer(map.getValue()) == null) {
                    openInv.remove(map.getKey());
                    continue;
                        }
                    Player targetPlayer = Bukkit.getPlayer(map.getValue());
                    if(targetPlayer == null) continue;
                    boolean isUsed = false;
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if(player.getOpenInventory().getTitle().contains(targetPlayer.getName() + "'s Inventory")){
                            isUsed = true;
                            for (int i = 0; i < 36; i++) {
                                ItemStack itemStack = targetPlayer.getInventory().getItem(i);
                                int targetSlot;
                                if(i <= 8){
                                    targetSlot = i+27;
                                }
                                else
                                    targetSlot = i-9;
                                if(itemStack == null)
                                    player.getOpenInventory().getTopInventory().setItem(targetSlot, new ItemStack(Material.AIR));
                                else
                                    player.getOpenInventory().getTopInventory().setItem(targetSlot, new ItemStack(itemStack));
                            }
                        }
                    }
                    if(!isUsed)
                        openInv.remove(map.getKey());
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 3);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("invsee")) {
            if (strings.length == 1) {
                List<String> subcommands = new ArrayList<>(List.of());
                for(Player player : Bukkit.getOnlinePlayers()){
                    subcommands.add(player.getName());
                }
                for(String string : subcommands){
                    if(string.toLowerCase().startsWith(strings[0].toLowerCase())){
                        completions.add(string);
                    }
                }
            }
        }
        return completions;
    }
}
