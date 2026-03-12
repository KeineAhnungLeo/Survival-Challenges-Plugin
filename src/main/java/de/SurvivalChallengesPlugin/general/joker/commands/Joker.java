package de.SurvivalChallengesPlugin.general.joker.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Joker implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            sendUsage(commandSender);
            return false;
        }
        switch (strings[0].toLowerCase()) {
            case "give": {
                if(strings.length >= 3) {
                    String target = strings[1];
                    String challenge = strings[2];
                    if(!challenge.equalsIgnoreCase("bedrockwall") && !challenge.equalsIgnoreCase("forcebattle")) {
                        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.RED + "Challenge not found");
                        return false;
                    }
                    int amount = 1;
                    if(strings.length >= 4) {
                        try {
                            amount = Integer.parseInt(strings[3]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if(amount <= 0){
                        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.RED + "Invalid number");
                        return false;
                    }
                    if (target.equals("@a")) {
                        int playerAmount = 0;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            giveJoker(player, amount, challenge);
                            playerAmount++;
                        }
                        if (playerAmount <= 1)
                            for(Player player : Bukkit.getOnlinePlayers()) {
                                if (amount <= 1)
                                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Gave " + amount + " joker to " + player.getName());
                                else
                                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Gave " + amount + " jokers to " + player.getName());
                            }
                        else if (amount <= 1)
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Gave " + amount + " joker to " + playerAmount + " players");
                        else
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Gave " + amount + " jokers to " + playerAmount + " players");
                        return true;
                    } else if (target.equals("@s")) {
                        if (!(commandSender instanceof Player player)) {
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "SurvivalChallenges" + ChatColor.GRAY + "] " + ChatColor.RED + "This command can only be used by players");
                            return false;
                        }
                        if (amount <= 1)
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Gave " + amount + " joker to " + player.getName());
                        else
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Gave " + amount + " jokers to " + player.getName());

                        giveJoker(player, amount, challenge);
                        return true;
                    }
                    else {
                        Player player = Bukkit.getPlayer(target);
                        if (player == null) {
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.RED + "Player not found");
                            return false;
                        }
                        if (amount <= 1)
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Gave " + amount + " joker to " + player.getName());
                        else
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Gave " + amount + " jokers to " + player.getName());
                        giveJoker(player, amount, challenge);
                        return true;
                    }
                }
                else {
                    sendUsage(commandSender);
                    return false;
                }
            }
            case "remove":{
                if(strings.length >= 3) {
                    String target = strings[1];
                    String challenge = strings[2];
                    if(!challenge.equalsIgnoreCase("bedrockwall") && !challenge.equalsIgnoreCase("forcebattle")) {
                        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.RED + "Challenge not found");
                        return false;
                    }
                    int amount = 1;
                    if(strings.length >= 4) {
                        try {
                            amount = Integer.parseInt(strings[3]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if(amount <= 0){
                        commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.RED + "Invalid number");
                        return false;
                    }
                    if (target.equals("@a")) {
                        int playerAmount = 0;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            removeJoker(player, amount, challenge);
                            playerAmount++;
                        }
                        if (playerAmount <= 1)
                            for(Player player : Bukkit.getOnlinePlayers()) {
                                if (amount <= 1)
                                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Removed " + amount + " joker from " + player.getName());
                                else
                                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Removed " + amount + " jokers from " + player.getName());
                            }
                        else if (amount <= 1)
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Removed " + amount + " joker from " + playerAmount + " players");
                        else
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Removed " + amount + " jokers from " + playerAmount + " players");
                        return true;
                    } else if (target.equals("@s")) {
                        if (!(commandSender instanceof Player player)) {
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "SurvivalChallenges" + ChatColor.GRAY + "] " + ChatColor.RED + "This command can only be used by players");
                            return false;
                        }
                        if (amount <= 1)
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Removed " + amount + " joker from " + player.getName());
                        else
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Removed " + amount + " jokers from " + player.getName());
                        removeJoker(player, amount, challenge);
                        return true;
                    }
                    else {
                        Player player = Bukkit.getPlayer(target);
                        if (player == null) {
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.RED + "Player not found");
                            return false;
                        }
                        if (amount <= 1)
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Removed " + amount + " joker from " + player.getName());
                        else
                            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Removed " + amount + " jokers from " + player.getName());
                        removeJoker(player, amount, challenge);
                        return true;
                    }
                }
                else {
                    sendUsage(commandSender);
                    return false;
                }
            }

            default:{
                sendUsage(commandSender);
                return false;
            }
        }
    }

    private void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Joker" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Usage: " + ChatColor.GOLD + "/joker <give | remove> <Player> <Challenge> <Amount>");
    }

    private void giveJoker(Player player, Integer amount, String challenge){
        Material material = Material.BARRIER;
        String name = "";
        if(challenge.equalsIgnoreCase("bedrockwall")){
            material = Material.BEDROCK;
            name = ChatColor.RED + "Joker [BedrockWall]";
        }
        if(challenge.equalsIgnoreCase("forcebattle")){
            material = Material.BARRIER;
            name = ChatColor.RED + "Joker [ForceBattle]";
        }
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (meta.hasLore())
                meta.setLore(null);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        player.getInventory().addItem(item);
    }

    private void removeJoker(Player player, int amountToRemove, String challenge) {
        if (!challenge.equalsIgnoreCase("bedrockwall") && !challenge.equalsIgnoreCase("forcebattle")) return;
        int remaining = amountToRemove;
        PlayerInventory inv = player.getInventory();
        ItemStack[] storage = inv.getStorageContents();
        remaining = removeFromArray(storage, remaining);
        inv.setStorageContents(storage);
        if (remaining <= 0) return;
        ItemStack[] armor = inv.getArmorContents();
        remaining = removeFromArray(armor, remaining);
        inv.setArmorContents(armor);
        if (remaining <= 0) return;
        ItemStack offhand = inv.getItemInOffHand();
        if (isJoker(offhand)) {
            int amount = offhand.getAmount();
            if (amount <= remaining)
                inv.setItemInOffHand(null);
            else
                offhand.setAmount(amount - remaining);
        }
    }

    private int removeFromArray(ItemStack[] contents, int remaining) {
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (!isJoker(item)) continue;
            int amount = item.getAmount();
            if (amount <= remaining) {
                contents[i] = null;
                remaining -= amount;
            } else {
                item.setAmount(amount - remaining);
                return 0;
            }
            if (remaining <= 0) return 0;
        }
        return remaining;
    }

    private boolean isJoker(ItemStack item) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;
        if (!meta.hasDisplayName()) return false;
        return (meta.getDisplayName().equals(ChatColor.RED + "Joker [BedrockWall]") || meta.getDisplayName().equals(ChatColor.RED + "Joker [ForceBattle]"));
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("joker")) {
            if (strings.length == 1) {
                List<String> subcommands = List.of("give", "remove");
                for(String string : subcommands)
                    if(string.toLowerCase().startsWith(strings[0].toLowerCase()))
                        completions.add(string);
            }
            if(strings.length==2&&(strings[0].equalsIgnoreCase("give") || strings[0].equalsIgnoreCase("remove"))){
                List<String> subcommands = new ArrayList<>(List.of("@a", "@s"));
                for(Player player : Bukkit.getOnlinePlayers())
                    subcommands.add(player.getName());
                for(String string : subcommands)
                    if(string.toLowerCase().startsWith(strings[1].toLowerCase()))
                        completions.add(string);
            }
            if (strings.length == 3) {
                List<String> subcommands = List.of("BedrockWall", "ForceBattle");
                for(String string : subcommands)
                    if(string.toLowerCase().startsWith(strings[2].toLowerCase()))
                        completions.add(string);
            }
        }
        return completions;
    }
}